(ns analysis.web-page-test
  (:require [hiccup.core :refer [html]]
            [hickory.core :refer [parse as-hiccup]]
            [clj-http.client :refer [get]]
            [clojure.spec.alpha :as s]
            [analysis.csv :as csv]))

;; structures .....................
(def ^:dynamic objective-values {:load-time "LoadTime"
                                 :first-byte "TTFB"
                                 :start-render "StartRender"
                                 :visual-complete "visualComplete"
                                 :speed-index "SpeedIndex"
                                 :fst-interactive "FirstInteractive"
                                 :doc-complete "DocComplete"
                                 :fully-loaded "FullyLoaded"
                                 :bytes-in "BytesIn"})

(def ^:dynamic runs [1 2 3])

(s/def ::env (s/and vector?  (s/coll-of (s/tuple string? string?))))
(s/def ::unit string?)
(s/def ::values (s/and vector? (s/coll-of number?)))
(s/def ::objective (-> objective-values keys set))
(s/def ::browser (s/or :name string? :type #{"Firefox" "Chrome" "IE11"}))

(s/def ::agg-result (s/keys :req [::env ::browser ::objective ::values ::unit]))

;; utilities .....................
;; e.g. url: https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/

;;TODO: move this to some external lib
(defn- third [coll]
  (nth coll 2))

(defn read-links [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (-> rdr line-seq vec)))

(defn wget-details [url run]
  (get (str url run "/details/")))

(defn lookup-objective-pattern [objective]
  (html [:td {:id (objective-values objective)} "*.*"]))

(defn lookup-location-pattern
  ([] (lookup-location-pattern "From:"))
  ([attr-value]
   (str (html [:strong attr-value]) "*.*")))


(defn extract-location [re-find-seq]
  (clojure.string/replace (->> re-find-seq
                               parse
                               as-hiccup
                               flatten
                               (filter string?)
                               rest
                               (apply str))
                          "," " "))

(defn extract-browser [environment-str]
  (second (clojure.string/split environment-str #"-")))

(defn sanitize [string-value]
  {:value (->> (clojure.string/replace string-value "," ".")
               (re-find #"\d+\.??\d+")
               (read-string))
   :unit (re-find #"[a-zA-Z]+" string-value)})

;; extractors .....................
(defn extract-pattern
  ([url run objective lookup-fn]
   ;(prn url)
   (extract-pattern ((wget-details url run) :body)
                    objective
                    lookup-fn))
  
  ([page-body objective lookup-fn]        
   (-> objective
       lookup-fn
       re-pattern
       (re-find page-body))))

(defn extract-value
  ([url run objective lookup-fn]
   (extract-value (extract-pattern url run objective lookup-fn) objective))
  
  ([url run objective]
   (extract-value (extract-pattern url run objective lookup-objective-pattern) objective))

  ([str-pattern objective]
   ;;(prn str-pattern)
   ;; TODO: solve this "if" hack - is nonsense
   [objective (if (keyword? objective)
                (do ;(prn str-pattern)
                    (or (re-find #"\d+,\d+\s*.*B" str-pattern)
                        (re-find #"\d+\.??\d+\s*\w*" str-pattern)))
                (extract-location str-pattern))]))

;; e.g. usage: (analysis.web-page-test/extract-value "https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/" 1 :bytes-in)


;; aggregators .....................
(defn agg-basic-result [url run objective]
  (let [location (extract-value url run "From:" lookup-location-pattern)
        browser (-> location second extract-browser)
        [value unit] ((juxt :value :unit) (sanitize (second (extract-value url run objective))))]
    {::env location ::browser browser ::objective objective ::values value ::unit unit}))


(def extract-value! (memoize extract-value))
(def sanitize! (memoize sanitize))

(defn agg-basic-result [urls run objective]
  (loop [URLs urls
         locations (transient [])
         browser (transient [])
         values (transient [])
         units (transient [])]
    (if (-> URLs seq not)
      {::env (persistent! locations)
       ::browser (persistent! browser)
       ::values (persistent! values)
       ::unit (persistent! units)
       ::objective objective}
      (recur (rest URLs)
             (conj! locations (extract-value! (first URLs) run "From:" lookup-location-pattern))
             (conj! browser (-> (extract-value! (first URLs) run "From:" lookup-location-pattern)
                                second extract-browser))
             (conj! values (-> (extract-value! (first URLs) run objective) second sanitize :value))
             (conj! units (-> (extract-value! (first URLs) run objective) second sanitize :unit))))))

(defn extract-sanitize-val [url run objective]
  ;;(prn (extract-value! url run objective))
  (-> (extract-value! url run objective) second sanitize :value))

(defn agg-mean-result [urls runs objective]
  (loop [URLs urls
         run (first runs)
         locations (transient [])
         browser (transient [])
         values (transient [])
         units (transient [])]
    (if (-> URLs seq) (prn (extract-value! (first URLs) run "From:" lookup-location-pattern)))
    (if (-> URLs seq not)
      {::env (persistent! locations)
       ::browser (persistent! browser)
       ::values (persistent! values)
       ::unit (persistent! units)
       ::objective objective}
      (recur (rest URLs) run
             (conj! locations (second (extract-value! (first URLs) run "From:" lookup-location-pattern)))
             (conj! browser (-> (extract-value! (first URLs) run "From:" lookup-location-pattern)
                                second extract-browser))
             (conj! values (/ (apply + (map #(extract-sanitize-val (first URLs) % objective) runs)) (count runs)))
             (conj! units (-> (extract-value! (first URLs) run objective) second sanitize :unit))))))

;; application .....................
(defn single-objective-app [urls-file objective]
  (agg-mean-result (read-links urls-file) runs objective))

(defn multi-objective-app [urls-file]
  (map #(vector % (single-objective-app urls-file %)) (keys (dissoc objective-values :fst-interactive))))

(defn multi-write-app [urls-file]
  (let [file-root-name (first (clojure.string/split urls-file #"\."))]
    (prn file-root-name)
    (map #(csv/write-csv (second %) (str file-root-name "-" (-> % first name) ".csv"))
         (multi-objective-app urls-file))))

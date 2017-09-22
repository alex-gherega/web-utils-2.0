(ns analysis.web-page-test
  (:require [hiccup.core :refer [html]]
            [hickory.core :refer [parse as-hiccup]]
            [clj-http.client :refer [get]]
            [clojure.spec.alpha :as s]))

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

(s/def ::env (s/and vector?  (s/coll-of (s/tuple string? string?))))
(s/def ::unit string?)
(s/def ::values (s/and vector? (s/coll-of number?)))
(s/def ::objective (-> objective-values keys set))
(s/def ::browser (s/or :name string? :type #{"Firefox" "Chrome" "IE11"}))
(s/def ::aggregate-result (s/keys :req [::env ::browser ::objective ::values ::unit]))

;; utilities .....................
;; e.g. url: https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/
(defn wget-details [url run]
  (get (str url run "/details/")))

(defn lookup-objective-pattern [objective]
  (html [:td {:id (objective-values objective)} "*.*"]))

(defn lookup-location-pattern
  ([] (lookup-location-pattern "From:"))
  ([attr-value]
   (str (html [:strong attr-value]) "*.*")))


(defn extract-location [re-find-seq]
  (->> re-find-seq
       parse
       as-hiccup
       flatten
       (filter string?)
       rest
       (apply str)))

(defn extract-browser [environment-str]
  (second (clojure.string/split environment-str #"-")))

(defn sanitize [string-value]
  {:value (->> (clojure.string/replace string-value "," ".")
               (re-find #"\d\.??\d+")
               (read-string))
   :unit (re-find #"[a-zA-Z]+" string-value)})

;; extractors .....................
(defn extract-pattern
  ([url run objective lookup-fn]
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
   (prn str-pattern)
   [objective (or (re-find #"\d,\d+\s*.*B" str-pattern)
                  (re-find #"\d\.??\d+\s*\w*" str-pattern)
                  (extract-location str-pattern))]))

;; e.g. usage: (analysis.web-page-test/extract-value "https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/" 1 :bytes-in)


;; aggregators .....................
(defn agg-basic-result [url run objective]
  (let [location (extract-value url run "From:" lookup-location-pattern)
        browser (-> location second extract-browser)
        ]))

(ns analysis.web-page-test
  (:require [hiccup.core :refer [html]]
            [hickory.core :refer [parse as-hiccup]]
            [clj-http.client :refer [get]]))

(def ^:dynamic objective-values {:load-time "LoadTime"
                                 :first-byte "TTFB"
                                 :start-render "StartRender"
                                 :visual-complete "visualComplete"
                                 :speed-index "SpeedIndex"
                                 :fst-interactive "FirstInteractive"
                                 :doc-complete "DocComplete"
                                 :fully-loaded "FullyLoaded"
                                 :bytes-in "BytesIn"})

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
      (apply str)))

;; extractors .....................
(defn extract-pattern
  ([url run objective lookup-fn]
   (extract-pattern ((wget-details url run) :body)
                    objective
                    lookup-fn))
  
  ([page-body objective lookup-fn]
   (prn (lookup-fn objective))
   (-> objective
       lookup-fn
       re-pattern
       (re-find page-body))))

(defn extract-value
  ([url run objective lookup-fn]
   (extract-value (extract-pattern url run objective lookup-fn) objective))
  
  ([url run objective]
   (extract-value (extract-pattern url run objective lookup-location-pattern) objective))

  ([str-pattern objective]
   [objective (or (re-find #"\d,\d+\s*.*B" str-pattern)
                  (re-find #"\d\.*\d+\s*\w*" str-pattern)
                  (extract-location str-pattern))]))

;; e.g. usage: (analysis.web-page-test/extract-value "https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/" 1 :bytes-in)

;; aggregators .....................

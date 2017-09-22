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

;; e.g. url: https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/
(defn wget-details [url run]
  (get (str url run "/details/")))

(defn- lookup-pattern [objective]
  (html [:td {:id (objective-values objective)} "*.*"]))

(defn sanitize [string-value]
  {:value (->> (clojure.string/replace string-value "," ".")
               (re-find #"\d\.??\d+")
               (read-string))
   :unit (re-find #"[a-zA-Z]+" string-value)})

(defn extract-pattern

  ([url run objective]
   (extract-pattern ((wget-details url run) :body)
                    objective))

  ([page-body objective]
   (-> objective
       lookup-pattern
       re-pattern
       (re-find page-body))))

(defn extract-value
  ([url run objective]
   (extract-value (extract-pattern url run objective) objective))

  ([str-pattern objective]
   [objective (or (re-find #"\d,\d+\s*.*B" str-pattern)
                  (re-find #"\d\.??\d+\s*\w*" str-pattern))]))

;; e.g. usage: (analysis.web-page-test/extract-value "https://www.webpagetest.org/result/170920_EE_90884ce83af9264e0604cdcf28b625d9/" 1 :bytes-in)

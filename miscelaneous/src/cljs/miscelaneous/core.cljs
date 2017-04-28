(ns miscelaneous.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [reagent.core :as r]
            [cljsjs.material]))

(defn comp-path
  ([src fname]
   (str src fname))

  ([fname]
   (str "images/" fname)))

(defn cells-cols [phone tablet desktop]
  {:phone phone
   :tablet tablet
   :desktop desktop})

(defn get-js-el [id]
  (.getElementById js/document id))

(defn get-js-cls [classname]
  (.getElementsByClass js/document classname))

(defn get-js-prop [id prop-name]
  (let [element (get-js-el id)]
    (aget element prop-name)))

(defn set-js-prop [id prop prop-value]
  (set! (.. (get-js-el id) -prop) prop-value))

(defn html! [cljh]
  (html cljh))

(defn insert-html [id h]
  (let [content (.createElement js/document "div")
        elemnt (get-js-el id)]
    ;(set! (.. content -innerHTML) h)
    ;(.appendChild elemnt content)
    (set! (.. elemnt -innerHTML) h)))

;; TODO: the following two functions are the same; set a param and use them
(defn hide-block [id]
  (set! (.. (get-js-el id) -style -display) "none"))

(defn hide-element [id]
  (set! (.. (get-js-el id) -style -visibility) "hidden"))

(defn show-block [id]
  (set! (.. (get-js-el id) -style -display) "block"))

(defn show-element [id]
  (set! (.. (get-js-el id) -style -visibility) "visible"))

;; TODO: use this everywhere;
;; TODO: use imrpoved version from ping
;; TODO: write one that can slo add style elements
(defn extend-class [div class & classes]
  (let [class (name class)
        classes (map #(->> % name (str ".")) classes)]
    (keyword (apply str (name div) "." class classes))))

(defn switch [v1 v2 id]
  (let []))

(defn show-modal [callback-fn]
  (let [dialog (.querySelector js/document "dialog")
        ;show-modal-button (.querySelector js/document (str "#" button-id))
        ]
    ;; (if (not (.showModal dialog))
    ;;   (.registerDialog js/dialogPolyfill dialog))
    ;; (.addEventListener show-modal-button
    ;;                    "click")
    (.showModal dialog)
    (-> dialog
        (.querySelector ".close")
        (.addEventListener "click" #(do (.close dialog) (callback-fn))))
    ))

(defn validate-email [text]
  (re-matches #"^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[A-Za-z]{2,6}$" text))

(defn is-checked [id]
  (.-checked (get-js-el id)))

(defn assemble-opts [opts]
  (if-let [spaces (map (fn [_] " ") opts)]
    (interleave spaces
                opts)))

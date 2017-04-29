(ns mdl-widgetry.cards
  (:require [clojure.string :as cstring]
            [cljsjs.material]
            [reagent.core :as r]
            [miscelaneous.core :as misc]))


(defn link [link & classes]
  [(apply misc/enrich-class
          :a.mdl-button.mdl-js-button.mdl-typography--text-upper-case
          classes)
    {:href link
     :target "_blank"}])

(defn media [content & classes]
  [(apply misc/enrich-class
          :div.mdl-card__media
          classes)
   content])

(defn title [title & subtitle]
  [:div.mdl-card__title {:style {:display "block"}}
   [:h4.mdl-card__title-text title]
   (if subtitle [:p (first subtitle)])])

(defn supp-text [text]
  [:div.mdl-card__supporting-text
   [:span.mdl-typography--font-light.mdl-typography--subhead
    text]])

(defn actions [msg card-link & classes]
  [(apply misc/enrich-class :div.mdl-card__actions classes)
   (conj card-link
         msg
         [:i.material-icons "chevron_right"])])

(defn cell [no-cels]
   (let [{pno :phone
          tno :tablet
          dno :desktop} no-cels
         mdl-cel "mdl-cell--"
         ccls-fn #(str mdl-cel %1 %2)]
     [(misc/enrich-class :div.mdl-cell
                     (ccls-fn dno "-col")
                     (ccls-fn tno "-col-tablet")
                     (ccls-fn pno "-col-phone")
                     :mdl-card
                     :mdl-shadow--3dp)]))

(defn coerce [k v]
  (let [apply-fn #(apply % v)]
    (condp = k
      :title (apply-fn title)
      :text (apply-fn supp-text)
      :media (apply-fn media)
      :actions (apply-fn actions))))

(defn card [inputm no-cels & orderv]
  (let [orderv (or orderv (keys inputm))]
    (apply conj
           (cell no-cels)
           (keep (fn [k] (coerce k (k inputm)))
                 orderv))))


(defn card! [no-cels & kvs]
  {:pre [(if (-> kvs count even?)
           true
           (assert (-> kvs count even?) "Need even number of key-values as arguments"))]}
  (let [input (vec kvs)
        inputm (apply assoc {} input)
        orderv (keep-indexed (fn [index item]
                               (if (odd? index) item nil))
                             input)]
    (apply card inputm no-cels orderv)))


(defn make-grid [classes content & contents]

  ;; TODO: refactor this to use enrich-classes from web-utils-2.0/miscelaneous
  (let [div-cls (keyword (str "div." (name classes) ".mdl-grid"))]
    (reduce conj [div-cls content] contents)))


;; TODO: change local make-grid for html/make-grid
(defn cards [classes card & cards]
  (apply make-grid classes card cards)) ;; where card is obtained from card or card!


;;.................................... SPECIFIC CARDS
;;
;;  Once can use these examples to workout
;;  how to use the cards API
;;
;; .................................................


;; picture  cards:

;; (defn picture-card
;;   ([img [pno tno dno]]
;;    (picture-card img img ""))
;;   ([img id [pno tno dno]]
;;    (picture-card img id ""))
;;   ([img id link [pno tno dno]]
;;    (picture-card img id link ""))
;;   ([img id link text [pno tno dno]]
;;    (picture-card img id link text "" [pno tno dno]))
;;   ([img id link text msg [pno tno dno]]
;;     (picture-card img id link text msg "Details" [pno tno dno]))
;;   ([img id link text msg action [pno tno dno]]
;;    (c/cell-of-cards text msg [img id] [action link] (mutils/cells-cols pno tno dno))))


;; ;; TODO: refactor this;; baaad baaad code
;; (defn picture-card!!
;;   ([img [pno tno dno]]
;;    (picture-card!! img img [pno tno dno]))

;;   ([img id [pno tno dno]]
;;    (picture-card!! img id "" [pno tno dno]))
  
;;   ([img id link [pno tno dno]]
;;    (picture-card!! img id link "" [pno tno dno]))
  
;;   ([img id link text [pno tno dno]]
;;    (picture-card!! img id link text "" [pno tno dno]))
  
;;   ([img id link text subtext [pno tno dno]]
;;    (picture-card!! img id link text subtext "" [pno tno dno]))
  
;;   ([img id link text subtext msg [pno tno dno]]
;;    (picture-card!! img id link text subtext msg "Details" [pno tno dno]))
  
;;   ([img id link text subtext msg action [pno tno dno]]
;;    (c/cell-of-cards!! text subtext msg [img id] [action link] (mutils/cells-cols pno tno dno))))

;; text cards
(defn text-card
  ([title text [pno tno dno]]
   (card! :title title :text text (misc/cells-cols pno tno dno)))
  ([title text [msg link] [pno tno dno]]
   ;; here link is obtained by the cards/link function
   (card! :title title :text text
          :actions [msg link] (misc/cells-cols pno tno dno))))


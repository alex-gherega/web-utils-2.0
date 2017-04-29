(ns mdl-widgetry.cards
  (:require [clojure.string :as cstring]
            [cljsjs.material]
            [reagent.core :as r]
            [miscelaneous.core :as misc]
            [clojure.browser.repl :as repl]))

(defn basic-link-attrs [fn] {:onClick fn})

;; TODO: link & media & actions have the perfect struct for a macro
(defn ^:dynamic link [{:keys [link attrs]} & classes]  
  [(apply misc/enrich-class
          :a.mdl-button.mdl-js-button.mdl-typography--text-upper-case
          classes)
   (into {:href link :target "_blank"} attrs)])

(defn ^:dynamic media [content & classes]
  [(apply misc/enrich-class
          :div.mdl-card__media
          classes)
   content])

(defn ^:dynamic title [title & subtitle]
  [:div.mdl-card__title {:style {:display "block"}}
   [:h4.mdl-card__title-text title]
   (if subtitle [:p (first subtitle)])])

(defn ^:dynamic supp-text [text]
  [:div.mdl-card__supporting-text
   [:span.mdl-typography--font-light.mdl-typography--subhead
    text]])

(defn ^:dynamic actions [msg card-link & classes]
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
  (let [apply-fn #(if (coll? v) (apply % v) (% v))] ;; TODO: move this to miscelaneous
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
                               (if (even? index) item nil))
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


(defn picture-full [media
                    [title subtitle] text
                    [action-name link]
                    [pno tno dno]]
  "VIN: (Very Important Note): media needs to be a collection of whatever markup you want inside the div; e.g. [[:img ...]] or [[your-markup] :your-class1 :your-class2...]"
  (card! (misc/cells-cols pno tno dno)
         :media media 
         :title [title subtitle]
         :text text
         :actions [action-name link]))

(defn picture-only [img [pno tno dno] & classes]
  (picture-full [img img]
                ["" ""] 
                ""
                ["Details" (apply link {:link "" :attrs {}} classes)]
                [pno tno dno]))


;; text cards

(defn text-card
  ([title text [pno tno dno]]
   (card! (misc/cells-cols pno tno dno) :title title :text text))
  ([title text [msg link] [pno tno dno]]
   ;; here link is obtained by the mdl-widgetry.cards/link function
   (card! (misc/cells-cols pno tno dno)
          :title title :text text
          :actions [msg link])))


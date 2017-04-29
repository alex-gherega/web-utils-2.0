(ns mdl-widgetry.cards
  (:require [clojure.string :as cstring]
            [cljsjs.material]
            [reagent.core :as r]
            [miscelaneous.core :as misc]))


(defn card-link [link & classes]
  [(apply misc/enrich-class
          :a.mdl-button.mdl-js-button.mdl-typography--text-upper-case
          classes)
    {:href link
     :target "_blank"}])

(defn card-media [content & classes]
  [(apply misc/enrich-class
          :div.mdl-card__media
          classes)
   content])

(defn card-title [title & subtitle]
  [:div.mdl-card__title {:style {:display "block"}}
   [:h4.mdl-card__title-text title]
   (if subtitle [:p (first subtitle)])])

(defn card-supp-text [text]
  [:div.mdl-card__supporting-text
   [:span.mdl-typography--font-light.mdl-typography--subhead
    text]])

(defn card-actions [msg card-link & classes]
  [:div.mdl-card__actions
   (conj card-link
         msg
         [:i.material-icons "chevron_right"])])

(defn find-a-name [no-cels] ;; TODO:
   (let [{pno :phone
          tno :tablet
          dno :desktop} no-cels
         mdl-cel "mdl-cell--"
         ccls-fn #(str mdl-cel %1 %2)]
     (js/alert (= (enrich-class :div.mdl-cell
                                (ccls-fn dno "-col")
                                (ccls-fn tno "-col-tablet")
                                (ccls-fn pno "-col-phone")
                                :mdl-card
                                :mdl-shadow--3dp)
                  (keyword (str "div.mdl-cell." (ccls-fn dno "-col.")
                    (ccls-fn tno "-col-tablet.")
                    (ccls-fn pno "-col-phone.")
                    "mdl-card."
                    "mdl-shadow--3dp"))))
     
     [(keyword (str "div.mdl-cell." (ccls-fn dno "-col.")
                    (ccls-fn tno "-col-tablet.")
                    (ccls-fn pno "-col-phone.")
                    "mdl-card."
                    "mdl-shadow--3dp"))]))
(defn cell-of-cards
  ;; no-cels is a map: {:phone #number :tablet #number :desktop #number}
  ([title text [img id] [msg link] no-cels]
   (conj (cell-of-cards title text [img id] no-cels)
         (card-actions msg link)))
  
  ([title text [img id] no-cels]
   (conj (cell-of-cards no-cels)
         (card-media img id)
         (card-title title)
         (card-supp-text text)))
  
  ([title text no-cels]
   (conj (cell-of-cards no-cels)
         (card-title title)
         (card-supp-text text)))
  
  ([no-cels]
   (let [{pno :phone
          tno :tablet
          dno :desktop} no-cels
         mdl-cel "mdl-cell--"
         ccls-fn #(str mdl-cel %1 %2)]
     [(keyword (str "div.mdl-cell." (ccls-fn dno "-col.")
                    (ccls-fn tno "-col-tablet.")
                    (ccls-fn pno "-col-phone.")
                    "mdl-card."
                    "mdl-shadow--3dp"))])))

(defn cell-of-cards!
  ;; no-cels is a map: {:phone #number :tablet #number :desktop #number}
  ([title text [img id] [msg link] no-cels]
   (conj (cell-of-cards title text [img id] no-cels)
         (card-actions msg link)))
  
  ([title text [msg link] no-cels]
   (conj (cell-of-cards no-cels)
         (card-title title)
         (card-supp-text text)
         (card-actions msg link))))

(defn cell-of-cards!!
  ;; no-cels is a map: {:phone #number :tablet #number :desktop #number}
  ([title subtitle text [img id] [msg link] no-cels]
   (conj (cell-of-cards!! title subtitle text [img id] no-cels)
         (card-actions msg link)))
  
  ([title subtitle text [img id] no-cels]
   (conj (cell-of-cards no-cels)
         (card-media img id)
         (card-title title subtitle)
         (card-supp-text text)))
  
  ([title subtitle text no-cels]
   (conj (cell-of-cards no-cels)
         (card-title title subtitle)
         (card-supp-text text))))

(defn card-ex [id]
  (cell-of-cards "Foods"; "Get Going on Androi"
                  ""; "Four tips to make your switch to Android quick and easy"
                  ["pexels-photo-207247.jpeg" id]
                                        ;["Make the switch" "http://www.google.com"]
                  ["Details" ""]
                  (misc/cells-cols 2 2 2)))

(defn cards
  ([] (cards (card-ex "1") (card-ex "2") (card-ex "3") (card-ex "4")
                  ))
  ([card & cards]
   (apply hutils/make-oric-grid :oric-card-container card cards)))

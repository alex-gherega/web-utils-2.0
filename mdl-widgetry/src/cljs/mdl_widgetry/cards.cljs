(ns mdl-widgetry.cards
  (:require [clojure.string :as cstring]
            [cljsjs.material]
            [reagent.core :as r]))

(defn card-media [img id]
  [:div.mdl-card__media
   [:object.mdl-card__media {:data ""
                             :type "image/jpg"}]])

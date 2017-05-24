(ns mdl-widgetry.dialog
  (:require [cljsjs.material]
            [miscelaneous.core :as misc]))

(def bg-style {:display "none"
               :position "fixed"
               :z-index "1"
               :left "0"
               :top "0"
               :width "100%"
               :height "100%"
               :overflow "auto"
               :background-color "rgba(0,0,0,0.4)"})

(def default-action [{:action-name "Ok"
                      :action-fn identity}])

(defn show-dialog[id]
  (misc/show-block id))

(defn close-dialog [id]
  (misc/hide-block id))

(defn- dialog-button [id {:keys [action-name action-fn]}]
  [:a.mdl-button.mdl-js-button
   {:key action-name
    :onClick #(do
                (action-fn)
                (close-dialog id))}
   action-name])

(defn- dialog-actions [id actions]
   (map #(dialog-button id %) (or actions default-action)))

(defn build-dialog [id content actions & classes]
  "Actions is a vector of maps [{:action-name Cancel :action-fn identity}].
   If missing, the default action which closes the dialog will be called."
  [:div.page-content
   {:id id
    :style bg-style}
   [(apply misc/enrich-class :div classes)
    [:div content]
    [:div
     (dialog-actions id actions)]]])

(defn confirmation-content [title msg]
  [:div
   [:h4.mdl-dialog__title title]
   [:div.mdl-dialog__content msg]])

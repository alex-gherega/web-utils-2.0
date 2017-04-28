;;; This namespace is used for testing purpose. It use the
;;; clojurescript.test lib.
(ns mdl-widgetry.core-test
  (:require-macros [cemerick.cljs.test :as m :refer (deftest is)]
                   [mdl-widgetry.core :as mdwl])
  (:require [cemerick.cljs.test :as t]
            [mdl-widgetry.cards :as mdlw-cards]))

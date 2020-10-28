(ns todolist-app.core
  (:require [reagent.dom :as reagent-dom]
            [re-frame.core :as re-frame]
            [todolist-app.login :as login]))

(enable-console-print!)


(defn mount-root
  []
  (println "Trying to mount")
  ;; (re-frame/clear-subscription-cache!)
  (reagent-dom/render [login/main-panel]
                      (.getElementById js/document "app")))


(defn ^:export render
  []
  ;; (re-frame/dispatch-sync [:initialize-db])
  (mount-root))


(defn on-js-reload [])

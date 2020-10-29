(ns todolist-app.core
  (:require [reagent.dom :as reagent-dom]
            [reagent.core :as r]
            [re-frame.core :as re-frame]
            [secretary.core :as secretary :refer-macros [defroute]]
            [accountant.core :as accountant]
            [todolist-app.login :as login]
            [todolist-app.user :as user]))

(enable-console-print!)

(def selected-page (r/atom login/page))


;;https://yogthos.net/posts/2014-08-14-Routing-With-Secretary.html
;; Routes
(secretary/defroute "/login" []
  (reset! selected-page login/page))


(secretary/defroute "/user/:id" {:as params}
  (js/console.log (:id params))
  (reset! selected-page (fn [] [user/page (:id params)])))


(defn page []
  [@selected-page])


(defn mount-root
  []
  (println "Trying to mount")
  ;; (re-frame/clear-subscription-cache!)
  (reagent-dom/render [page]
                      (.getElementById js/document "app")))

(defn ^:export render
  []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))


(defn on-js-reload [])

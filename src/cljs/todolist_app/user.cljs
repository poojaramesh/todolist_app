(ns todolist-app.user
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [ajax.core :refer [POST GET]]
            [cljs.reader :refer [read-string]]
            [cljsjs.semantic-ui-react :as ui]))


(def user-data* (r/atom {}))

(defn page
  [id]
  (GET "/data" {:params {:method "user-todolist"
                         :user/resource-id id}
                :handler #(do
                            (reset! user-data* (read-string %)))})
  (fn [id]
    [:div.user
     [:p (str "Test " id)]]))

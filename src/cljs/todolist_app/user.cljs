(ns todolist-app.user
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [ajax.core :refer [POST GET]]
            [cljs.reader :refer [read-string]]
            [cljsjs.semantic-ui-react :as ui]
            [accountant.core :as accountant]))


(def user-data* (r/atom {}))


(defn update-item
  [todo-item-resource-id complete?]
  (POST "/data" {:params {:method "update-complete?"
                          :resource-id todo-item-resource-id
                          :complete? complete?}
                 :handler #(do
                             (swap! user-data* assoc todo-item-resource-id (read-string %)))}))


(defn delete-item
  [todo-item-resource-id complete?]
  (POST "/data" {:params {:method "delete-todo-item"
                          :resource-id todo-item-resource-id}
                 :handler #()}))


(defn page
  [user-resource-id]
  (GET "/data" {:params {:method "user-todolist"
                         :resource-id user-resource-id}
                :handler #(do
                            (reset! user-data* (read-string %)))})
  (fn [user-resource-id]
    (println "user-data: " @user-data*)
    (if-not (empty? @user-data*)
      [:div.user
       [:> ui/Grid {:divided :vertically}
        [:> ui/Grid.Row {:columns 1}
         [:> ui/Grid.Column {:text-align :right}
          [:> ui/Button {:size :tiny :basic true
                         :on-click (fn [_]
                            (reset! user-data* {})
                            (accountant/navigate! "/login"))} "Logout"]]]
        [:> ui/Grid.Row {:columns 2}
         [:> ui/Grid.Column {:text-align :center}
          [:> ui/Card {:fluid true :centered true}
           [:> ui/Card.Header "Complete vs. Incomplete Tasks"]]]
         [:> ui/Grid.Column {:text-align :center}
          [:> ui/Card {:fluid true :centered true}
           [:> ui/Card.Header "Unfinished tasks over time"]]]]
        [:> ui/Grid.Row {:columns 1}
         [:> ui/Grid.Column
          [:> ui/Table
           [:> ui/Table.Header
            [:> ui/Table.Row
             [:> ui/Table.Cell {:text-align :left}
              [:> ui/Header "To-Do Items:"]]
             [:> ui/Table.Cell {:text-align :right}
              [:> ui/Button
               {:size :tiny :basic true}
               "Add Item"]]]]
           (into [:> ui/Table.Body]
                 (map-indexed (fn [i [id {:todo-item/keys [task complete?] :as item}]]
                                ^{:key i}
                                [:> ui/Table.Row
                                 [:> ui/Table.Cell {:text-align :left}
                                  [:> ui/Checkbox {:label task
                                                   :checked complete?
                                                   :on-change (fn [_]
                                                                (swap! user-data* update-in [id :todo-item/complete?] not)
                                                                (update-todo-item! id (not complete?)))}]]
                                 [:> ui/Table.Cell {:text-align :right}
                                  [:> ui/Button {:icon :trash
                                                 :size :tiny
                                                 :on-click (fn [_]
                                                             (swap! user-data* dissoc id)
                                                             (delete-todo-item! id))}]]])
                              @user-data*))]]]]]
      [:div "Loading..."])))

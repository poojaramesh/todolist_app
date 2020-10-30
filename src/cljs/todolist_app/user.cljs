(ns todolist-app.user
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [ajax.core :refer [POST GET]]
            [cljs.reader :refer [read-string]]
            [cljsjs.semantic-ui-react :as ui]
            [accountant.core :as accountant]
            [todolist-app.plots :as plots]))


(def user-data* (r/atom {}))
(def edit-mode* (r/atom {:edit-mode? false
                         :new-task nil}))


(defn update-todo-item
  [todo-item-resource-id complete?]
  (POST "/data" {:params {:method "update-complete?"
                          :resource-id todo-item-resource-id
                          :complete? complete?}
                 :format :raw
                 :handler #(swap! user-data* assoc todo-item-resource-id (read-string %))}))


(defn delete-todo-item!
  [todo-item-resource-id]
  (POST "/data" {:params {:method "delete-todo-item"
                          :resource-id todo-item-resource-id}
                 :format :raw
                 :handler #()}))


(defn add-todo-item
  [user-resource-id todo-item-record]
  (POST "/data" {:params {:method "add-todo-item"
                          :todoitem (pr-str todo-item-record)
                          :userid user-resource-id}
                 :format :raw
                 :handler #()}))


(defn get-values-for-pie-chart
  [s]
  (let [freq (->> (map :todo-item/complete? s)
                  (frequencies))]
    [(get freq true)
     (get freq false)]))


(defn get-values-for-line-chart
  [s]
  (let [date->count      (->> s
                              (filter #(not (:todo-item/complete? %)))
                              (map #(:todo-item/date %))
                              (frequencies)
                              (sort-by first))]
    {:x (map first date->count)
     :y (map last date->count)}))


(defn page
  [user-resource-id]
  (GET "/data" {:params {:method "user-todolist"
                         :resource-id user-resource-id}
                :handler #(do
                            (reset! user-data* (read-string %)))})
  (fn [user-resource-id]
    (if-not (empty? @user-data*)
      [:div.user
       [:div.button
        [:> ui/Button {:size :tiny :basic true :floated :right
                       :on-click (fn [_]
                                   (reset! user-data* {})
                                   (accountant/navigate! "/login"))} "Logout"]]
       [:> ui/Grid {:divided :vertically}
        [:> ui/Grid.Row {:columns 2}
         [:> ui/Grid.Column {:text-align :center}
          [:> ui/Header {:as "h4"} "Complete vs. Incomplete Tasks"]
          [plots/plotly-chart {:div-id "pie" :type "pie"}
           [{:labels ["Complete" "Incomplete"]
             :values (get-values-for-pie-chart (vals @user-data*))
             :type "pie"}]]]
         [:> ui/Grid.Column {:text-align :center}
          [:> ui/Header {:as "h4"} "Unfinished tasks over time"]
          [plots/plotly-chart
           {:div-id "line" :type "line"
            :ylabel "Number of incomplete tasks"
            :margin {:t 20 :b 50 :l 80 :r 10}}
           [(assoc (get-values-for-line-chart (vals @user-data*))
                   :type "line")]]]]
        [:> ui/Grid.Row {:columns 1}
         [:> ui/Grid.Column
          [:> ui/Table
           [:> ui/Table.Header
            [:> ui/Table.Row
             [:> ui/Table.Cell {:text-align :left}
              [:> ui/Header "To-Do Items:"]]
             [:> ui/Table.Cell {:text-align :right}
              [:> ui/Button {:size :tiny
                             :basic true
                             :on-click (fn [_]
                                         (swap! edit-mode* update-in [:edit-mode?] not))}
               "Add Item"]]]]
           [:> ui/Table.Body
            (if (:edit-mode? @edit-mode*)
              [:> ui/Table.Row
               [:> ui/Table.Cell {:text-align :left}
                [:> ui/Input {:type "text"
                              :fluid true
                              :placeholder "Task"
                              :on-change (fn [e d]
                                           (swap! edit-mode* assoc :new-task (-> d .-value)))}]]
               [:> ui/Table.Cell {:text-align :right}
                [:> ui/Button {:icon :check
                               :size :tiny
                               :on-click (fn [_]
                                           (let [resource-id (random-uuid)
                                                 new-todo-item {:resource/id resource-id
                                                                :todo-item/task (:new-task @edit-mode*)
                                                                :todo-item/date (js/Date.)
                                                                :todo-item/complete? false}]
                                             (swap! user-data* assoc resource-id new-todo-item)
                                             (add-todo-item user-resource-id new-todo-item)
                                             (swap! edit-mode* update-in [:edit-mode?] not)))}]]])
            (doall
             (map-indexed (fn [i [id {:todo-item/keys [task complete?] :as item}]]
                            ^{:key i}
                            [:> ui/Table.Row
                             [:> ui/Table.Cell {:text-align :left}
                              [:> ui/Checkbox {:label task
                                               :checked complete?
                                               :on-change (fn [_]
                                                            (swap! user-data* update-in [id :todo-item/complete?] not)
                                                            (update-todo-item id (not complete?)))}]]
                             [:> ui/Table.Cell {:text-align :right}
                              [:> ui/Button {:icon :trash
                                             :size :tiny
                                             :on-click (fn [_]
                                                         (swap! user-data* dissoc id)
                                                         (delete-todo-item! id))}]]])
                          (sort-by (comp :todo-item/date val) > @user-data*)))]]]]]]
      [:div "Loading..."])))

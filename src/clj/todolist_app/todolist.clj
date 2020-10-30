(ns todolist-app.todolist
  (:require [datomic.client.api :as d]))

(defn validate-uuid
  [uuid]
  (if (uuid? uuid)
    uuid
    (java.util.UUID/fromString (str uuid))))


(defn resource-id->eid
  [conn resource-id]
  (:db/id (d/pull (d/db conn) '[*] [:resource/id resource-id])))


(defn create-new-todoitem
  [conn user-resource-id todo-item]
  (let [user-resource-id (validate-uuid user-resource-id)
        user-eid         (resource-id->eid conn user-resource-id)
        {:todo-item/keys
         [task complete? date]} todo-item
        todo-resource-id (or (:resource/id todo-item)
                             (java.util.UUID/randomUUID))
        tx-data          {:todo-item/task task
                          :todo-item/complete? (or complete? false)
                          :todo-item/date (or date (new java.util.Date))
                          :resource/id todo-resource-id}]
    (d/transact conn {:tx-data [{:todo-item/task task
                                 :todo-item/complete? (or complete? false)
                                 :todo-item/date (or date (new java.util.Date))
                                 :resource/id todo-resource-id}]})
    (d/transact conn {:tx-data [[:db/add user-eid
                                 :user/todo-list (resource-id->eid conn todo-resource-id)]]})))


(defn user->todo-items
  [conn user-resource-id]
  (let [db (d/db conn)
        user-resource-id (validate-uuid user-resource-id)
        hydration [{:user/todo-list [:todo-item/task
                                     :todo-item/complete?
                                     :todo-item/date
                                     :resource/id]}]]
    (->> (d/q '[:find (pull ?user hydration)
                :in $ hydration ?user-id
                :where
                [?user :resource/id ?user-id]] db hydration user-resource-id)
         (ffirst)
         (:user/todo-list)
         (map (fn [{:keys [:resource/id] :as m}]
                [id m]))
         (into {}))))


(defn update-complete?
  [conn todo-item-resource-id complete?]
  (let [todo-item-eid (->> todo-item-resource-id
                           (validate-uuid)
                           (resource-id->eid conn))]
    (d/transact conn {:tx-data [[:db/add todo-item-eid :todo-item/complete? (read-string complete?)]]})
    (-> (d/pull (d/db conn) '[*] todo-item-eid)
        (dissoc :db/id))))


(defn delete-todo-item
  [conn todo-item-resource-id]
  (let [todo-item-eid (->> todo-item-resource-id
                           (validate-uuid)
                           (resource-id->eid conn))]
    (d/transact conn {:tx-data [[:db/retractEntity todo-item-eid]]})))


(defn add-todo-item
  [conn user-resource-id todo-item-record]
  (create-new-todoitem conn user-resource-id todo-item-record)
  todo-item-record)

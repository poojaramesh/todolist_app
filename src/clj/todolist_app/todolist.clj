(ns todolist-app.todolist
  (:require [datomic.client.api :as d]
            [todolist-app.datomic :as td]))

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
        todo-resource-id (java.util.UUID/randomUUID)
        tx-data          {:todo-item/task task
                          :todo-item/complete? (or complete? false)
                          :todo-item/date (or date (new java.util.Date))
                          :resource/id todo-resource-id}]
    (d/transact conn {:tx-data [{:todo-item/task task
                                 :todo-item/complete? (or complete? false)
                                 :todo-item/date (or date (new java.util.Date))
                                 :resource/id todo-resource-id}]})
    (println (str "Addding " todo-resource-id " to " user-eid))
    (d/transact conn {:tx-data [[:db/add user-eid :user/todo-lista (resource-id->eid conn todo-resource-id)]]})))



(defn user->todo-items
  [conn user-resource-id]
  (let [db (d/db conn)]
    (d/q '[:find (pull ?list [*])
           :in $ ?user-id
           :where
           [?user :resource/id ?user-id]
           [?user :user/todo-list ?list]
           [?list :todo-item/task ?task]] db user-resource-id)))

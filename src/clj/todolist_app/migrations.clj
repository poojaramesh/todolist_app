(ns todolist-app.migrations)



(defn schema
  []
  [{:db/ident :resource/id
    :db/valueType :db.type/uuid
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/email-address
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/todo-list
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}

   {:db/ident :todo-item/task
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :todo-item/complete?
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one}

   {:db/ident :todo-item/date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one}])

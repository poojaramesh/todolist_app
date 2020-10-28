(ns todolist-app.migrations.todo-item
  (:require [datomic.api :as d]))

(def tx-map
  {:txes
   [[{:db/id #db/id[:db.part/db]
      :db/ident :resouce.type/todo-item}

     {:db/id #db/id[:db.part/db]
      :db/ident :todo-item/task
      :db/valueType :db.type/string
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}

     {:db/id #db/id[:db.part/db]
      :db/ident :todo-item/status
      :db/valueType :db.type/boolean
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}


     {:db/id #db/id[:db.part/db]
      :db/ident :todo-item/date
      :db/valueType :db.type/instant
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}]]})


(def spec (vec [:todo-item tx-map]))

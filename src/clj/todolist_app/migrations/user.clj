(ns todolist-app.migrations.user
  (:require [datomic.api :as d]))

(def tx-map
  {:txes
   [[{:db/id #db/id[:db.part/db]
      :db/ident :resouce.type/user}

     {:db/id #db/id[:db.part/db]
      :db/ident :user/email-address
      :db/valueType :db.type/string
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}


     {:db/id #db/id[:db.part/db]
      :db/ident :user/todo-list
      :db/valueType :db.type/ref
      :db/cardinality :db.cardinality/many
      :db.install/_attribute :db.part/db}]]})


(def spec (vec [:user tx-map]))

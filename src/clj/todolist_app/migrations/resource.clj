(ns todolist-app.migrations.resource
  (:require [datomic.api :as d]))

(def tx-map
  {:txes
   [[{:db/id #db/id[:db.part/db]
      :db/ident :resource/id
      :db/valueType :db.type/uuid
      :db/unique :db.unique/identity
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}

     {:db/id #db/id[:db.part/db]
      :db/ident :resource/type
      :db/valueType :db.type/ref
      :db/cardinality :db.cardinality/one
      :db.install/_attribute :db.part/db}]]})


(def spec (vec [:resource tx-map]))

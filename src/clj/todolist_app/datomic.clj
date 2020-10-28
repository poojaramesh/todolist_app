(ns todolist-app.datomic
  (:require [datomic.client.api :as d]
            [datomic.dev-local :refer [release-db]]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [todolist-app.migrations :as migrations]
            [taoensso.nippy :as nippy]))


(defonce ^:private saved-db-file "demo-db.nippy")


(def client (d/client {:server-type :dev-local
                       :storage-dir :mem
                       :system "ci"}))

(def db-name "todolist")


(defn connect
  []
  (d/connect client {:db-name db-name}))


(defn db
  []
  (d/db (connect)))


;;https://docs.datomic.com/cloud/tutorial/client.html
(defn create-db
  "Creates a new db and run migrations"
  []
  (println "Creating database")
  (d/create-database client {:db-name db-name})
  (println "Database created")
  (println "Running migrations")
  (d/transact (connect) {:tx-data (migrations/schema)})
  (println "Migrations completed"))


(defn load-db
  []
  (create-db)
  #_(let [data-vec (nippy/thaw-from-file saved-db-file)
        conn (d/connect datomic-uri)]
    (d/transact conn data-vec)))


(defrecord Db []
  component/Lifecycle
  (start [this]
    (load-db)
    (println "Datomic started")
    this)
  (stop [this]
    (release-db {:db-name db-name
                 :system "ci"})))

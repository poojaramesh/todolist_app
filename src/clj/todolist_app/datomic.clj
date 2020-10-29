(ns todolist-app.datomic
  (:require [datomic.client.api :as d]
            [datomic.dev-local :refer [release-db]]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [taoensso.nippy :as nippy]
            [todolist-app.migrations :as migrations]
            [todolist-app.test-data :as test-data]))


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
  (println "Populating db")
  (test-data/populate-db (connect)))



(defrecord Db []
  component/Lifecycle
  (start [this]
    (load-db)
    (println "Datomic started")
    this)
  (stop [this]
    #_(release-db {:db-name db-name
                   :system "ci"})
    ))

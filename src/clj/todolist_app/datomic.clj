(ns todolist-app.datomic
  (:require [datomic.client.api :as d]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [todolist-app.migrations :as migrations]
            [taoensso.nippy :as nippy]))


(defonce ^:private saved-db-file "demo-db.nippy")


(def client (d/client {:server-type :dev-local
                       :storage-dir :mem
                       :system "ci"}))

(defn create-mem-uri
  []
  (format "datomic:mem://%s" (java.util.UUID/randomUUID)))


(defn db
  [datomic-uri]
  (d/db (d/connect datomic-uri)))


(defn connect
  [datomic-uri]
  (d/connect client datomic-uri))

;;https://docs.datomic.com/cloud/tutorial/client.html
(defn create-db
  "Creates a new db and run migrations"
  [datomic-uri]
  (println datomic-uri)
  ;; (try)
  (println "Creating database")
  (d/create-database datomic-uri)
  (println "Database created")
  (println "Running migrations")
  (d/transact (d/connect datomic-uri) {:tx-data (migrations/migrations)})
  (println "Migrations completed")
  #_(catch Throwable e
    (throw (ex-info "Error while creating database" {:datomic-uri datomic-uri
                                                     }))))


(defn load-db
  [data-vec datomic-uri]
  (create-db datomic-uri)
  #_(let [data-vec (nippy/thaw-from-file saved-db-file)
        conn (d/connect datomic-uri)]
    (d/transact conn data-vec)))


(defrecord Db [data-vec datomic-uri]
  component/Lifecycle
  (start [this]
    (load-db data-vec datomic-uri)
    (println "Datomic started at: " datomic-uri)
    this)
  (stop [this]))

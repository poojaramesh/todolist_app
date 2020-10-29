(ns todolist-app.user
  (:require [datomic.client.api :as d]))


(defn create-new-user!
  [conn email-address]
  (let [resource-id (java.util.UUID/randomUUID)]
    (d/transact conn {:tx-data
                      [{:resource/id resource-id
                        :user/email-address email-address}]})
    resource-id))


(defn get-user-by-email
  [conn email-address]
  (->> (d/q '[:find ?id
              :in $ ?email
              :where
              [?user :user/email-address ?email]
              [?user :resource/id ?id]]
            (d/db conn) email-address)
       (ffirst)))


(defn get-or-create-user
  [conn email-address]
  (or (get-user-by-email conn email-address)
      (create-new-user! conn email-address)))


(defn get-user-record
  [conn user-resource-id]
  (d/pull (d/db conn) '[*] [:resource/id user-resource-id]))

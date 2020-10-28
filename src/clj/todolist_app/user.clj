(ns todolist-app.user
  (:require [datomic.client.api :as d]
            [todolist-app.datomic :as td]))


(defn create-new-user!
  [conn email-address]
  (d/transact conn {:tx-data
                    [{:resource/type :resource.type/user
                      :resource/id (java.util.UUID/randomUUID)
                      :user/email-address email-address}]}))

(ns todolist-app.user
  (:require [datomic.api :as d]
            [todolist-app.datomic :as td]))


(defn create-new-user!
  [conn email-address]
  (d/transact conn
              [{:resource/type :resource.type/user
                :user/email-address email-address}]))


#_(defn get-user-by-email-address
  [db email-address]
  )

(ns todolist-app.handler
  (:require [todolist-app.user :as user]))


(defmulti app-handler
  (fn [request]
    (get-in request [:params "method"])))


(defmethod app-handler "verify-user-by-email"
  [{:keys [db conn] :as request}]
  (let [email (get-in request [:params "email"])
        resource-id (user/get-or-create-user conn email)]
    {:headers {"Content-Type" "application/edn"}
     :body (pr-str {:user/email-address email
                    :user/resource-id resource-id})}))

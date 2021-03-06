(ns todolist-app.handler
  (:require [todolist-app.user :as user]
            [todolist-app.todolist :as todolist]))


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


(defmethod app-handler "user-todolist"
  [{:keys [db conn] :as request}]
  (let [user-resource-id (get-in request [:params "resource-id"])]
    {:headers {"Content-Type" "application/edn"}
     :body (pr-str (todolist/user->todo-items conn user-resource-id))}))


(defmethod app-handler "update-complete?"
  [{:keys [db conn] :as request}]
  (let [todo-item-resource-id (get-in request [:params "resource-id"])
        complete? (get-in request [:params "complete?"])]
    {:headers {"Content-Type" "application/edn"}
     :body (pr-str (todolist/update-complete? conn todo-item-resource-id complete?))}))


(defmethod app-handler "delete-todo-item"
  [{:keys [db conn] :as request}]
  (let [todo-item-resource-id (get-in request [:params "resource-id"])]
    {:headers {"Content-Type" "application/edn"}
     :body (pr-str (todolist/delete-todo-item conn todo-item-resource-id))}))


(defmethod app-handler "add-todo-item"
  [{:keys [db conn] :as request}]
  (let [user-resource-id (get-in request [:params "userid"])
        todo-item-record (read-string (get-in request [:params "todoitem"]))]
    {:headers {"Content-Type" "application/edn"}
     :body (pr-str (todolist/add-todo-item conn user-resource-id todo-item-record))}))

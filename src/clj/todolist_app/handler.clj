(ns todolist-app.handler)

(defmulti app-handler
  (fn [request]
    (get-in request [:params "method"])))

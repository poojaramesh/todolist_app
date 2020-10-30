(ns todolist-app.server
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes routes GET PUT POST DELETE]]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response
             :refer [response header redirect] :as ring-response]
            [ring.middleware.params :refer [wrap-params]]
            [datomic.client.api :as d]
            [todolist-app.handler :refer [app-handler]]
            [todolist-app.datomic :as td]
            [todolist-app.migrations :as migrations]))

(defn app
  []
  (html
   [:head
    ;;https://cdnjs.com/libraries/semantic-ui
    (include-css "//cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css"
                 "https://fonts.googleapis.com/css?family=Roboto"
                 "css/style.css"
                 )]
   [:html
    [:body
     [:div#app]
     (include-js "js/compiled/todolist_app.js")
     [:script "todolist_app.core.render();"]]
    ]))


(defn app-routes
  []
  (cond-> (-> (routes
               (GET "/login" [] (app))
               (GET "/data" req (app-handler req))
               (POST "/data" req (app-handler req))
               (resources "/")
               (GET "/" [] (redirect "/login"))
               (not-found "Not Found"))
              wrap-params)))


(defn wrap-db
  [handler]
  (fn [request]
    (handler (assoc request
                    :db (td/db)
                    :conn (td/connect)))))


;;https://github.com/stuartsierra/component#web-applications
;;http://http-kit.github.io/server.html#routing
(defrecord WebServer [port handler]
  component/Lifecycle
  (start [this]
    (when-not (:server this)
      (assoc this :server (run-server (wrap-db handler) {:port port}))))
  (stop [this]
    (when-let [server (:server this)]
      (server))
    (dissoc this :server)))


(defrecord Router []
  component/Lifecycle
  (start [this]
    (when-not (:routes this)
      (assoc this :routes (app-routes))))
  (stop [this]
    (dissoc this :routes))
  clojure.lang.IFn
  (invoke [this request] ((:routes this) request)))


(defn router []
  (->Router))


(defn web-server [{:keys [port]
                   :or {port 5000}}]
  (component/system-map
   :datomic (td/->Db)
   :server (component/using (map->WebServer {:port port})
                            [:datomic :handler])
   :handler (router)))


(defn -main []
  (component/start (web-server {:port 5000})))

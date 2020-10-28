(ns todolist-app.server
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes routes GET PUT POST DELETE]]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response
             :refer [response header] :as ring-response]
            [ring.middleware.params :refer [wrap-params]]
            [datomic.api :as d]
            [todolist-app.handler :refer [app-handler]]
            [todolist-app.datomic :as td]
            [todolist-app.migrations :as migrations]))

(defn app
  []
  (println "Calling app")
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
               (GET "/home" [] (app))
               (GET "/data" req (app-handler req))
               ;; (post "/data" req (app-handler req))
               (resources "/")
               (not-found "Not Found>>>"))
              wrap-params)))


(defn wrap-db
  [handler datomic-uri]
  (fn [request]
    (handler (assoc request
                    :datomic-uri datomic-uri
                    ;; :db (d/db datomic-uri)
                    ))))


;;https://github.com/stuartsierra/component#web-applications
;;http://http-kit.github.io/server.html#routing
(defrecord WebServer [port datomic-uri handler]
  component/Lifecycle
  (start [this]
    (println port handler)
    (when-not (:server this)
      (assoc this :server (run-server (wrap-db handler datomic-uri) {:port port}))))
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


(defn web-server [{:keys [port datomic-uri]
                   :or {port 6000
                        datomic-uri (td/create-mem-uri)
                        }}]
  (component/system-map
   :server (component/using (map->WebServer {:port port :datomic-uri datomic-uri})
                            [:datomic :handler])
   :datomic (td/->Db (migrations/migrations) datomic-uri)
   :handler (router)))


(defn -main []
  (component/start (web-server {:port 6000
                                :datomic-uri (td/create-mem-uri)})))

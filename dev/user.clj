(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.repl-api :as figwheel]
            [repl-state :as repl-state]
            [todolist-app.server :as server]))


(defonce system nil)


(defn init [] (alter-var-root #'system (constantly (server/web-server {:port 6000}))))


(defn start [] (alter-var-root #'system component/start-system))


(defn stop [] (alter-var-root #'system component/stop-system))


(defn reset []
  (stop)
  (tools-ns/set-refresh-dirs "src/clj" "src/cljc" "dev/user.clj" "dev/repl_state.clj")
  (tools-ns/refresh)
  (start))


(defn go []
  (init)
  (start))


(defn fig-start []
  (figwheel/start-figwheel!))


(defn fig-stop []
  (figwheel/stop-figwheel!))


(defn cljs-repl []
  (figwheel/cljs-repl))

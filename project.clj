(defproject todolist_app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [com.cognitect/transit-clj "0.8.313"]
                 [cljs-ajax "0.5.8"]
                 [com.stuartsierra/component "0.3.2"]
                 [ring "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-basic-authentication "1.0.5"]
                 [ring-middleware-format "0.7.2"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [compojure "1.5.1"]
                 [bidi "2.1.5"]
                 [re-frame "0.10.6"]
                 [hiccup "1.0.5"]
                 [reagent "1.0.0-alpha2"
                  :exclusions [cljsjs/react cljsjs/react-dom]]
                 [garden "1.3.2"]
                 [cljsjs/semantic-ui-react "0.84.0-0"]
                 [cljsjs/plotly "1.45.3-0"]
                 [venantius/accountant "0.1.7"]
                 [clj-commons/secretary "1.2.4"]
                 [com.taoensso/nippy "3.0.0"]
                 [com.datomic/dev-local "0.9.225"
                  ;;datomic free pulls old deps
                  ;;https://github.com/boot-clj/boot-cljs/issues/190
                  :exclusions [com.google.guava/guava org.clojure/core.async]]
                 [org.clojure/core.async "1.3.610"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            ;; [lein-environ "1.1.0"]
            [lein-figwheel "0.5.18"]
            [lein-garden "0.3.0"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  ;; :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js" "dev-target"]

  :uberjar-name "todolist_app.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main todolist-app.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (go) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/cljc"]

                :figwheel {:on-jsload "todolist-app.core/on-js-reload"}

                :compiler {:main todolist-app.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/todolist_app.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :optimizations :none
                           :preloads [devtools.preload]}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :compiler {:main todolist-app.core
                           :output-to "resources/public/js/compiled/todolist_app.js"
                           :optimizations :advanced
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :garden {:builds [{;; Optional name of the build:
                     :id "style"
                     ;; Source paths where the stylesheet source code is
                     :source-paths ["src/clj"]
                     ;; The var containing your stylesheet:
                     :stylesheet todolist-app.styles/style
                     ;; Compiler flags passed to `garden.core/css`:
                     :compiler {;; Where to save the file:
                                :output-to "resources/public/css/style.css"
                                ;; Compress the output?
                                :pretty-print? false}}]}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.18"]
                             [figwheel-sidecar "0.5.18"]
                             [cider/piggieback "0.4.0"]
                             [cider/cider-nrepl "0.18.0"]
                             [reloaded.repl "0.2.4"]
                             [binaryage/devtools "0.7.2"]]

              :source-paths ["src/clj" "src/cljc" "dev"]
              :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]]
              :hooks [leiningen.cljsbuild]
              :omit-source true
              :aot :all}})

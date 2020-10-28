(ns todolist-app.migrations
  (:require [datomic.api :as d]
            [todolist-app.migrations.resource :as resource]
            [todolist-app.migrations.user :as user]
            [todolist-app.migrations.todo-item :as todo-item]))

(defn migrations
  []
  [resource/spec
   user/spec
   todo-item/spec])

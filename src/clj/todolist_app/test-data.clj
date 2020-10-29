(ns todolist-app.test-data
  (:require [clojure.string :as s]
            [datomic.client.api :as d]
            [todolist-app.datomic :as td]
            [todolist-app.user :as user]
            [todolist-app.todolist :as todolist]))


(def task-list
  ["Buy groceries"
   "Wash car"
   "Read grant proposal"
   "Do laundry"
   "Find fresh ramen noodles"
   "Prepare ochem worksheet"
   "Take a walk"
   "Watch Gran Torino"
   "Vacuum carpets"
   "Buy coffee beans"])


(def email-addresses
  ["john@gmail.com"
   "jake@gmail.com"])


(defn generate-random-date
  []
  (let [month (inc (rand-int 12))
        day (inc (rand-int 27))
        year "2020"
        date (s/join "/" [(str month) (str day) year])
        df (java.text.SimpleDateFormat. "MM/dd/yyyy")]
    (.parse df date)))


(defn populate-db
  []
  (for [user-email email-addresses]
    (let [user-resource-id (user/create-new-user! (td/connect) user-email)
          todo-items (map (fn [task]
                            {:todo-item/task task
                             :todo-item/complete? (zero? (rand-int 2))
                             :todo-item/date (generate-random-date)
                             :resource/id (java.util.UUID/randomUUID)})
                          (take 1 #_(+ (rand-int 5) 5) (shuffle task-list)))]
      (doseq [todo-item todo-items]
        (todolist/create-new-todoitem (td/connect) user-resource-id todo-item)))))

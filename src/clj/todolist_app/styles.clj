(ns todolist-app.styles
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent]]))


(def login
  [[:div.header
    {:text-align :center
     :display :inline}]

   [:div.login
      {:width (px 400)
       :height (px 250)
       :position :fixed
       :top (percent 30)
       :left (percent 50)
       :margin-left (px -200)
       ;; :border "solid 1px lightgrey"
       }]])


(def user
  [[:div.user
    {:margin-top (px 10)}
    [:div.pie]
    [:div.line]]] )

(defstyles style
  [[:body {:margin 20}
    [:div.page {:padding [[(px 5) (px 5)]]}]
    login]])

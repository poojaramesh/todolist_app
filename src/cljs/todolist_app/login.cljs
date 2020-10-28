(ns todolist-app.login
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [cljsjs.semantic-ui-react :as ui]))


(defn main-panel
  []
  [:div.login
   [:span "TEST"]
   [:> ui/Button {:content "Start" :primary true :size :medium}]
   #_[ui/Grid {:centered true :columns 1 :celled true}
    [ui/Grid.Row
     [:h2 "Login to the TODO App"]]
    [ui/Grid.Row
     [ui/Form
      [ui/Form.Field
       {:label "Email Address"
        :error {:content "Please enter a valid email address"
                :pointing "below"}}]]]]
   ])

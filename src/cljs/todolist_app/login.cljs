(ns todolist-app.login
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [ajax.core :refer [POST]]
            [cljs.reader :refer [read-string]]
            [cljsjs.semantic-ui-react :as ui]
            [accountant.core :as accountant]
            [secretary.core :as secretary]))



(def state* (r/atom {:email-address nil
                     :user/resource-id nil
                     :valid? nil}))


;;https://stackoverflow.com/a/33737528
(defn valid-email?
  [email-address]
  (println email-address)
  (when-not (nil? email-address)
    (boolean (re-matches #".+\@.+\..+" email-address))))


(defn verify-user-by-email
  [email-address]
  (let [valid? (valid-email? email-address)]
    (println valid?)
    (swap! state* assoc :valid? valid?)
    (when valid?
      (POST "/data" {:params {:method "verify-user-by-email"
                              :email email-address}
                     :format :raw
                     :handler (fn [e]
                                (println "read string: " (read-string e) (:user/resource-id (read-string e)))
                                (swap! state* assoc :user/resource-id (:user/resource-id (read-string e)))
                                (println "state " @state*))}))))


(defn dispatch [resource-id*]
  (when-not (nil? @resource-id*)
    (js/console.log (str "Got back resource id: " @resource-id*))

    (accountant/navigate! (str "/user/" @resource-id*))))



(defn page
  []
  (r/track! dispatch (r/cursor state* [:user/resource-id]))
  (fn []
    [:div.page
    [:div.header
     [:h2 "TODO List Tracker"]]
    [:div.login
     [:> ui/Grid {:centered true :stretched false}
      [:> ui/Grid.Row
       [:> ui/Form
        {:on-submit (fn [_]
                      (verify-user-by-email (:email-address @state*))
                      (println "Happen right away?" @state*)
                      (dispatch state*))}
        [:> ui/Form.Input
         {:label "Login"
          :placeholder "Email Address"
          :on-change (fn [e d]
                       (swap! state* assoc :email-address (-> e .-target .-value)))}]
        [:> ui/Form.Button "Submit"]]]
      [:> ui/Grid.Row
       (when (and (not (:valid? @state*)) (not (nil? (:valid? @state*))))
         [:> ui/Message {:negative true
                         :content "Invalid email format"}])]]]]))

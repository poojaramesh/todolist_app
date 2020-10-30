(ns todolist-app.plots
  (:require [reagent.core :as r]
            cljsjs.plotly))


(defn plotly-chart
  [{:keys [width height title div-id xlabel ylabel margin]
    :or {width 400
         height 250
         margin {:t 20 :b 50 :l 20 :r 20}}}
   data]
  (let [layout {:height height
                :width width
                :margin margin}
        layout (cond-> layout
                 title (assoc :title title)
                 xlabel (assoc-in [:xaxis :title] xlabel)
                 ylabel (assoc-in [:yaxis :title] ylabel))]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (js/Plotly.newPlot div-id (clj->js data) (clj->js layout)))
      :component-did-update
      (fn [this]
        (let [new-data (last (r/argv this))]
          (js/Plotly.newPlot div-id (clj->js new-data) (clj->js layout))))
      :reagent-render
      (fn [_]
        [:div {:id div-id}])})))

(ns open-source.pub.projects.create
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [open-source.utils :as u]
            [open-source.components.ui :as ui]
            [open-source.pub.projects.project-form :as pf]))

(defn view
  []
  (let [source (subscribe [:key :forms :project :create :base])]
    (fn []
      [:div.edit-listing
       [:div.title [:h1 "Post an Open Source Project"]]
       [:div.wizard
        [:div.spiff.inset
         [pf/form [:projects :create] source]]]])))

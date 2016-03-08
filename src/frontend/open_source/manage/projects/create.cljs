(ns open-source.manage.os-projects.create
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [open-source.utils :as u]
            [open-source.components.ui :as ui]
            [open-source.manage.os-projects.os-project-form :as pf]))

(defn view
  []
  (let [source (subscribe [:key :forms :os :create :base])]
    (fn []
      [:div.edit-listing
       [:div.breadcrumbs
        [:a {:href "/manage/os"} "← manage os projects"]]
       [:div.title [:h1 "Post Your Open Source Project"]]
       [:div.wizard
        [:div.spiff.inset
         [pf/form [:os :create] source]]]])))

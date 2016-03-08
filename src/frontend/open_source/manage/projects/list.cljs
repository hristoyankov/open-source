(ns open-source.manage.os-projects.list
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as str]
            [open-source.components.ui :as ui]
            [open-source.components.form-helpers :as fh]
            [open-source.utils :as u]
            [open-source.routes :as r]))

(defn view
  []
  (let [listings (subscribe [:key :data :my-os-projects])]
    (fn []
      [:div.os-projects
       [:div.pill-nav
        [:a {:href "/manage/jobs"} "Jobs"]
        [:a.active "Open Source Projects"]]
       [:div
        [:button.submit.target
         {:on-click #(r/nav "/manage/os/new" "Publish Open Source Project")}
         "post OS project"]
        [:p "Post the open source projects you manage if you'd like to help new Clojurists get involved :D"]]

       [:div.my-data
        (if-not (empty? @listings)
          [:div
           [:h3 "OS Projects"]
           [:p.tip "Click to edit"]
           [:ul
            (for [listing (sort-by :os-project/name @listings)]
              ^{:key (str "project" (:db/id listing))}
              [:li
               {:on-click #(r/nav (str "/manage/os/" (:db/id listing) "/edit")
                                  (str "Editing OS project " (:os-project/name listing)))}
               (:os-project/name listing)
               [ui/delete-btn [:delete-os-project listing]]])]])]])))

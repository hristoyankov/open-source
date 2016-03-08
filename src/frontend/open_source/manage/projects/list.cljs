(ns open-source.manage.projects.list
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as str]
            [open-source.components.ui :as ui]
            [open-source.components.form-helpers :as fh]
            [open-source.utils :as u]
            [open-source.routes :as r]))

(defn view
  []
  (let [listings (subscribe [:key :data :my-projects])]
    (fn []
      [:div.projects
       [:div.pill-nav
        [:a {:href "/manage/jobs"} "Jobs"]
        [:a.active "Open Source Projects"]]
       [:div
        [:button.submit.target
         {:on-click #(r/nav "/manage/projects/new" "Publish Open Source Project")}
         "post project"]
        [:p "Post the open source projects you manage if you'd like to help new Clojurists get involved :D"]]

       [:div.my-data
        (if-not (empty? @listings)
          [:div
           [:h3 "Projects"]
           [:p.tip "Click to edit"]
           [:ul
            (for [listing (sort-by :project/name @listings)]
              ^{:key (str "project" (:db/id listing))}
              [:li
               {:on-click #(r/nav (str "/manage/projects/" (:db/id listing) "/edit")
                                  (str "Editing Project " (:project/name listing)))}
               (:project/name listing)
               [ui/delete-btn [:delete-project listing]]])]])]])))

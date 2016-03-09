(ns open-source.pub.projects.view
  (:require [re-frame.core :refer [subscribe]]
            [open-source.components.ui :as ui]
            [clojure.string :as str]))

(defn view
  [data]
  (let [data (subscribe [:key :projects :selected])]
    (fn []
      (let [data @data]
        [:div.listings
         [:div.view-all [:a {:href "/"} "← view all open source projects"]]
         [:div.os-project.detail.clearfix
          [:div.meta
           [:div.title [ui/attr data :project/name]]
           (if-let [t (:project/tagline data)]
             [:div.tagline t])]
          [:div.secondary
           (let [repo-url (:project/repo-url data)
                 bil      (:project/beginner-issues-label data)]
             [:div.links
              (if repo-url
                [:div.repo-url [ui/ext-link repo-url "repo"]])
              (if (and repo-url (not-empty bil) (re-find #"github" repo-url))
                [:div.beginner-issues [ui/ext-link (str repo-url "/labels/" bil) "beginner-friendly tasks"]])
              (if-let [hp (:project/home-page-url data)]
                [:div.home-page-url [ui/ext-link hp "home page"]])])
           (if-let [t (:project/tags data)]
             [:div.tags [ui/tags t]])]
          [:div.description
           [:div.project-description (ui/markdown (:project/description data))]]]]))))


(ns open-source.manage.os-projects.preview
  (:require [open-source.components.ui :as ui]
            [open-source.utils :as u]
            [clojure.string :as str]))

(defn tags [t]
  [:div (map (fn [t] ^{:key (gensym)} [:span.tag t])
             (str/split t ","))])

(defn preview-attr
  [data key & [placeholder]]
  (if (and placeholder (not (get data key)))
    [:div {:class (str (name key) " placeholder")} placeholder]
    [ui/attr data key]))

(defn preview
  [data]
  (let [data @data]
    [:div.preview.listings
     [:h2 "Preview"]
     [:h3 "Listing View"]
     [:p "This is how your project will look on the OS listing home page, where it's with listed the rest of the listings."]

     [:div.listing.clearfix
      [:div.core
       [:div.title [preview-attr data :os-project/name "Project Name"]]
       [preview-attr data :os-project/tagline "tagline"]
       (if-let [t (:os-project/tags data)]
         [:div.tags [ui/tags t]])]]
     [:h3 "Detail View"]
     [:p "This is how your listing will look when a user clicks on it to view it."]
     [:div.os-project.detail.clearfix
      [:div.meta
       [:div.title [preview-attr data :os-project/name "Project Name"]]
       [preview-attr data :os-project/tagline "Tagline"]]
      [:div.secondary
       (let [repo-url (:os-project/repo-url data)
             bil      (:os-project/beginner-issues-label data)]
         
         [:div.links
          (if repo-url
            [:div.repo-url [ui/ext-link repo-url "repo"]]
            [:div.repo-url.placeholder "Repo URL"])
          (if (and repo-url (not-empty bil) (re-find #"github" repo-url))
            [:div.beginner-issues [ui/ext-link (str repo-url "/labels/" bil) "beginner-friendly tasks"]])
          (if-let [hp (:os-project/home-page-url data)]
            [:div.home-page-url [ui/ext-link hp "home page"]]
            [:div.home-page-url.placeholder "Home Page URL"])])
       (if-let [t (:os-project/tags data)]
         [:div.tags [ui/tags t]]
         [:div.tags.placeholder "Tags"])]
      [:div.description
       (if (:os-project/description data)
         [:div.project-description (ui/markdown (:os-project/description data))]
         [:div {:class "project-description placeholder"} "Project description"])]]]))

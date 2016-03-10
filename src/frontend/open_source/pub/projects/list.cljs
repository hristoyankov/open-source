(ns open-source.pub.projects.list
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as str]
            [open-source.utils :as u]
            [open-source.components.ui :as ui]
            [open-source.routes :as r]
            [open-source.components.form-helpers :as fh]))

(defn filter-tag
  [tags tag]
  [:span.tag-container
   [:span.tag {:class (if (get tags tag) "active")
               :data-prevent-nav true
               :on-click #(do (.stopPropagation %)
                              (.preventDefault %)
                              (dispatch [:toggle-tag tag]))} tag]])

(defn view
  []
  (let [listings      (subscribe [:filtered-projects])
        search-input  (fh/builder [:projects :search])
        selected-tags (subscribe [:key :forms :projects :search :data :tags])
        tags          (subscribe [:project-tags])]
    (fn []
      (let [listings @listings
            tags @tags
            selected-tags @selected-tags]
        [:div
         [:div.intro
          [:div.main
           [:p "Looking to improve your skills and contribute? These projects are active and welcome contributors."]]
          [:div.secondary
           [:button.submit.target
            {:on-click #(r/nav "/projects/new")}
            "Post Your Project"]]]
         [:div.main.listings.public
          [ui/ctg {:transitionName "filter-survivor" :class "listing-list"}
           (for [l listings]
             ^{:key (str "os-project-" (:slug l))}
             [:div.listing-container
              [:a.listing.clearfix {:href (:slug l)}
               [:div.core
                [:div.title [ui/attr l :project/name]]
                [ui/attr l :project/tagline]
                (if (:project/beginner-friendly l)
                  [:div.beginner-friendly "beginner friendly"])
                (if-let [t (:record/tags l)]
                  [:div.tags
                   (for [tag (u/split-tags t)]
                     ^{:key (gensym)} [filter-tag selected-tags tag])])]]])]]
         [:div.secondary.listings
          [:div.section.search
           [search-input :search :query
            :no-label true
            :placeholder "Search: `music`, `database` ..."]]
          [:div.section.beginner-toggle
           [search-input :checkbox :project/beginner-friendly :label "Beginner friendly?"]]
          [:div.section.tags
           [:div
            (for [tag tags]
              ^{:key (gensym)} [filter-tag selected-tags tag])]]
          [:div.section
           [:div.details
            [:h3 "Learn Clojure"]
            [:div.book-cover [:img {:src "/images/book-cover.png"}]]
            [:p [:a {:href "http://braveclojure.com"} [:em "Clojure for the Brave and True"]]
             " is a fun, in-depth introduction to Clojure. Read
                   it "
             [:a {:href "http://braveclojure.com"} "free online"]
             " or "
             [:a {:href "http://amzn.to/1mz8qNR"} "buy the print
                   or ebook on Amazon"]
             "!"]]]]]))))

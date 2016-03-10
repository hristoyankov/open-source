(ns open-source.pub.projects.list
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as str]
            [open-source.utils :as u]
            [open-source.components.ui :as ui]
            [open-source.routes :as r]
            [open-source.components.form-helpers :as fh]))

(defn filter-tag
  [tag]
  [:span.tag {:data-prevent-nav true
              :on-click #(do (.stopPropagation %)
                             (.preventDefault %)
                             (dispatch [:edit-field [:forms :projects :search :data :query] tag]))} tag])

(defn view
  []
  (let [listings (subscribe [:filtered-projects])
        query (subscribe [:key :forms :projects :search :data :query])
        tags  (subscribe [:project-tags])]
    (fn []
      (let [listings @listings
            tags @tags]
        (println listings)
        [:div
         [:div.intro
          [:div.main
           [:p "Looking to improve your skills and contribute? These projects are active and welcome contributors."]]
          [:div.secondary
           [:button.submit.target
            {:on-click #(r/nav "/projects/new" "Post your project")}
            "Post Your Project"]]]
         [:div.main.listings.public
          [:div.listing-list
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
                   (for [tag (map str/trim (str/split t ","))]
                     ^{:key (gensym)} [filter-tag tag])])]]])]]
         [:div.secondary.listings
          [:div.section.search
           [:input {:type :search
                    :placeholder "Search: `music`, `database` ..."
                    :on-change #(fh/handle-change % [:forms :projects :search] :query)
                    :value @query}]]
          [:div.section.beginner-toggle
           ]
          [:div.section.tags
           [:div
            (for [tag tags]
              ^{:key (gensym)} [filter-tag tag])]]
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

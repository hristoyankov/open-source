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
        [:div
         [:div.main.listings.public
          [:div.nav.clearfix
           [:div.pill-nav
            [:a {:href "/"}
             "Jobs"]
            [:a.active 
             "Open Source Projects"]]
           [:div.search
            [:input {:type :search
                     :placeholder "Search: `music`, `database` ..."
                     :on-change #(fh/handle-change % [:forms :projects :search] :query)
                     :value @query}]]]
          [:p "Looking to improve your skills and contribute? These projects are active and welcome beginners."]
          [:div.listing-list
           (for [l listings]
             ^{:key (str "os-project-" (:db/id l))}
             [:a.listing.clearfix {:href (u/slug l)}
              [:div.core
               [:div.title [ui/attr l :os-project/name]]
               [ui/attr l :os-project/tagline]
               (if-let [t (:os-project/tags l)]
                 [:div.tags
                  (for [tag (map str/trim (str/split t ","))]
                    ^{:key (gensym)} [filter-tag tag])])]])]]
         [:div.secondary.listings
          [:div.section
           [:div
            [:button.submit.target
             {:on-click #(r/nav "/manage/os/new" "Post your project")}
             "Post your project"]]]
          [:div.section.tags
           [:h3 "Filter by Tag"]
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

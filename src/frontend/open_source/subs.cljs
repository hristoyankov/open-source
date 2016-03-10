(ns open-source.subs
  (:require [re-frame.core :refer [register-sub subscribe]]
            [clojure.string :as str]
            [clojure.set :as set]
            [open-source.utils :as u])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn path-reaction
  [db path]
  (reaction (get-in @db path)))

(register-sub
 :key
 (fn [db [_ & path]]
   (reaction (get-in @db path))))

(register-sub
 :form-data
 (fn [db [_ & path]]
   (reaction (get-in @db (concat [:forms] path [:data])))))

(defn filter-items
  [query items]
  (if (empty? query)
    items
    (let [query (str/lower-case query)]
      (filter (fn [item]
                (not= -1
                      (.indexOf (->> (vals item)
                                     (filter string?)
                                     (str/join " ")
                                     (str/lower-case))
                                query)))
              items))))

(defn filter-query
  [db query-path items]
  (let [query (path-reaction db query-path)]
    (reaction (filter-items @query @items))))

(defn filter-tags
  [db tag-path items]
  (let [tags (path-reaction db tag-path)]
    (reaction
     (let [tags (set @tags)
           items @items]
       (if (empty? tags)
         items
         (filter #(every? (set (u/split-tags (:record/tags %))) tags)
                 items))))))

(defn filter-toggle
  [db toggle-path items]
  (let [toggle (path-reaction db toggle-path)]
    (reaction (if @toggle (filter (last toggle-path) @items) @items))))

(register-sub
 :filtered-projects
 (fn [db _]
   (let [items (path-reaction db [:data :projects])]
     (->> items
          (filter-query  db [:forms :projects :search :data :query])
          (filter-tags   db [:forms :projects :search :data :tags])
          (filter-toggle db [:forms :projects :search :data :project/beginner-friendly])))))

(register-sub
 :project-tags
 (fn [db _]
   (let [listings (subscribe [:filtered-projects])]
     (reaction
      (->> @listings
           (mapcat (fn [l] (str/split (:record/tags l) ",")))
           (map (comp str/lower-case str/trim))
           distinct
           sort)))))

(ns open-source.subs
  (:require [re-frame.core :refer [register-sub]]
            [clojure.string :as str])
  (:require-macros [reagent.ratom :refer [reaction]]))

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

(defn filter-by
  [db data-path query-path]
  (let [query (reaction (get-in @db query-path))
        items (reaction (get-in @db data-path))]
    (reaction (filter-items @query @items))))

(register-sub
 :filtered-projects
 (fn [db _]
   (filter-by db [:data :projects] [:forms :projects :search :data :query])))

(register-sub
 :project-tags
 (fn [db _]
   (let [listings (reaction (get-in @db [:data :projects]))]
     (reaction
      (->> @listings
           (mapcat (fn [l] (str/split (:project/tags l) ",")))
           (map (comp str/lower-case str/trim))
           distinct
           sort)))))

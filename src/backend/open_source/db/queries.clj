(ns open-source.db.queries
  (:require [datomic.api :as d]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(defn projects
  [db]
  {:projects (->> (d/q '[:find (pull ?e [*])
                         :in $
                         :where [?e :project/name]]
                       db)
                  (map first)
                  (sort-by (comp clojure.string/lower-case :project/name)))})

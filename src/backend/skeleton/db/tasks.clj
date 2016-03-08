(ns skeleton.db.tasks
  "database management functions"
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [com.flyingmachine.vern :as v]))


(defn slurp-resource [path] (-> path clojure.java.io/resource slurp))
(defn read-resource [path] (-> path slurp-resource read-string))

(defn create-db
  [env]
  (d/create-database (:db-uri env)))

(defn load-fixtures
  []
  (let [entities (atom [])
        data (read-resource "db/fixtures.edn")]
    (v/do-named (fn [processed group-name entity]
                  (swap! entities #(conj % (:data entity)))
                  (get-in entity [:data :db/id]))
                data)
    @entities))

(defn conform [conn file]
  (let [schema (read-resource file)]
    (c/ensure-conforms conn schema)))

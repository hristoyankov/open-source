(ns open-source.resources.common
  (:require [datomic.api :as d]
            [com.flyingmachine.datomic-junk :as dj]
            [flyingmachine.webutils.validation :refer [if-valid]]
            [buddy.auth :as buddy]
            [medley.core :as medley]))

(defn remove-nils-from-map
  [record]
  (into {} (remove (comp nil? second) record)))

(defn errors-map
  [errors]
  {:errors errors
   :representation {:media-type "application/transit+json"}})

(defmacro validator
  "Used in invalid? which is why truth values are reversed"
  ([validation]
   `(validator ~(gensym) ~validation))
  ([ctx-sym validation]
   `(fn [~ctx-sym]
      (if-valid
       (params ~ctx-sym) ~validation errors#
       false
       [true (errors-map errors#)]))))

(defn add-record
  "Return a map that gets added to the ctx"
  [r]
  (if r {:record r}))

(defn add-http
  [url]
  (when url
    (if (re-find #"^http" url)
      url
      (str "http://" url))))

(defn ensure-http
  [x url-keys]
  (reduce (fn [m k] (clojure.core/update m k add-http))
          x
          url-keys))

(defn result-novelty
  [ctx & [id]]
  (let [id  (or id (ctx-id ctx) (created-id ctx))
        ent (into {} (dj/ent id (db-after ctx)))]
    {:novelty (merge {:db/id id}
                     (medley/map-vals (fn [v] (if-let [id (:db/id v)]
                                               {:db/id id}
                                               v))
                                      ent))}))

(defn result-data
  [& queries]
  (fn [ctx]
    (apply merge-with merge (result-novelty ctx) (map #(% ctx) queries))))


(defn projects
  [ctx]
  (get-in ctx [:request :projects]))

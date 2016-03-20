(ns open-source.resources.common
  (:require [flyingmachine.webutils.validation :refer [if-valid]]
            [buddy.auth :as buddy]
            [medley.core :as medley]))

(defn remove-nils-from-map
  [record]
  (into {} (remove (comp nil? second) record)))

(defn params
  [ctx]
  (get-in ctx [:request :params]))

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

(defn projects
  [ctx]
  (get-in ctx [:request :projects]))

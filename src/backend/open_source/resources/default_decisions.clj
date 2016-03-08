(ns open-source.resources.default-decisions
  (:require [io.clojure.liberator-transit]))

(defn record-in-ctx
  [ctx]
  (:record ctx))

(defn errors-in-ctx
  ([]
   (errors-in-ctx {}))
  ([opts]
   (fn [ctx]
     (merge {:errors (:errors ctx)} opts))))

(def defaults
  ^{:doc "A 'base' set of liberator resource decisions for list,
    create, show, update, and delete"}
  (let [errors-in-ctx (errors-in-ctx {:representation {:media-type "application/transit+json"}})
        base {:available-media-types ["application/transit+json"
                                      "application/transit+msgpack"
                                      "application/json"]
              :allowed-methods [:get]
              :authorized? true
              :handle-unauthorized errors-in-ctx
              :handle-malformed errors-in-ctx
              :respond-with-entity? true
              :new? false}]
    {:list base
     :create (merge base {:allowed-methods [:post]
                          :new? true
                          :handle-created record-in-ctx})
     :show base
     :update (merge base {:allowed-methods [:put]})
     :delete (merge base {:allowed-methods [:delete]
                          :delete-enacted? true})}))

(ns skeleton.middleware.datomic
  "Provide a simple ring middleware injecting the connection and
  current database in the ring request"
  (:require [datomic.api :as d]))

(defn wrap-datomic [handler uri]
  (fn [request]
    (let [conn (d/connect uri)]
      (handler (assoc request
                      :conn conn
                      :db (d/db conn))))))


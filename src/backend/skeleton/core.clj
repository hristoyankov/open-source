(ns skeleton.core
  (:gen-class)
  (:require [environ.core               :as env]
            [datomic.api                :as d]
            [clojure.java.io            :as io]
            [com.stuartsierra.component :as component]
            [org.httpkit.server         :as hk]
            [skeleton.system            :as system]
            [skeleton.handlers.app      :as app]
            [skeleton.db.tasks          :as dbt]
            [open-source.db.projects    :as p]))

(defmacro final
  [& body]
  `(do ~@body (System/exit 0)))

(defn -main
  [cmd & args]
  (case cmd
    "server"
    (let [projects (atom {})]
      (p/start (p/new-core-async-project-watcher projects 30000))
      (-> (app/create-app projects)
          (hk/run-server {:port (Integer. (:open-source-http-server-port env/env))})))))

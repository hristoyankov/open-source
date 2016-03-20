(ns open-source.core
  (:gen-class)
  (:require [environ.core               :as env]
            [org.httpkit.server         :as hk]
            [open-source.handlers.app   :as app]
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

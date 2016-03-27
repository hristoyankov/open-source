(ns open-source.system
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as hk]
            [open-source.handlers.app :as app]
            [open-source.db.projects :as p]))

;; ring app
(defrecord RingApp [projects-db]
  component/Lifecycle
  (start [component]
    ;; TODO this is so gross
    (assoc component :app (app/create-app (get-in projects-db [:project-watcher :projects]))))
  (stop [component]
    (assoc component :app nil)))
(defn new-ring-app []
  (map->RingApp {}))

;; http server
(defn new-http-kit [ring-app config]
  (hk/run-server ring-app config))
(defrecord HttpServer [config ring-app]
  component/Lifecycle
  (start [component]
    (assoc component :server (new-http-kit (:app ring-app) config)))
  (stop [component]
    (if-let [server (:server component)]
      (server))
    (assoc component :server nil)))

(defn new-http-server
  [config]
  (map->HttpServer {:config config}))

(defn env->config
  [env]
  {:db-uri (:db-uri env)
   :http-server {:port (Integer. (:http-server-port env))}})

(defn build [env]
  (let [config (env->config env)]
    (component/system-map
     :projects-db (p/new-project-db 30000)
     :ring-app (component/using (new-ring-app)
                                [:projects-db])
     :server   (component/using (new-http-server (:http-server config))
                                [:ring-app]))))

(ns skeleton.system
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as hk]
            [skeleton.handlers.app :as app]
            [open-source.db.github :refer [projects]]))

;; ring app
(defrecord RingApp [projects]
  component/Lifecycle
  (start [component]
    (assoc component :app (app/create-app projects)))
  (stop [component]
    (assoc component :app nil)))
(defn new-ring-app [projects]
  (map->RingApp {:projects projects}))

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
     :ring-app (new-ring-app projects)
     :server   (component/using (new-http-server (:http-server config))
                                [:ring-app]))))

(ns skeleton.system
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as hk]
            [skeleton.handlers.app :as app]))

;; ring app
(defrecord RingApp [db-uri]
  component/Lifecycle
  (start [component]
    (assoc component :app (app/create-app db-uri)))
  (stop [component]
    (assoc component :app nil)))
(defn new-ring-app [db-uri]
  (map->RingApp {:db-uri db-uri}))

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
     :ring-app (new-ring-app (:db-uri config))
     :server   (component/using (new-http-server (:http-server config))
                                [:ring-app]))))

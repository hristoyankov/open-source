(ns skeleton.handlers.app
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.session.cookie :as sc]
            [ring.middleware.format :as f]
            [compojure.core :refer [routes]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends.session :refer [session-backend]]
            [skeleton.handlers.routes :as routes]
            [skeleton.middleware.datomic :as md]
            [datomic.api :as d]))

(defn log-params
  "Pring every request's params to aid debugging"
  [f]
  (fn [req]
    (println (:params req))
    (f req)))

(def middleware
  (-> site-defaults
      (assoc-in [:static :resources] "/")
      (assoc-in [:session :store] (sc/cookie-store {:key "customize this.."}))))

(def auth-backend (session-backend))

(defn create-app [db-uri]
  (-> (routes/resource-routes)
      (wrap-authentication auth-backend)
      ;; (log-params) uncomment to log
      (md/wrap-datomic db-uri)
      (f/wrap-restful-format :formats [:transit-json])
      (wrap-defaults middleware)))

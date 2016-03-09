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


(defn wrap-projects [handler projects]
  (fn [request] (handler (assoc request :projects projects))))

(def middleware
  (-> site-defaults
      (assoc-in [:static :resources] "/")
      (assoc-in [:security :anti-forgery] false)))

(defn create-app [projects]
  (-> (routes/resource-routes)
      (log-params)
      (wrap-projects projects)
      (f/wrap-restful-format :formats [:transit-json])
      (wrap-defaults middleware)))

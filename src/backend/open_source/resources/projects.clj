(ns open-source.resources.projects
  (:require [open-source.resources.common :as c]
            [open-source.db.queries :as q]))

(defn process-params
  [ctx]
  (update-in ctx [:request :params]
                 c/ensure-http
                 [:project/repo-url :project/home-page-url]))

(def result-data (c/result-data (c/query-result q/projects)))

(defn resource-decisions
  [_]
  {:create {:authorized? (fn [ctx] (println "checking auth") true)
            :post! (comp c/add-result c/create process-params)
            :handle-created result-data}

   :update {:authorized? true
            :put! (comp c/add-result c/update)
            :handle-ok result-data}})

(comment :delete {:delete! (comp c/add-result c/delete)
                  :delete-enacted? true
                  :respond-with-entity? true
                  :handle-ok result-data})


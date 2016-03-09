(ns open-source.resources.projects
  (:require [open-source.resources.common :as c]
            [open-source.db.github :as db]
            [clojure.string :as str]))

(defn process-params
  [ctx]
  (update-in ctx [:request :params]
             (fn [params]
               (-> params
                   (c/ensure-http [:project/repo-url :project/home-page-url])
                   (select-keys db/project-keys)))))


(defn slugify
  [txt]
  (-> txt
      str/lower-case
      (str/replace #"[^a-zA-Z0-9]" "-")
      (str/replace #"-+" "-")
      (str/replace #"-$" "")))

(defn params->project
  [params]
  (let [path (slugify (:project/name params))]
    ))

(defn list-projects
  [ctx]
  {:data (db/list-projects @(c/projects ctx))})

(defn resource-decisions
  [_]
  {:create {:authorized? true
            :post! (fn [ctx]
                     (let [project (process-params (c/params ctx))]
                       (db/write-project! (c/projects ctx) project)))
            :handle-created list-projects}

   :update {:authorized? true
            :put! (fn [ctx])
            :handle-ok list-projects}})

(comment :delete {:delete! (comp c/add-result c/delete)
                  :delete-enacted? true
                  :respond-with-entity? true
                  :handle-ok list-projects})


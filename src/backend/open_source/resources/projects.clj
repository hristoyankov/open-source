(ns open-source.resources.projects
  (:require [open-source.resources.common :as c]
            [open-source.db.github :as db]))

(def project-keys [:project/name :project/tagline :project/repo-url
                   :project/home-page-url :project/tags
                   :project/beginner-issues-label
                   :project/description :project/beginner-friendly])

(defn process-params
  [ctx]
  (update-in ctx [:request :params]
             (fn [params]
               (-> params
                   (c/ensure-http [:project/repo-url :project/home-page-url])
                   (select-keys project-keys)))))

(defn create-project
  [params])

(defn list-projects
  [ctx]
  (db/list-projects @(c/projects ctx)))

(defn resource-decisions
  [_]
  {:create {:authorized? true
            :post! (fn [ctx]
                     (let [project (create-project (c/params ctx))]
                       (swap! (c/projects ctx) assoc (:path project) project)))
            :handle-created list-projects}

   :update {:authorized? true
            :put! (fn [ctx])
            :handle-ok list-projects}})

(comment :delete {:delete! (comp c/add-result c/delete)
                  :delete-enacted? true
                  :respond-with-entity? true
                  :handle-ok list-projects})


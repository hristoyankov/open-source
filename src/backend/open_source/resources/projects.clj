(ns open-source.resources.projects
  (:require [open-source.resources.common :as c]
            [open-source.db.github :as db]
            [clojure.string :as str]))


(defn update-project
  [ctx]
  (db/write-project! (c/projects ctx) (c/params ctx)))

(defn list-projects
  [ctx]
  {:data (db/list-projects @(c/projects ctx))})

(defn resource-decisions
  [_]
  {:create {:authorized? true
            :post! update-project
            :handle-created list-projects}

   :update {:authorized? true
            :put! update-project
            :handle-ok list-projects}})

(comment :delete {:delete! (comp c/add-result c/delete)
                  :delete-enacted? true
                  :respond-with-entity? true
                  :handle-ok list-projects})


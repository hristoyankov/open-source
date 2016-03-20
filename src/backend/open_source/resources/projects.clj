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
  {:create {:post! update-project
            :handle-created list-projects}

   :update {:put! update-project
            :handle-ok list-projects}})

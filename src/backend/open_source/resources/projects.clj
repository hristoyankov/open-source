(ns open-source.resources.projects
  (:require [open-source.resources.common :as c]
            [open-source.db.github :as db]
            [clojure.string :as str]))


(defn update-project
  [ctx]
  (db/write-project! (c/projects ctx) (c/params ctx)))

(defn resource-decisions
  [_]
  {:create {:post! update-project
            :handle-created c/list-projects}

   :update {:put! update-project
            :handle-ok c/list-projects}})

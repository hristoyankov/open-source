(ns open-source.resources.init
  (:require [open-source.db.github :as db]
            [open-source.resources.common :as c]
            [environ.core :refer [env]]))

(defn resource-decisions
  [opts]
  {:list {:handle-ok (fn [ctx] {:data (db/list-projects @(c/projects ctx))})}})


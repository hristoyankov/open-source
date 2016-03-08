(ns open-source.resources.init
  (:require [open-source.db.queries :as q]
            [open-source.resources.common :as c]
            [environ.core :refer [env]]))

(defn resource-decisions
  [opts]
  {:list {:handle-ok (fn [ctx] {:data (q/projects (c/db ctx))})}})


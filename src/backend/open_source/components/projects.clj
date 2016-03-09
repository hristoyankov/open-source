(ns open-source.components.projects
  (:require [com.stuartsierra.component :as component]
            [open-source.lib.github :refer [projects]]))

;; TODO use core async to start/stop periodic project polling
(defrecord Projects []
  component/Lifecycle
  (start [component]
    (assoc component :projects projects))
  (stop [component]
    (assoc component :projects nil)))

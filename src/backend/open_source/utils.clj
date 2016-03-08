(ns open-source.utils
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(defmacro tv
  [event-name]
  `(.. ~event-name -target -value))

(defn now
  []
  (java.util.Date.))

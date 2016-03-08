(ns open-source.handlers.init
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET]]
            [accountant.core :as acc]
            [open-source.handlers.common :as c]
            [open-source.db :as db]))

(register-handler :initialize
  [trim-v]
  (fn [db _]
    (GET "/init"
         :handler (c/ajax-success :init-success))
    db/default-value))

(register-handler :init-success
  [trim-v]
  (fn [db [data]]
    ;; TODO prevent double google hit
    (acc/dispatch-current!)
    (merge db data {:initialized true})))

(ns open-source.manage.os-projects.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [open-source.utils :as u]
            [open-source.routes :as r]
            [open-source.db :as db]
            [open-source.handlers.common :as c]))

(register-handler :new-os-project
  [trim-v]
  (fn [db _]
    (-> db
        (merge {:nav {:l0 :manage
                      :l1 :os-projects
                      :l2 :new}})
        (assoc-in [:forms :os :create] db/form-default))))

(register-handler :edit-os-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :my-os-projects id)]
      (-> db
          (merge {:nav {:l0 :manage
                        :l1 :os-projects
                        :l2 :edit}})
          (update-in [:forms :os :update] merge {:data listing
                                                 :base listing})))))

(register-handler :create-project-success
  [trim-v]
  (fn [db [data]]
    (-> (merge-with merge db data)
        (merge {:nav {:l0 :manage
                      :l1 :os-projects
                      :l2 :list}}))))


;; ===========
;; delete
;; ===========

(register-handler :delete-os-project
  [trim-v]
  (fn [db [listing]]
    (DELETE (str "/manage/os-projects/" (:db/id listing))
            {:handler (c/ajax-success :merge-result)})
    db))

(register-handler :admin-delete-os-project
  [trim-v]
  (fn [db [listing]]
    (DELETE (str "/admin/os-projects/" (:db/id listing))
            {:handler (c/ajax-success :merge-result)})
    db))

;; ===========
;; admin edit
;; ===========

(register-handler :admin-edit-os-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :os-projects id)]
      (-> db
          (merge {:nav {:l0 :admin
                        :l1 :os-projects
                        :l2 :edit}})
          (update-in [:forms :admin-os :update] merge {:data listing
                                                       :base listing})))))

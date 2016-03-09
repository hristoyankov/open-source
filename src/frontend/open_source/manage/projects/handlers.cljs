(ns open-source.manage.projects.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [open-source.utils :as u]
            [open-source.routes :as r]
            [open-source.db :as db]
            [open-source.handlers.common :as c]))

(register-handler :new-project
  [trim-v]
  (fn [db _]
    (-> db
        (merge {:nav {:l0 :manage
                      :l1 :projects
                      :l2 :new}})
        (assoc-in [:forms :projects :create] db/form-default))))

(register-handler :edit-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :projects :slug (str "projects/" id))]
      (-> db
          (merge {:nav {:l0 :manage
                        :l1 :projects
                        :l2 :edit}})
          (update-in [:forms :projects :update] merge {:data listing
                                                       :base listing})))))

(register-handler :create-project-success
  [trim-v]
  (fn [db [data]]
    (-> (merge-with merge db data)
        (merge {:nav {:l0 :public
                      :l1 :projects
                      :l2 :list}}))))


;; ===========
;; delete
;; ===========

(register-handler :delete-project
  [trim-v]
  (fn [db [listing]]
    (DELETE (str "/manage/projects/" (:db/id listing))
            {:handler (c/ajax-success :merge-result)})
    db))

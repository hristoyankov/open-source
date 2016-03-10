(ns open-source.pub.projects.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [accountant.core :as a]
            [open-source.routes :as r]
            [open-source.utils :as u]
            [open-source.routes :as r]
            [open-source.db :as db]
            [open-source.handlers.common :as c]))

(register-handler :new-project
  [trim-v]
  (fn [db _]
    (-> db
        (merge {:nav {:l0 :public
                      :l1 :projects
                      :l2 :new}})
        (assoc-in [:forms :projects :create] db/form-default))))

(register-handler :edit-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :projects :slug (str "projects/" id))]
      (-> db
          (merge {:nav {:l0 :public
                        :l1 :projects
                        :l2 :edit}})
          (update-in [:forms :projects :update] merge {:data listing
                                                       :base listing})))))

(register-handler :list-projects
  [trim-v]
  (fn [db [tags]]
    (-> db
        (merge {:nav {:l0 :public
                      :l1 :projects
                      :l2 :list}})
        (assoc-in [:forms :projects :search :data :tags] tags))))

(register-handler :create-project-success
  [trim-v]
  (fn [db [data]]
    (r/nav "/")
    (merge-with merge db data)))

(register-handler :edit-project-success
  [trim-v]
  (fn [db [data]]
    (r/nav "/")
    (merge-with merge db data)))


;; ===========
;; delete
;; ===========

(register-handler :delete-project
  [trim-v]
  (fn [db [listing]]
    (DELETE (str "/projects/" (:db/id listing))
            {:handler (c/ajax-success :merge-result)})
    db))

;; ===========
;; search
;; ===========

(register-handler :toggle-tag
  [trim-v]
  (fn [db [tag]]
    (println "tag:" tag)
    (let [tags (set (get-in db [:forms :projects :search :data :tags]))
          new-tags (if (tags tag) (disj tags tag) (conj tags tag))]
      (println "path:" (r/projects-path {:query-params {:tags (clojure.string/join "," (sort new-tags))}}))
      (r/nav (r/projects-path {:query-params {:tags (clojure.string/join "," (sort new-tags))}}))
      db)))

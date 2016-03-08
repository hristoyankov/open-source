(ns open-source.handlers.common
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET PUT POST DELETE]]
            [open-source.utils :as u]
            [open-source.db :as db]))

;; UI

(register-handler :toggle
  [trim-v]
  (fn [db [& path]] (update-in db path not)))

;; Forms
(register-handler :edit-field
  [trim-v]
  (fn [db [path val]] (assoc-in db path val)))

(defn success->sleeping
  [form-state-path]
  (js/setTimeout #(dispatch [:edit-field form-state-path :sleeping]) 2000))

(defn ajax-success
  "Dispatch result of ajax call to handler key"
  [handler-key & args]
  (fn [x] (dispatch (into [handler-key x] args))))

(defn ajax-error
  "Dispatch error returned by ajax call to handler key"
  [handler-key & args]
  (fn [x] (dispatch (into [handler-key (get-in x [:response :errors])] args))))

(defn method-url
  [action resource-path data]
  (case action
    :create [POST (str "/" resource-path)]
    :update [PUT (str "/" resource-path "/" (:db/id data))]))

(defn submit-form [action resource-path form-path data
                   {:keys [submit-form-success-handler submit-form-error-handler]
                    :or {submit-form-success-handler :submit-form-success
                         submit-form-error-handler   :submit-form-error}
                    :as form-spec}]
  (let [[method url] (method-url action resource-path data)]
    (method url
            {:params data
             :handler (ajax-success submit-form-success-handler form-path form-spec)
             :error-handler (ajax-error submit-form-error-handler form-path form-spec)})))

(defn register-forms
  [form-specs]
  (register-handler :submit-form
    [(path :forms) trim-v]
    (fn [form-db [form-path]]
      (let [[form-name action] form-path
            form-spec          (get-in form-specs form-path)
            resource-path      (get-in form-specs [(first form-path) :path])]
        (submit-form action
                     resource-path
                     form-path
                     (get-in form-db (conj form-path :data))
                     form-spec)
        (assoc-in form-db (conj form-path :state) :submitting)))))

(defn submit-form-success
  [db data form-path]
  (let [form-state-path (u/flatv :forms form-path :state)
        form-base-path  (u/flatv :forms form-path :base)]
    (success->sleeping form-state-path)
    (-> (merge-with merge db data)
        (assoc-in form-state-path :success)
        (assoc-in form-base-path  (:novelty data)))))

(register-handler :submit-form-success
  [trim-v]
  (fn [db [data form-path form-spec]]
    (submit-form-success db data form-path)))

(register-handler :submit-form-error
  [(path :forms) trim-v]
  (fn [db [errors form-path form-spec]]
    (assoc-in db (conj form-path :errors) errors)))

(register-handler :merge-result
  [trim-v]
  (fn [db [data]]
    (merge-with merge db data)))

;;

(defn data-by-id
  [db k id]
  (first (filter #(= id (str (:db/id %)))
                 (get-in db [:data k]))))

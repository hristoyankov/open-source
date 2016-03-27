(ns open-source.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [open-source.handlers.common :as c]
            [open-source.handlers.init]
            [open-source.db :as db]))

;; generic

(register-handler :assoc-in
  [trim-v]
  (fn [db [path val]]
    (assoc-in db path val)))

(register-handler :merge
  [trim-v]
  (fn [db [m]]
    (merge db m)))

;; register forms

(c/register-forms db/forms)

;; view/nav
(register-handler :view-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :projects :slug (str "projects/" id))]
      (merge db {:nav {:l0 :public
                       :l1 :projects
                       :l2 :view}
                 :projects {:selected listing}}))))

;; title
(def suffix " | Open Source Clojure Projects")
(defn sfx [s] (str s suffix))

(register-handler :set-title
  [trim-v]
  (fn [db [{:keys [l0 l1 l2]}]]
    (set! js/document.title
          (case [l0 l1 l2]
            [:public :projects :list]  (sfx "Open Source Clojure Projects")
            [:public :projects :view]  (sfx (get-in db [:projects :selected :project/name]))
            [:public :projects :edit]  (sfx "Edit OS Project")
            [:public :projects :new]   (sfx "New OS Project")
            
            [nil nil nil] (sfx "Open Source Clojure Projects")))
    db))

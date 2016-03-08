(ns open-source.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [open-source.handlers.common :as c]
            [open-source.handlers.init]
            [open-source.db :as db]
            [open-source.manage.projects.handlers]))

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

(register-handler :view-job-listing
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :job-listings id)]
      (merge db {:nav {:l0 :public
                       :l1 :job-listings
                       :l2 :view}
                 :job-listings {:selected listing}}))))

(register-handler :view-os-project
  [trim-v]
  (fn [db [id]]
    (let [listing (c/data-by-id db :projects id)]
      (merge db {:nav {:l0 :public
                       :l1 :projects
                       :l2 :view}
                 :projects {:selected listing}}))))

;; title
(def suffix " | Clojure Work")
(defn sfx [s] (str s suffix))

(register-handler :set-title
  [trim-v]
  (fn [db [{:keys [l0 l1 l2]}]]
    (set! js/document.title
          (case [l0 l1 l2]
            [:public :job-listings :list] (sfx "Clojure Jobs")
            [:public :projects :list]  (sfx "Open Source Clojure Projects")

            [:public :job-listings :view] (sfx (get-in db [:job-listings :selected :listing/job-title]))
            [:public :projects :view]  (sfx (get-in db [:projects :selected :project/name]))

            [:manage :job-listings :list] (sfx "Manage Job Listings")
            [:manage :job-listings :edit] (sfx "Edit Job Listing")
            [:manage :job-listings :new]  (sfx "Post Job Listing")
            [:manage :job-listings :charts] (sfx "Job Listing Charts")

            [:manage :projects :list]  (sfx "Manage OS Project Listings")
            [:manage :projects :edit]  (sfx "Edit OS Project")
            [:manage :projects :new]   (sfx "New OS Project")

            [:admin :job-listings :list]  (sfx "Admin Job Listings")
            [:admin :projects  :list]  (sfx "Admin OS Project Listings")
            [:admin :job-listings :edit]  (sfx "Admin Edit Job Listing")
            [:admin :projects  :edit]  (sfx "Admin Edit OS Project Listings")

            [:public :forgot-password :request-reset] (sfx "Forgot Password")
            [:public :forgot-password :reset]         (sfx "Reset Password")
            
            [nil nil nil] "Clojure Work"))
    db))

;; state
(register-handler :poll
  [trim-v]
  (fn [db _]
    (GET "/listings"
         :handler (fn [data]
                    (dispatch [:merge-result data])
                    (js/setTimeout #(dispatch [:poll]) 30000)))
    db))

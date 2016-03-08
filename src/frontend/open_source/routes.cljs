(ns open-source.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :refer [dispatch subscribe]]
            [re-frame.db :refer [app-db]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [accountant.core :as acc]
            [open-source.utils :as u])
  (:require-macros [open-source.routes :refer [when-initialized defroute-ga]])
  (:import goog.history.Html5History))

(def initialized (atom false))
(def queue (atom []))

(defroute-ga "/" {}
  (dispatch [:merge {:nav {:l0 :public
                           :l1 :projects
                           :l2 :list}}]))

(defroute-ga "/projects/:id" {:keys [id]}
  (dispatch [:view-os-project id]))

(defroute-ga "/manage/projects" {}
  (dispatch [:merge {:nav {:l0 :manage
                           :l1 :projects
                           :l2 :list}}]))

(defroute-ga "/manage/projects/new" {}
  (dispatch [:new-project]))

(defroute-ga "/manage/projects/:id/edit" {:keys [id]}
  (dispatch [:edit-project id]))

(defn nav
  [path title]
  (acc/navigate! path))

(acc/configure-navigation!)

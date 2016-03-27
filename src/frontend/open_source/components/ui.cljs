(ns open-source.components.ui
  (:require [reagent.core :refer [atom] :as r]
            [re-frame.core :refer [dispatch]]
            [clojure.string :as str]
            [open-source.utils :as u]
            [cljsjs.marked]
            [cljs-time.core :as time]))

;; 
(def ctg (r/adapt-react-class (-> js/React (aget "addons") (aget "CSSTransitionGroup"))))

(defn ext-url
  [url]
  (if (re-find #"^http" url)
    url
    (str "http://" url)))

(defn ext-link
  [url text]
  [:a.view-link {:href (ext-url url)
                 :target "_blank"
                 :on-click #(.stopPropagation %)}
   [:i {:class "fa fa-external-link"}]
   " "
   (or text "view")])

;; markdown
(defn link-emails
  [txt]
  (clojure.string/replace txt #"([^ ]+@[^ \.]+\.[a-zA-Z]{2,15})" "[$1](mailto:$1)"))

(defn markdown [txt]
  {:dangerouslySetInnerHTML #js {:__html (js/marked (link-emails (or txt "")))}})

(defn markdown-help-toggle
  []
  [:span.markdown-help-toggle
   {:on-click #(dispatch [:toggle :ui :show-markdown-help])}
   "(markdown " [:span.fa.fa-question-circle] ")"])

;; misc

(defn attr
  [data key]
  [:div {:class (name key)} (get data key)])

(defn tags [t]
  [:div (map (fn [t] ^{:key (gensym)} [:span.tag t])
             (str/split t ","))])

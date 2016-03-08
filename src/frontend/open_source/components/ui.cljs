(ns open-source.components.ui
  (:require [reagent.core :refer [atom] :as r]
            [re-frame.core :refer [dispatch]]
            [clojure.string :as str]
            [open-source.utils :as u]
            [cljsjs.marked]
            [cljs-time.core :as time]))

;; 
(def ctg (r/adapt-react-class (-> js/React (aget "addons") (aget "CSSTransitionGroup"))))

;; slides
(defn slide [attrs ent component]
  [ctg {:transitionName "slide"
        :class (str "slide-container " (:class attrs))}
   (when-not (empty? (:data @ent)) component)])

(defn editable-side-slide [attrs ent ent-type view edit]
  (let [editing (r/atom false)]
    (fn [] [slide attrs ent
           [:div
            [:div.actions
             [:span.edit {:on-click #(swap! editing not)}
              (if @editing "view" "edit")]
             [:span.cancel {:on-click (fn []
                                        (reset! editing false)
                                        (dispatch [(u/strk ent-type "-cancel")]))}
              [:i.fa.fa-times-circle]
              " close"]]
            (if @editing [edit ent] [view ent])]])))

;; buttons
(defn toggle-btn
  ([visible show-text hide-text]
   (toggle-btn visible show-text hide-text #(swap! visible not)))
  ([visible show-text hide-text on-click]
   (let [vis @visible
         classname (if vis "hide" "show")
         text      (str " " (if vis hide-text show-text))
         i-class   (if vis "fa-minus-circle" "fa-plus-circle")]
     [:div.toggle-btn
      [:span {:class classname
              :on-click on-click}
       [:i {:class (str "fa " i-class)}]
       text]])))

(defn delete-btn
  [dispatch-v]
  [:div.delete-btn
   {:on-click (fn [e]
                (.stopPropagation e)
                (dispatch dispatch-v))}
   [:i {:class "fa fa-trash"}]])

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


(defn pop-top
  [component]
  (r/create-class
   {:component-did-mount (fn [_] (u/scroll-top))
    :reagent-render component}))

(defn tags [t]
  [:div (map (fn [t] ^{:key (gensym)} [:span.tag t])
             (str/split t ","))])

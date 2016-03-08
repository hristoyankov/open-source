(ns open-source.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch-sync dispatch subscribe]]
            
            [open-source.utils :as u]
            [open-source.routes]
            [open-source.handlers]
            [open-source.subs]
            [open-source.utils :as u]
            [open-source.routes :as routes]

            [open-source.pub.projects.list  :as ppl]
            [open-source.pub.projects.view  :as ppv]

            [open-source.manage.projects.list   :as mpl]
            [open-source.manage.projects.create :as mpc]
            [open-source.manage.projects.edit   :as mpe]))

(enable-console-print!)

(defn app
  []
  (let [nav          (subscribe [:key :nav])
        initialized  (subscribe [:key :initialized])]
    (fn []
      (let [nav @nav
            {:keys [l0 l1 l2]} nav]
        (dispatch [:set-title nav])
        [:div.app
         (when (= l0 :public)
           [:div.hero
            [:div.container
             [:div.banner
              [:a {:href "/"} "Clojure Work"]]
             [:div.tagline
              [:a {:href "/"} "use the language you love"]]
             [:a.manage {:href "/manage/projects"}]]])
         [:div.container {:class (if (= l0 :public) "pub" "manage")}
          (when @initialized
            [:div.panel
             (case [l0 l1 l2]                                                 
               [:public :projects  :list]   [ppl/view]
               [:public :projects  :view]   [ppv/view]
               
               [:manage :projects :list]    [mpl/view]
               [:manage :projects :edit]    [mpe/view]
               [:manage :projects :new]     [mpc/view]
               
               [nil nil nil] [:div])])]]))))

(defn -main []
  (dispatch-sync [:initialize])
  (r/render-component [app] (u/el-by-id "app"))
  (dispatch [:poll]))

(-main)

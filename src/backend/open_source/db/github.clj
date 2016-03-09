(ns open-source.db.github
  (:require [tentacles.repos :as r]
            [org.httpkit.client :as http]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [environ.core :refer [env]]))

(def projects (atom {}))
(def user "braveclojure")
(def repo "open-source-projects")

(def project-keys [:project/name
                   :project/tagline
                   :project/repo-url
                   :project/home-page-url 
                   :project/beginner-issues-label
                   :project/description
                   :project/tags
                   :project/beginner-friendly])

(defn slugify
  "Take arbitrary text and format it nicely"
  [txt]
  (-> txt
      str/lower-case
      (str/replace #"[^a-zA-Z0-9]" "-")
      (str/replace #"-+" "-")
      (str/replace #"-$" "")))

(defn path->slug
  [p]
  (str/replace p #"(\.edn$)" ""))

(defn merge-github-data
  [{:keys [sha path]} project]
  (merge project
         {:sha  sha
          :path path
          :slug (path->slug path)}))

(defn update-projects
  [current-projects]
  (let [files (r/contents user repo  "projects" {})
        need-update (filter (fn [file] (not= (get-in current-projects [(:path file) :sha])
                                            (:sha file)))
                            files)
        need-delete (set/difference (into #{} (keys current-projects)) (into #{} (map :path files)))]
    (->> need-update
         (pmap (fn [file] (merge-github-data file (edn/read-string (:body @(http/get (:download_url file)))))))
         (reduce (fn [xs x] (assoc xs (:path x) x))
                 current-projects)
         (#(apply dissoc % need-delete)))))

(swap! projects update-projects)

;; TODO parse project body
(defn list-projects
  [projects]
  {:projects (->> (vals projects)
                  (sort-by (comp str/lower-case :project/name)))})



(defn write-project!
  [projects project]
  (let [path (str "projects/" (slugify (:project/name project)) ".edn")
        result (:content (r/update-contents user
                                            repo
                                            path
                                            "updating project via web"
                                            (str project)
                                            (:sha project)
                                            {:oauth-token (:open-source-github-oauth-token env)}))]
    
    (swap! projects assoc path (merge-github-data result project))
    @projects))

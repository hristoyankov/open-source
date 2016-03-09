(ns open-source.db.github
  (:require [tentacles.repos :as r]
            [org.httpkit.client :as http]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(def projects (atom {}))

(defn path->slug
  [p]
  (str/replace p #"(\.edn$)" ""))

(defn update-projects
  [current-projects]
  (let [files (r/contents "braveclojure" "open-source-projects" "projects" {})
        need-update (filter (fn [file] (not= (get-in current-projects [(:path file) :sha])
                                            (:sha file)))
                            files)
        need-delete (set/difference (into #{} (keys current-projects)) (into #{} (map :path files)))]
    (->> need-update
         (pmap (fn [file] (merge {:sha  (:sha file)
                                 :path (:path file)
                                 :slug (path->slug (:path file))}
                                (edn/read-string (:body @(http/get (:download_url file)))))))
         (reduce (fn [xs x] (assoc xs (:path x) x))
                 current-projects)
         (#(apply dissoc % need-delete)))))

(swap! projects update-projects)

;; TODO parse project body
(defn list-projects
  [projects]
  {:projects (->> (vals projects)
                  (sort-by (comp str/lower-case :project/name)))})

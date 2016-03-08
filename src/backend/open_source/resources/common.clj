(ns open-source.resources.common
  (:require [datomic.api :as d]
            [com.flyingmachine.datomic-junk :as dj]
            [flyingmachine.webutils.validation :refer [if-valid]]
            [buddy.auth :as buddy]
            [medley.core :as medley]))

(defn now
  []
  (java.util.Date.))

(defn remove-nils-from-map
  [record]
  (into {} (remove (comp nil? second) record)))

(defn params
  [ctx]
  (get-in ctx [:request :params]))

(defn ctx-id
  [ctx]
  (if-let [id (get-in ctx [:request :params :id])]
    (Long/parseLong id)
    (:db/id (params ctx))))

(defn auth
  [ctx]
  (get-in ctx [:request :identity]))

(defn auth-id
  [ctx]
  (:db/id (auth ctx)))

(defn errors-map
  [errors]
  {:errors errors
   :representation {:media-type "application/transit+json"}})

(def auth-error (errors-map {:authorization "Not authorized."}))

(defn authenticated?
  [ctx]
  (if (buddy/authenticated? (:request ctx))
    [true {:auth (:identity (:request ctx))}]
    [false auth-error]))

(defn session-userid
  [ctx])

(defn admin?
  [ctx]
  (let [user (auth ctx)]
    (if (and user (:user/admin user))
      [true {:auth user}]
      [false auth-error])))

(defmacro validator
  "Used in invalid? which is why truth values are reversed"
  ([validation]
   `(validator ~(gensym) ~validation))
  ([ctx-sym validation]
   `(fn [~ctx-sym]
      (if-valid
       (params ~ctx-sym) ~validation errors#
       false
       [true (errors-map errors#)]))))

(defn add-record
  "Return a map that gets added to the ctx"
  [r]
  (if r {:record r}))

(defn add-result
  [r]
  (if r {:result r}))

(defn conn
  [ctx]
  (get-in ctx [:request :conn]))

(defn db
  [ctx]
  (get-in ctx [:request :db]))

(defn ctx->create-map
  [ctx]
  (-> ctx
      params
      remove-nils-from-map
      (merge {:db/id (d/tempid :db.part/user)})))

(defn created-id
  [ctx]
  (-> ctx
      (get-in [:result :tempids])
      first
      second))

(defn create
  [ctx]
  @(d/transact (conn ctx) [(ctx->create-map ctx)]))

(defn ctx->update-map
  [ctx]
  (-> ctx
      params
      remove-nils-from-map
      (assoc :db/id (ctx-id ctx))
      (dissoc :id)))

(defn update
  [ctx]
  @(d/transact (conn ctx) [(ctx->update-map ctx)]))

(defn delete
  [ctx]
  @(d/transact (conn ctx) [[:db.fn/retractEntity (ctx-id ctx)]]))

;; TODO fix
(defn user-owns?
  [owner-key]
  (fn [ctx]
    (let [auth (auth ctx)
          ent  (into {} (dj/ent (ctx-id ctx) (db ctx)))]
      (or (and auth ent
               (= (:db/id (owner-key ent)) (:db/id auth))
               [true {:auth auth}])
          [false auth-error]))))

(defn assoc-user
  [ctx user-key]
  (assoc-in ctx
            [:request :params user-key]
            (auth-id ctx)))

(defn db-after
  [ctx]
  (get-in ctx [:result :db-after]))

(defn query-result
  [query]
  (fn [ctx]
    {:data (query (db-after ctx))}))

(defn query-result-owned
  [query]
  (fn [ctx]
    {:data (query (db-after ctx) (auth-id ctx))}))

(defn add-http
  [url]
  (when url
    (if (re-find #"^http" url)
      url
      (str "http://" url))))

(defn ensure-http
  [x url-keys]
  (reduce (fn [m k] (clojure.core/update m k add-http))
          x
          url-keys))

(defn result-novelty
  [ctx & [id]]
  (let [id  (or id (ctx-id ctx) (created-id ctx))
        ent (into {} (dj/ent id (db-after ctx)))]
    {:novelty (merge {:db/id id}
                     (medley/map-vals (fn [v] (if-let [id (:db/id v)]
                                               {:db/id id}
                                               v))
                                      ent))}))

(defn result-data
  [& queries]
  (fn [ctx]
    (apply merge-with merge (result-novelty ctx) (map #(% ctx) queries))))

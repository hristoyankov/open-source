(set-env!
 :source-paths #{"src/frontend" "src/backend"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs      "1.7.228-1"       :scope "test"]
                 [org.clojure/clojurescript "1.7.228"]
                 [adzerk/boot-reload    "0.4.4"           :scope "test"]
                 
                 ;; server
                 [environ                             "1.0.1"]
                 [boot-environ                        "1.0.1"]
                 [ring/ring-core                      "1.3.2" :exclusions [org.clojure/tools.reader]]
                 [ring/ring-devel                     "1.3.2"]
                 [ring/ring-defaults                  "0.1.4"]
                 [ring-middleware-format              "0.5.0"]
                 [org.clojure/core.async              "0.2.374"]
                 [com.stuartsierra/component          "0.3.1"]
                 [org.danielsz/system                 "0.2.0" :scope "test"]
                 [liberator                           "0.14.0"]
                 [com.flyingmachine/liberator-unbound "0.1.1"]
                 [com.flyingmachine/webutils          "0.1.6"]
                 [http-kit                            "2.1.16"]
                 [compojure                           "1.3.4"]
                 [buddy                               "0.9.0"]
                 [io.clojure/liberator-transit        "0.3.0"]
                 [medley                              "0.7.1"]
                 [clj-time                            "0.11.0"]
                 [org.imgscalr/imgscalr-lib           "4.2"]
                 [hiccup                              "1.0.5"]
                 [me.raynes/cegdown                   "0.1.1"]
                 [tentacles                           "0.5.1"]
                 [org.apache.httpcomponents/httpclient "4.5.1"]

                 ;; client
                 [reagent                     "0.5.1" :exclusions [cljsjs/react]]
                 [boot-sassc                  "0.1.2" :scope "test"]
                 [cljsjs/marked               "0.3.5-0"]
                 [cljsjs/react-with-addons    "0.13.3-0"]
                 [re-frame                    "0.6.0"]
                 [cljs-ajax                   "0.5.3"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [secretary                   "1.2.3"]
                 [venantius/accountant        "0.1.6"]])

;; don't remember why this is necessary
(load-data-readers!)

(require
 '[boot.core                      :as c]
 '[boot.pod                       :as pod]
 '[adzerk.boot-cljs               :refer [cljs]]
 '[adzerk.boot-reload             :refer [reload]]
 '[system.boot                    :as sb]
 '[environ.core                   :as environ]
 '[environ.boot                   :refer [environ]]
 '[mathias.boot-sassc             :refer [sass]]
 '[open-source.system             :as system])


(deftask run []
  "Run a reloading web server"
  (comp
   (watch)
   (sass :sass-file "main.scss" :output-dir "stylesheets")
   (sb/system :sys #(system/build environ/env) :hot-reload true :auto-start true)
   (reload :on-jsload 'open-source.core/-main) ;; customize this
   (cljs :compiler-options {:parallel-build true
                            :asset-path "http://localhost:3000/main.out"}
         :source-map true)
   (repl :server true)))

(deftask development
  "Set development env vars"
  []
  (environ :env {:http-server-port "3000"
                 :db-uri           "datomic:free://localhost:4334/open-source"}))


(deftask dev
  "Compose development and run to run application in development mode"
  []
  ;; Output to target/dev so that builds, which output to
  ;; target/build, don't clobber dev
  (set-env! :target-path "target/dev")
  (comp (development)
        (run)))

(deftask build
  "Builds an uberjar"
  []
  (set-env! :target-path "target/build")
  (comp (sass :sass-file "main.scss" :output-dir "stylesheets")
        (cljs :optimizations :advanced)
        (pom :project 'app :version "0.1.0")
        ;; I've forgotten why I need this voodoo magic
        (uber :exclude (conj pod/standard-jar-exclusions #".*\.html" #"clout/.*" #"license" #"LICENSE"))
        (aot :namespace '#{open-source.core})
        (jar :main 'open-source.core :file "app.jar")))

(defproject nong-taphan "0.1.0-SNAPSHOT"
  :description "A simple geosparql server"
  :url "https://github.com/veer66/nong-taphan"
  :license {:name "BSD-2-Clause"
            :url "https://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.apache.jena/jena-core "3.5.0"]
                 [org.apache.jena/jena-spatial "3.5.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [compojure "1.6.0"]]
  :ring {:handler nong-taphan.core/app}
  :uberjar-name "server.jar"
  :target-path "target/%s"
  :profiles {:dev {:plugins [[lein-ring "0.12.0"]]}
             :uberjar {:aot :all}})

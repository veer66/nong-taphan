(ns nong-taphan.core
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp])
  (:import [org.apache.jena.query.spatial EntityDefinition]
           [org.apache.jena.query
            ReadWrite
            QueryFactory
            DatasetFactory
            QueryExecutionFactory
            ResultSetFormatter]
           [org.apache.lucene.store FSDirectory]
           [org.apache.jena.riot RDFDataMgr]
           [org.apache.jena.sparql.util QueryExecUtils]
           [org.apache.jena.query.spatial SpatialDatasetFactory])
  (:gen-class))

(load-file "config.clj")

(defn init-base-dataset [idx-dir-path]
  (let [ent-def (EntityDefinition. "entityField" "geoField")
        idx-dir (-> idx-dir-path java.io.File. .toPath FSDirectory/open)
        dataset (DatasetFactory/create)]
    (SpatialDatasetFactory/createLucene dataset idx-dir ent-def)))

(defn load-ttl [dataset ttl-path]
  (.begin dataset ReadWrite/WRITE)
  (let [m (.getDefaultModel dataset)]
    (RDFDataMgr/read m ttl-path)
    (.commit dataset))
  (.end dataset))

(defn make-q-str [lat lon limit]
  (str "PREFIX spatial: <http://jena.apache.org/spatial#>"
       "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
       "SELECT ?p  WHERE {"
       "  ?p spatial:nearby (" lat " " lon " " limit " 'km') ."
       ;;   "  ?p rdfs:label ?n ."
       "}"))

(defn query [dataset q-str]
  (.begin dataset ReadWrite/READ)
  (let [q (QueryFactory/create q-str)
        q-exec (QueryExecutionFactory/create q dataset)
        results (.execSelect q-exec)]
    (.end dataset)
    results))

(defn res-set->json [res-set]
  (let [buf (java.io.ByteArrayOutputStream.)]
    (ResultSetFormatter/outputAsJSON buf res-set)
    (-> buf
        .toByteArray
        String.)))

(def dataset (-> (init-base-dataset (:idx-path config))))
(load-ttl dataset (:ttl-path config))

(def q-str (make-q-str 12.6813 101.2816 1000.0))

(defroutes app
  (GET "/" [] "<h1>nong-taphan</h1>")
  (POST "/query" req
        (let [body-q-str (-> req
                             :body
                             slurp)]
          (println body-q-str)
          (let [resp (-> (resp/response (-> (query dataset body-q-str)
                                            res-set->json))
                         (resp/status 200)
                         (resp/header "Content-Type" "application/json"))]
            resp)))
  (route/not-found "Not found"))

(defn -main
  [& args]
  (let [dataset (init-base-dataset "idx")]
    (load-ttl dataset "ve.ttl")
    (println "1000 KM from Rayong")
    (println (res-set->json (query dataset q-str)))))

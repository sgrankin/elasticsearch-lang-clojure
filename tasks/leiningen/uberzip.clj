(ns leiningen.uberzip
  (:require [leiningen.jar :refer [get-jar-filename]]
            [leiningen.core.main :refer [info]]
            [clojure.java.io :refer [input-stream copy file]]
            )
  (:import [java.io FileOutputStream]
           [java.util.zip ZipOutputStream ZipEntry]))

(defn uberzip [project & args]
  "Zip the uberjar"
  (let [f (get-jar-filename project :standalone)
        {v :version n :name } project]
    (with-open [out (-> (str "target/" n "-" v ".zip")
                        (FileOutputStream.)
                        (ZipOutputStream.))]
      (with-open [in (input-stream f)]
        (.putNextEntry out (ZipEntry. (-> f file .getName)))
        (copy in out)
        (.closeEntry out)
        (info "zipped file" f)))
    ))

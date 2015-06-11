(ns leiningen.uberzip
  (:require
    [clojure.java.io :as io]
    [leiningen.core.main :refer [apply-task info]]
    [leiningen.jar :refer [get-jar-filename]]
    )
  (:import
    [java.io FileOutputStream]
    [java.util.zip ZipOutputStream ZipEntry]
    ))

(defn uberzip [project & args]
  "Zip the uberjar"
  (apply-task "uberjar" project args)
  (let [jar (io/file (get-jar-filename project :standalone))
        {v :version n :name} project
        zip (.getPath (io/file (.getParent jar) (str n "-" v ".zip")))
        ]
    (with-open [out (-> zip io/output-stream ZipOutputStream.)
                in (io/input-stream jar)]
      (.putNextEntry out (-> jar .getName ZipEntry.))
      (io/copy in out)
      (.closeEntry out)
      (info "Created " zip))
    ))

(defproject org.clojars.touch/elasticsearch-lang-clojure "0.3.0-SNAPSHOT"
  :description "Clojure script engine for ElasticSearch."
  :url "https://github.com/sgrankin/elastic-lang-clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]]
  :profiles {:provided {:dependencies [[org.elasticsearch/elasticsearch "1.4.4"]]}
             :uberjar {:aot :all}
             :dev {:dependencies [[log4j/log4j "1.2.17"]]}}
  :aot :all)

(ns com.thelastcitadel.es.engine
  (:require [clojure.tools.logging :as log]
            [clojure.walk :refer [keywordize-keys]]
            [com.thelastcitadel.es.core])
  (:gen-class
   :extends org.elasticsearch.common.component.AbstractComponent
   :implements [org.elasticsearch.script.ScriptEngineService]
   :constructors {^{org.elasticsearch.common.inject.Inject true} [org.elasticsearch.common.settings.Settings]
                  [org.elasticsearch.common.settings.Settings]}))

(defn -init [s]
  [[] nil])

(declare executable-script
         search-script)

(def types (into-array String ["clojure" "clj"]))

(def exts (into-array String ["clj"]))

(defn -types [_]
  types)

(defn -extensions [_]
  exts)

(defn -compile [_ script]
  (let [out (java.io.ByteArrayOutputStream.)
        fun (with-open [o (java.io.PrintWriter. (java.io.OutputStreamWriter. out))
                        e (java.io.PrintWriter. (java.io.OutputStreamWriter. out))]
              (binding [*warn-on-reflection* true
                        *out* o
                        *err* e
                        *ns* (find-ns 'com.thelastcitadel.es.core)]
                (eval `(fn [~'env] ~(read-string (str "(do " script " )"))))))
        out (String. (.toByteArray out))]
    (when-not (empty? out)
      (log/info out))
    (fn [env]
      (try
        (fun env)
        (catch Exception e
          (log/info e)
          (throw e))))))

(defn -sandboxed [_] false)

(defn -executable [_ compiled-script env]
  (executable-script compiled-script (into {} env)))

(defn -search [_ compiled-script lookup env]
  (search-script compiled-script  (into {} env) lookup))

(defn -execute [_ compiled-script env]
  (compiled-script env))

(defn -unwrap [_ x]
  x)

(defn -close [_])

(defn -scriptRemoved [_ compiled-script])

(defn executable-script [compiled-script env]
  (let [env (atom env)]
    (reify org.elasticsearch.script.ExecutableScript
      ; ExecutableScript
      (setNextVar [_ name value]
        (swap! env assoc name value))

      (run [_]
        (-execute nil compiled-script @env))

      (unwrap [_ x]
        x))))

(defn search-script [compiled-script env ^org.elasticsearch.search.lookup.SearchLookup lookup]
  (let [lookup-map (into {} (.asMap lookup))
        env (atom (merge lookup-map (keywordize-keys lookup-map) env))]
    (reify org.elasticsearch.script.SearchScript
      ; ExecutableScript
      (setNextVar [_ name value]
        (swap! env assoc name value))

      (run [_]
        (compiled-script @env))

      (unwrap [_ x]
        x)

      ; SearchScript
      (setNextDocId [_ id]
        (.setNextDocId lookup (int id)))

      (^void setNextSource [_ ^java.util.Map source]
        (-> lookup (.source) (.setNextSource source)))

      (runAsFloat [this]
        (float (.run this)))

      (runAsLong [this]
        (long (.run this)))

      (runAsDouble [this]
        (double (.run this)))

      ; ReaderContextAware
      (setNextReader [_ context]
        (.setNextReader lookup context))

      ; ScorerAware
      (setScorer [_ scorer]
        (let [score (org.elasticsearch.script.ScoreAccessor. scorer)]
          (swap! env assoc "_score" score :_score score))))))

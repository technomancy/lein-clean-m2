(ns leiningen.clean-m2
  (:require [leiningen.core.classpath :as cp]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]))

(def local-repo (io/file (System/getProperty "user.home") ".m2" "repository"))

(defn- coords [local-repo-path f]
  (rest (re-find #"([^\/]+)/([^\/]+)/([^\/]+)/"
                 (.replace (.getAbsolutePath f) local-repo-path ""))))

;; TODO: pom artifacts can be required to calculate dependencies but
;; not show up in the final dependencies listing

(defn clean-m2
  "Wipe local repository of all artifacts not required for project.

Don't use this in a user account unless it's only used for a single project."
  [project & [dry-run?]]
  (let [artifacts (concat (cp/resolve-dependencies :plugins project)
                          (cp/resolve-dependencies :dependencies project))
        local-repo (.getAbsolutePath (io/file (:local-repo project local-repo)))
        used? (set (map (partial coords local-repo) artifacts))]
    (doseq [f (file-seq (io/file local-repo))
            :when (and (not (used? (coords local-repo f))) (.isFile f))]
      (main/debug "Unused:" (str f))
      (when-not dry-run?
        (io/delete-file f)))))
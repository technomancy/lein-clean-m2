(ns leiningen.clean-m2
  (:require [leiningen.core.classpath :as cp]
            [clojure.java.io :as io]))

(def local-repo (io/file (System/getProperty "user.home") ".m2" "repository"))

(defn- coords [local-repo-path f]
  (rest (re-find #"([^\/]+)/([^\/]+)/([^\/]+)/"
                 (.replace (str f) local-repo-path ""))))

(defn clean-m2
  "Wipe local repository of all artifacts not required for project.

Don't use this in a user account unless it's only used for a single project."
  [project & [dry-run?]]
  (let [artifacts (concat (cp/resolve-dependencies :plugins project)
                          (cp/resolve-dependencies :dependencies project))
        local-repo (:local-repo project local-repo)
        used? (set (map (partial coords (str local-repo)) artifacts))]
    (doseq [f (file-seq (io/file local-repo))]
      (when (and (not (used? (coords (str local-repo) f))) (.isFile f))
        (println "Unused:" (str f))
        (when-not dry-run?
          (io/delete-file f))))))
(defproject rhs-ip-utils "0.1.0-SNAPSHOT"
  :description "Library of functions that manipulate various forms of IP addresses."
  :url         "http://example.com/FIXME://github.com/rstinejr/Shadrahk/tree/master/rhs-ip-utils"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :release-tasks [["change" "version" "leiningen.release/bump-version" "release"]]
  :jvm-opts ["-Djava.library.path=/usr/lib:/usr/local/lib"]
  :profiles {:uberjar {:aot :all}
             :dev { :plugins [
                      [lein-bin "0.3.4"]
                      [lein-voom "0.1.0-20140716_032004-g85e4c9b"]]}})

; .............................................................. ABOUT
;
; The miscelaneous/profiles.clj is written under the hiccups philosophy
;    keep the dev view of the cljs lib separated from its user view
; This content will be merged with the content from the
; ~/.lein/profiles.clj and miscelaneous/project.clj
; In case of conflicts the miscelaneous/profiles.clj takes
; precedence over the miscelaneous/project.clj;
; ....................................................................

{:dev { ;; dirs to be cleaned by the lein clean command
       :clean-targets ["out"]
       ;; have test/clj and test/cljs into the leiningen :test-paths option
       :test-paths ["test/clj" "test/cljs"]
       ;; adding dev-resources/tools/repl; the cljsbuild plugin
       ;; does not add its own source-paths
       :source-paths ["dev-resources/tools/http" "dev-resources/tools/repl"]
       ;; complete project's classpath with dev-resources
       :resources-paths ["dev-resources"]
       ;; To instrument the project with the browser-repl facilities
       ;; (i.e. the ring/compojure/enlive libs) and the austin plugin
       ;; (cf. see below)
       :dependencies [[ring "1.2.1"]
                      [compojure "1.1.6"]
                      [enlive "1.1.4"]]

       ;; lib for cljs unit testing which is a maximal port of
       ;; +
       ;; clojure.test standard lib; 
       ;; +
       ;; the lib for instrumenting the brepl
       :plugins [[com.cemerick/clojurescript.test "0.2.1"]
                 [com.cemerick/austin "0.1.3"]]

       ;; Cljsbuild settings for development and test phases
       :cljsbuild
       {;; compiler optmizations options
        :builds {;; the :whitespace optimizations build is the
                 ;; only build included in the index.html page and is used
                 ;; for the browser-repl connection
                 :whitespace
                 {:source-paths ["src/cljs" "test/cljs" "dev-resources/tools/repl"]
                  :compiler
                  {:output-to "dev-resources/public/js/miscelaneous.js"
                   :optimizations :whitespace
                   :pretty-print true}}
                 ;; :simple optimizations build
                 :simple
                 {:source-paths ["src/cljs" "test/cljs"]
                  :compiler
                  {:output-to "dev-resources/public/js/simple.js"
                   :optimizations :simple
                   :pretty-print false}}
                 ;; :advanced optimizations build
                 :advanced
                 {:source-paths ["src/cljs" "test/cljs"]
                  :compiler
                  {:output-to "dev-resources/public/js/advanced.js"
                   :optimizations :advanced
                   :pretty-print false}}}

        }

       :injections [(require '[ring.server :as http :refer [run]]
                             'cemerick.austin.repls)
                    (defn browser-repl []
                      (cemerick.austin.repls/cljs-repl (reset! cemerick.austin.repls/browser-repl-env
                                                               (cemerick.austin/repl-env))))]}}

(defproject mdl-widgetry "0.1.8-alpha"
  :description "A ClojureScript lib for useful MDL widgets and utilities"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  ;; Required by cljsbuild  plugin
  :min-lein-version "2.2.0"

  ;; Add src/cljs as cljsbuild does not add its
  ;; source-paths to the project source-paths
  :source-paths ["src/clj" "src/cljs"]

  ;; Latest stable clj and cljs releases
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [cljsjs/material "1.3.0-0"]
                 [reagent "0.6.1"]
                 [web-utils-2.0/miscelaneous "0.0.2-alpha"]]

  ;; The plugins injection
  :plugins [[lein-cljsbuild "1.1.6"]]

  ;; Hook the cljsbuild subtasks to the lein tasks: lein clean, lein
  ;; compile, lein test and lein jar
  :hooks [leiningen.cljsbuild]

  ;; Lein-cljsbuild plugin configuration. Single build task:
  ;;            used for packaging any cljs sources in the jar
  ;;            generated by lein jar command
  :cljsbuild
  {:builds {
            :useless
            {:source-paths ["src/cljs"]
             ;; The :jar true option is not needed to include the CLJS
             ;; sources in the packaged jar. This is because we added
             ;; the CLJS source codebase to the Leiningen
             ;; :source-paths
             ;:jar true
             ;; Compilation Options
             :compiler
             {:output-to "dev-resources/public/js/useless.js"
              ;; Compiler optimizations option set to :none to speed
              ;; up this useless compilation
              :optimizations :none
              :pretty-print false}}}})

(ns analysis.csv
  (:require [clojure.java.io :refer [reader writer]]))

(defn- parse-entry [entry]
  (reduce str "" (map #(str %1 "," %2 "," %3 "," %4 "," (:analysis.web-page-test/objective entry) "\n")
                      (:analysis.web-page-test/env entry)
                      (:analysis.web-page-test/browser entry)
                      (:analysis.web-page-test/values entry)
                      (:analysis.web-page-test/unit entry))))

(defn- write-data [file-writer data]
  (.write file-writer (str (reduce str "" (map #(str % ",") (-> data first keys))) "\n"))
  (.write file-writer (reduce str "" (map #(parse-entry %) data))))

(defn write-csv [data & filename]
  (let [filename (or (first filename) "results.csv")]
    (with-open [file-writer (writer filename :append true)]
      (if (map? data)
        (write-data file-writer [data])
        (write-data file-writer data)))))


;; TODO: move to test
;; (defn gen-n [n]
;;   (take n (repeatedly #(rand-int 5000))))

;; (defn gen-agreg-map [n]
;;   {:env (vec (map #(str "location-" %) (gen-n n)))
;;    :objective :first-byte
;;    :values (gen-n n)
;;    :unit "s"})

;; (defn gen-data1 [n m]
;;   (repeatedly n #(gen-agreg-map m)))

;; (defn gen-map [n]
;;   {:env (str "location-" (rand-int n))
;;    :value (rand-int n)
;;    :unit "s"
;;    :objective :first-byte})

;; (defn gen-data2 [n]
;;   (repeatedly n #(gen-map 6000)))

;; (defn write-to-csv-d2 [data & filename]
;;   (let [filename (or (first filename) "result-d2-tool.csv")]
;;     (with-open [file-writer (writer filename)]
;;       ;; CSV header
;;       (write-csv file-writer (vector (-> data first keys)))
;;       ;; CSV content
;;       (write-csv file-writer (map #(vals %) data)))))

;; (defn parse-d1 [entry]
;;   (map #(vector %1 %2 (:unit entry) (:objective entry))
;;        (:env entry)
;;        (:values entry)))

;; (defn write-to-csv-d1 [data & filename]
;;   (let [filename (or (first filename) "result-d1-tool.csv")]
;;     (with-open [file-writer (writer filename)]
;;       ;; CSV header
;;       (write-csv file-writer (vector (-> data first keys)))
;;       ;; CSV content
;;       (write-csv file-writer (reduce into [] (map #(parse-d1 %) data))))))
;; (defn parse-comma-d2 [entry]
;;   (str
;;    (reduce str "" (map #(str (val %) ",") entry))
;;    "\n"))

;; (defn my-write-d2 [data & filename]
;;   (let [filename (or (first filename) "simple-result-d2.csv")]
;;     (with-open [file-writer (writer filename :append true)]
;;       (.write file-writer (str (reduce str "" (map #(str % ",") (-> data first keys))) "\n"))
;;       (.write file-writer (reduce str "" (map #(parse-comma-d2 %) data))))))

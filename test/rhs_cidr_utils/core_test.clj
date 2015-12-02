(ns rhs-cidr-utils.core-test
  (:require [clojure.test        :refer :all]
            [rhs-cidr-utils.core :refer :all]))


(deftest bad-octet-tests
  (testing "poorly formed dotted-octets"
    (is (thrown? Exception (dotted->bits "abc")))
    (is (thrown? Exception (dotted->bits "1.2.3")))))

(def legit-octet "10.11.12.13")
(def legit-int   168496141)

(deftest happy-path-octet-test
  (testing "convert legit dotted-octet string."
    (is (= legit-int (dotted->bits legit-octet)))
    (is (= 0x1020304 (dotted->bits "1.2.3.4")))))

(deftest happy-bin-to-dotted
  (testing "convert bin to dotted."
    (is (= "127.2.3.64" (bits->dotted 0x7f020340)))
    (is (= "10.11.9.32" (bits->dotted 0x0a0b0920)))))

(deftest test-width
  (testing "get width from cidr->bitmask"
    (is (= 26 ((cidr->bitmask "192.34.66.127/26") 1)))
	(is (= 5  ((cidr->bitmask "10.10.127.168/5")  1)))))

(deftest bad-cidr
  (testing "poorly formed CIDR string"
    (is (thrown? Exception (cidr->bitmask legit-octet)))))

(def cidr-mask (- legit-int 13))
(def cidr-str (str legit-octet "/24"))

(deftest happy-cidr-mask
  (testing "happy path, cidr->bitmask"
    (is (= cidr-mask  (first (cidr->bitmask cidr-str))))
    (is (= 0xcc000000 (first (cidr->bitmask "204.0.0.0/8")))) ;; 204 == 0xCC
    (is (= 0xa000000  (first (cidr->bitmask "10.0.0.0/8"))))
	(is (= 0xfe000000 (first (cidr->bitmask "255.255.255.255/7"))))
	(is (= 0x0a0b0cfc (first (cidr->bitmask "10.11.12.255/30"))))
	(is (= 0x0a0b0c10 (first (cidr->bitmask "10.11.12.16/28"))))))

(deftest within-mask
  (testing "within-mask, ip in scope"
    (is (rhs-cidr-utils.core/< (first (cidr->bitmask "10.5.0.0/16")) 16 (dotted->bits "10.5.169.250")))
    (is (rhs-cidr-utils.core/< (first (cidr->bitmask "204.0.0.0/8"))  8 (dotted->bits "204.255.255.255")))
    (is (rhs-cidr-utils.core/< 0xa000000 8 0xa0b001f)))
  (testing "ip not within cidr"
    (is (not (rhs-cidr-utils.core/< (first (cidr->bitmask "10.5.5.0/23"))  23 (dotted->bits "10.5.6.250"))))
    (is (not (rhs-cidr-utils.core/< (first (cidr->bitmask "204.0.0.0/10")) 10 (dotted->bits "204.127.45.2"))))
    (is (not (rhs-cidr-utils.core/< 0xa000000 16 0xa0b001f)))))

(deftest cidr-range
  (testing "legit cidrs"
    (let [cidr-range (get-cidr-range "10.9.0.0/16")
          doc-range  (get-cidr-range "127.64.0.0/16")]  ;; this range is in README.md.
      (is (and (= 0xa090000 (cidr-range 0)) (= 0xa09ffff (cidr-range 1)))
          (and (= 0x7f400000 (doc-range 0)) (= 0x7f40ffff (doc-range 1)))))))

(deftest geo-cidr
  (testing "missing cidr"
    (let [cidr-str "73.0.0.0/8"
          cidr-range (get-cidr-range cidr-str)]
      (println (str "Range for " cidr-str ": " cidr-range))
      (is (= 0x0ffffff (- (cidr-range 1) (cidr-range 0))))))
  (testing "missing empireblue.com"
    (let [cidr-str "162.95.221.0/25"
          cidr-range (get-cidr-range cidr-str)]
      (println (str "Range for " cidr-str ": " cidr-range))
      (is (= 127 (- (cidr-range 1) (cidr-range 0))))))
  (testing "missing 76.177.49.255"
    (let [cidr-str "76.117.0.0/16"
          cidr-range (get-cidr-range cidr-str)]
      (println (str "Range for " cidr-str ": " cidr-range))
      (is (= 0xffff (- (cidr-range 1) (cidr-range 0)))))))

(deftest cidr-contained-by
  (testing "test cidr-contained-by?"
    (is (cidr-contained-by? "10.0.8.128/25" "10.0.0.0/16"))
    (is (not (cidr-contained-by? "10.1.0.0/16" "10.2.0.0/16")))))

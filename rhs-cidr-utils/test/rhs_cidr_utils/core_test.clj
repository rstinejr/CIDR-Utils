(ns rhs-cidr-utils.core-test
  (:require [clojure.test      :refer :all]
            [rhs-cidr-utils.core :refer :all]))


(deftest bad-octet-tests
  (testing "poorly formed dotted-octets"
    (is (thrown? Exception (dotted->bits "abc")))
    (is (thrown? Exception (dotted->bits "1.2.3")))))

(def legit-octet "10.11.12.13")
(def legit-int   168496141)

(deftest happy-path-octet-test
  (testing "convert legit dotted-octet string."
    (is (= legit-int (dotted->bits legit-octet)))))

(deftest bad-cidr
  (testing "poorly formed CIDR string"
    (is (thrown? Exception (cidr->bitmask legit-octet)))))

(def cidr-mask (- legit-int 13))
(def cidr-str (str legit-octet "/24"))

(deftest happy-cidr-mask
  (testing "happy path, cidr->bitmask"
    (is (= cidr-mask  (cidr->bitmask cidr-str)))
    (is (= 0xcc000000 (cidr->bitmask "204.0.0.0/8")))))   ;; 204 == 0xCC

(deftest within-mask
  (testing "within-mask, ip in scope"
    (is (ip-within-mask? (cidr->bitmask "10.5.0.0/16") 16 (dotted->bits "10.5.169.250")))
    (is (ip-within-mask? (cidr->bitmask "204.0.0.0/8")  8 (dotted->bits "204.255.255.255"))))
  (testing "ip not within cidr"
    (is (not (ip-within-mask? (cidr->bitmask "10.5.5.0/23")  23 (dotted->bits "10.5.6.250"))))
    (is (not (ip-within-mask? (cidr->bitmask "204.0.0.0/10") 10 (dotted->bits "204.127.45.2"))))))

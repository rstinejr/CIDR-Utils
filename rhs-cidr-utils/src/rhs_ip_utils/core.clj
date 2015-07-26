(ns rhs-ip-utils.core
  (:require [clojure.string :as str]))

(defn- left-8
  [v]
  (bit-shift-left v 8))

(defn dotted->bits
  "Convert IPv4 dotted-octet string to its binary form."
  [dotted-octet]
  (let [decimal-strs (str/split dotted-octet #"\.")]
    (when (not= 4 (count decimal-strs))
      (throw (ex-info (str "Invalid dotted octet: '" dotted-octet "'") {:causes #{:invalid-parm}})))
    (let [decimals (into [] (flatten (map (fn [s] (Integer. s)) decimal-strs)))]
      (bit-or (decimals 3) (left-8 (bit-or (decimals 2) (left-8 (bit-or (decimals 1) (left-8 (decimals 0))))))))))

(defn cidr->bitmask
  "Given the string form of a CIDR, convert to its bit mask."
  [cidr-str]
  (let [parts (str/split cidr-str #"/")]
    (when (not= 2 (count parts))
      (throw (ex-info (str "Invalid CIDR '" cidr-str "', no '/'") {:causes #{:invalid-parm}})))
    (let [ip-bits (dotted->bits (first parts))
          shift-n (- 32 (Integer. (last parts)))]
      (left-8 (bit-shift-right ip-bits 8)))))

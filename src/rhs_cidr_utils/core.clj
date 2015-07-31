(ns rhs-cidr-utils.core
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

(defn bits->dotted
  "Convert a binary IP address (an int) to a dotted-octet string."
  [ip-addr]
  (when (not= 0 (bit-shift-right ip-addr 32))
    (throw (ex-info (str "Integer '" ip-addr "' out of range for IPv4."){:causes #{:invalid-parm}})))
  (format "%d.%d.%d.%d"
    (bit-shift-right ip-addr 24)
    (bit-shift-right (bit-and 0x00ff0000 ip-addr) 16)
    (bit-shift-right (bit-and 0x0000ff00 ip-addr)  8)
    (bit-and 0x000000ff ip-addr)))
     
(defn cidr->bitmask
  "Given the string form of a CIDR, return a vector containing the binary bitmask and the CIDR width."
  [cidr-str]
  (let [parts (str/split cidr-str #"/")]
    (when (not= 2 (count parts))
      (throw (ex-info (str "Invalid CIDR '" cidr-str "', no '/'") {:causes #{:invalid-parm}})))
    (let [ip-bits    (dotted->bits (first parts))
		  cidr-width (Integer. (last parts))
          shift-n    (- 32 cidr-width)]
      [(bit-shift-left (bit-shift-right ip-bits shift-n) shift-n) cidr-width])))

(defn ip-within-mask?
  "Bit operations to determine whether a binary IP address is within the scope of a CIDR."
  [cidr-mask mask-width ip]
  (let [shift    (- 32 mask-width)
        net-part (bit-shift-left (bit-shift-right ip shift) shift)]
    (= cidr-mask net-part)))

(defn get-cidr-range
  "Return a vector containing the high and low IP addresses, as Longs, witin a CIDR."
  [cidr-str]
  (let [[cidr width] (cidr->bitmask cidr-str)]
    (when (or (= 0 width)(> width 31))
      (throw (ex-info (str "Bad CIDR '" cidr-str "', width must be between 1 and 31.", {:causes #{:invalid-parm}}))))
    (let [host-max (bit-shift-right 0xffffffff width)]
      [cidr (bit-or cidr host-max)])))

(defn cidr-contained-by?
  "Given two string represenstations of CIDRs, return true iff all IPs within range of the first are within range of the second."
  [cidr-1 cidr-2]
  (let [[cidr-2-mask cidr-width] (cidr->bitmask cidr-2)]
    (= (bit-shift-right (first (cidr->bitmask cidr-1)) cidr-width) (bit-shift-right cidr-2-mask cidr-width))))

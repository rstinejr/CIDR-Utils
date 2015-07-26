# CIDR-Utils
Clojure implementations of utility functions for CIDRs and IP

**dotted-bits**  Given a string of an IP address in dotted-decimal form, return the IP address as a binary long.  
E.g., ```(dotted-bits "1.2.3.4")``` returns 0x1020304.

**cidr->bitmask** Given the string form of a CIDR, e.g., "10.0.0.0/8", return the net mask as a binary long.
E.g., ```(cidr->bitmask "10.0.0.0/8")``` returns 0xa000000.

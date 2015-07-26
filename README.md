# CIDR-Utils
Clojure implementations of utility functions for CIDRs and IP

### dotted->bits  
Given a string of an IP address in dotted-decimal form, return the IP address as a binary long.  *E.g.*,   
```
(dotted->bits "1.2.3.4")
``` 
...returns 0x1020304.

### cidr->bitmask
Given the string form of a CIDR, e.g., "10.0.0.0/8", return the net mask as a binary long. *E.g.*, 
```
(cidr->bitmask "10.0.0.0/8")
``` 
...returns 0xa000000.

### ip-within-mask?
Returns *true* if-and-only-if a CIDR mask, as a binary long and with a given width, contains an IP address. *E.g.*, 
```
(ip-within-mask? 0xa000000 8 0xa0b001f)
```
... returns *true*,  while 
```
(ip-within-mask? xa000000 16  0xa0b001f)
``` 
...returns *false*.

### get-cidr-range
Given the string representation of a CIDR, return a vector containing the lowest and highest IP address within that CIDR, as binary ints. *E.g.*,

```
(get-cidr-range "127.64.0.0/16")
```
returns *[0x7f400000 0x7f40ffff]*


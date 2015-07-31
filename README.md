# CIDR-Utils
Clojure implementations of utility functions for CIDRs and IP

### dotted->bits  
Given a string of an IP address in dotted-decimal form, return the IP address as a binary long.  *E.g.*,   
```
(dotted->bits "1.2.3.4")
``` 
...returns 0x1020304.

### bits->dotted
Given a binary IP address, generate its corresponding dotted-octet string. *E.g.*,
```
(bits->dotted 0x0a0b0920)
```
... returns *"10.11.9.32"*

### cidr->bitmask
Given the string form of a CIDR, e.g., "10.0.0.0/8", return the net mask and with as a binary long and in  within a vector. *E.g.*, 
```
(cidr->bitmask "10.0.0.0/8")
``` 
...returns [0xa000000 8].

### rhs-cidr-utils.core/<
Returns *true* if-and-only-if a CIDR mask, as a binary long and with a given width, contains an IP address. *E.g.*, 
```
(rhs-cidr-utils.core/< 0xa000000 8 0xa0b001f)
```
... returns *true*,  while 
```
(rhs-cidr-utils.core/< xa000000 16  0xa0b001f)
``` 
...returns *false*.

### get-cidr-range
Given the string representation of a CIDR, return a vector containing the lowest and highest IP address within that CIDR, as binary ints. *E.g.*,

```
(get-cidr-range "127.64.0.0/16")
```
returns *[0x7f400000 0x7f40ffff]*

### cidr-contained-by?
Given the string representation of 2 CIDRs, return *true* if and only if every IP address within range of the first is also within range of the second. *E.g.*,
```
(cidr-contained-by? "10.0.8.128/25" "10.0.0.0/16")
```
...returns *true*, while
```
(cidr-contained-by? "10.1.0.0/16" "10.2.0.0/16")
```
...returns *false*. 

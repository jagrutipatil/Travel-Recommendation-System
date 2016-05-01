underscore-x
============

Extend some features from underscore for more complicated cases. 


-------------


## Purpose
The purpose of this module is to cover some complicated cornerstone cases on some object or array transformation (or any JS features) not covered by underscore.js.

## Installation
    $ npm install underscore-x

## Quick Start

```js
    var _ = require('underscore-x');
    
    var output = {};
    
    _.extend_x(output, {a:123});

   // output is {a:123}
   
    var output = {a1:{b1:"h", b2:123}, a2:[1,2,3,4]};
    
    _extend_x(output, {a1:{b1:"b1", b3:[1,2,3], b4:"b4"}, a2:[3,4,5], a3:"xxx" });
    
    // output is {a:{b1:"b1", b2:123, b3:[123], b4:"b4"}, a2:[1,2,3,4,3,4,5], a3:"xxx"}
```

## Objects functions

**extend_x(target, sources... )**

 Merge the entire tree of source objects to the target object with the following rules:
- If multiple sources, e.g. _.extend_x(target, source1, source2...), merging will begin from left to right, i.e. source1 will merge into target first, then source2 will merge into the output (which just merged with source1).  This order is important as you can see later on.
- Every single node (or key) of the source hierarchy will be visited and merge to the target.  If target doesn't have that key, then the entire tree (or subtree) from the source will be copied over.
- If both key & value exist in target and source, then perform the followings in order:

  1. If both values of the keys are the type of "function", then copy and overwrite the function of source to target.  
  2. If both values of the keys are the type of "array", then append the array of source to the end of the array of target.
  3. If both values of the keys are the type of "object", then go through the sub-tree of source.
  4. If the value of the target key is undefined, but the value of the source key has something, then copy the value of the source to target of the same key
  5. If both values of the keys are the same type (e.g. number, string), then copy the value of the source key to target key.
  6. If both values are of different type, then do not do anything.

- Based on the rules above, if there is a key exist with value (or tree) in target but empty or undefined in source, it won't delete or erase the key nor its value in Target.
- There is no control of how many levels deep the function will visit.  It will visit all levels if neccessary. 
- The final result is in Target.






string-to-json
==============

Convert a string representation of dot-notion key into json format object.
It is particularly useful for converting req.query with dot notation as the key into json.
```js
e.g. a http query like this: 

canada.bc.yvr.name=Vancouver&canada.bc.yvr.id=1234&usa.ny.nyc.name=New%20York

or a req.query like this: 

req.query = { "canada.bc.yvr.name":"Vancouver", "canada.bc.yvr.id":1234, "usa.ny.nyc.name"="New%20York" }

will result in: {"canada":{"bc":{"yvr":{"name":"Vancouver","id":"1234"}}},"usa":{"ny":{"nyc":{"name":"New York"}}}}

```

-------------


## Purpose


## Installation
    $ npm install string-to-json

## Quick Start

```js
    var str2json = require('string-to-json');
    
    var output = str2json.convert({"abc.def.g":2});

    // result: output = {'abc':{'def':{{'g':2}}}


   var output = str2json.convert({"abc.def.g":2, "abc.def.h":[1,2,3], "abc.def.i":"xxxxx"});

   // result: output = {abc:{def:{g:2, h:[1,2,3], i:"xxxxx"}}}

```

## Upcoming

- Take input as JSON format in entire string like this:  '{"abc.def.g":123}' which passes JSON.parse();
- Take input as http query like this: 'abc.def.g=123&abc.def.h=[1,2,3]&abc.def.i=xxxxx'


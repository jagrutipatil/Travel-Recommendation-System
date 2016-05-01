var express = require('express');
var server = express();
var str2json = require('../lib/string-to-json');

server.use(express.bodyParser());

server.get("/test", function(req, res) {
    res.send("Req.query: " + JSON.stringify(str2json.convert(req.query)));
});

var port = process.env.PORT; // or whatever port
console.log("Running at port: " + port);
server.listen(port);

// try sending the query like:
//  http://localhost:<port>/test?canada.bc.yvr.name=Vancouver&canada.bc.yvr.id=1234&usa.ny.nyc.name=New%20York
// the output will be: Req.query: {"canada":{"bc":{"yvr":{"name":"Vancouver","id":"1234"}}},"usa":{"ny":{"nyc":{"name":"New York"}}}}
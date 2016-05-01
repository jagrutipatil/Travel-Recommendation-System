/*
*
*/
var str2json = require('../lib/string-to-json');

var input = { "widgets.weather.name":"xxxxx",
                "widgets.weather.id": 123} ;
                
var output = str2json.convert(input);                

console.log(JSON.stringify(output));


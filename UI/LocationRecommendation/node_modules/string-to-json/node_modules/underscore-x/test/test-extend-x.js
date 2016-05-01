var _ = require('../lib/underscore-x.js');


var obj1 = {
        widgets: {
                weather: {
                        name: "vancouver",
                        time: 12345,
                        myf1: function() { }
                },
                heros: [4444],
                diff: { mmm: "xxx"}
        }
}

var obj2 = {
        widgets: {
                weather: {
                        id: 1234242,
                        time: 9999,
                        myf1: function(data) { console.log(data); }
                },
                ga : {
                        id: 123444242455
                },
                heros: [1223, 4, 5, 6],
                diff: [2414,2423],
                myfx21: function() {}
        }
}

var output = {};
_.extend_x(output, obj1, obj2);
console.log("Output: " + JSON.stringify(output));


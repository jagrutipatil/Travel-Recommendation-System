/*
*
*/
var _ = require('underscore-x');

var recurFunc = function(arr, val) {

    // stop condition
    if (arr.length <= 0) {
            return val;
    }
    // check if array
    // pop the first item of the array;
    var first = arr[0];
    var rest = arr.slice(1);

    var result = {};
    if (_.isUndefined(result[first]) ) {
            result[first] = {};
    }

    var temp = recurFunc(rest, val);
    result[first] = temp;
    return result;
}

module.exports.convert = function(data) {
    var output = {};
    
    // Take data as an object with dot notation key
    if (_.isObject(data) && !_.isArray(data)) {
        for (var item in data) {
                if (data.hasOwnProperty(item)) {
                        var iArray = item.split(".");
                        var value = data[item];
                        _.extend_x(output, recurFunc(iArray, value));
                }
        }
    }
    
    return output;
}




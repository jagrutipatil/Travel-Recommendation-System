var _ = require('underscore');

var ArrayProto = Array.prototype, ObjProto = Object.prototype, FuncProto = Function.prototype;

  // Create quick reference variables for speed access to core prototypes.
  var
    push             = ArrayProto.push,
    slice            = ArrayProto.slice,
    concat           = ArrayProto.concat,
    toString         = ObjProto.toString,
    hasOwnProperty   = ObjProto.hasOwnProperty;

var
    nativeForEach      = ArrayProto.forEach,
    nativeMap          = ArrayProto.map,
    nativeReduce       = ArrayProto.reduce,
    nativeReduceRight  = ArrayProto.reduceRight,
    nativeFilter       = ArrayProto.filter,
    nativeEvery        = ArrayProto.every,
    nativeSome         = ArrayProto.some,
    nativeIndexOf      = ArrayProto.indexOf,
    nativeLastIndexOf  = ArrayProto.lastIndexOf,
    nativeIsArray      = Array.isArray,
    nativeKeys         = Object.keys,
    nativeBind         = FuncProto.bind;

var each = _.each = _.forEach = function(obj, iterator, context) {
    if (obj == null) return;
    if (nativeForEach && obj.forEach === nativeForEach) {
      obj.forEach(iterator, context);
    } else if (obj.length === +obj.length) {
      for (var i = 0, l = obj.length; i < l; i++) {
        if (iterator.call(context, obj[i], i, obj) === breaker) return;
      }
    } else {
      for (var key in obj) {
        if (_.has(obj, key)) {
          if (iterator.call(context, obj[key], key, obj) === breaker) return;
        }
      }
    }
  };


_.extend_x = function(obj) {
	var pc = function(target, source) {
		for (var prop in source) {
			if (target[prop] === void 0) {
				target[prop] = source[prop];
			} else {
				var targetType = ( _.isUndefined(target[prop]))? "undefined" : _.isArray(target[prop])? "array" : _.isFunction(target[prop])? "function" : _.isObject(target[prop])? "object" : toString.call(target[prop]);
				var sourceType = ( _.isUndefined(source[prop]))? "undefined" : _.isArray(source[prop])? "array" : _.isFunction(source[prop])? "function" : _.isObject(source[prop])? "object" : toString.call(source[prop]);
				
				// only look if type ==
				if (targetType == "function" && sourceType == "function") {
					target[prop] = source[prop];
				} else if (targetType == "array" && sourceType == "array") {
					target[prop] = target[prop].concat(source[prop]);
				} else if (targetType == "object" && sourceType == "object"){
					pc(target[prop], source[prop] );
				} else if (targetType == "undefined" && sourceType != "undefined" ) {
					target[prop] = source[prop];
				} else if (targetType == sourceType ){
					target[prop] = source[prop];
				} else {}
			}
		}
	}
    each(slice.call(arguments, 1), function(source) {
      if (source) {

	pc (obj, source);
      }
    });
    return obj;
  };

module.exports = _;

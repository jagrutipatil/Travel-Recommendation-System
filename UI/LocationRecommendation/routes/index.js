
/*
 * GET home page.
 */
var ejs = require("ejs");
var mysql = require('./MySQL');
var session=require('client-sessions');
var request = require('sync-request');

var https = require("https");
var http = require("http");

exports.homePage=function(req,res) {
	ejs.renderFile('./views/index.ejs',function(err3, result3) {
        // render on success
        if (!err3) {
            res.end(result3);
        }
        // render or error
        else {
            res.end('An error occurred');
            console.log(err3);
        }
    });	
	
};

exports.GetLoginPage=function(req,res) {
	ejs.renderFile('./views/Signin.ejs',function(err3, result3) {
        // render on success
        if (!err3) {
            res.end(result3);
        }
        // render or error
        else {
            res.end('An error occurred');
            console.log(err3);
        }
    });	
	
};

exports.GetSignUpPage=function(req,res) {
	ejs.renderFile('./views/Signup.ejs',function(err3, result3) {
        // render on success
        if (!err3) {
            res.end(result3);
        }
        // render or error
        else {
            res.end('An error occurred');
            console.log(err3);
        }
    });	
	
};



exports.Login=function(req,res) {
	var getUser="select * from users where username='"+req.param("email")+"' and password='" + req.param("password") +"'";
	console.log("Query is: "+getUser);
	
	mysql.fetchData(function(err,results){
		if(err){
			throw err;
		}
		else 
		{
			if(results.length > 0){
				
				console.log("valid Login");
				console.log(results);
				req.session.userId=results[0].userid;
				req.session.username=req.param("username");
				console.log("User Id is..........."+req.session.userId);
				json_responses={"statusCode":200};
				console.log(json_responses);
				ejs.renderFile('./views/UserHomepage.ejs',function(err, result) {
					if (!err) {
						console.log("redirecting to user home page");
						res.end(result);
					}
					else {
						res.end('An error occurred');
						console.log(err);
					}
				});
			}
			else {    
				
				console.log("Invalid Login");
				json_responses = {"statusCode" : 401};
				res.send(json_responses);
			}
		}  
	},getUser);
};

exports.redirectToHome=function(req,res) {
	console.log("inside redirectToHome function");
	ejs.renderFile('./views/UserProfile.ejs',function(err, result) {
		if (!err) {
			console.log("redirecting to user home page");
			res.end(result);
		}
		else {
			res.end('An error occurred');
			console.log(err);
		}
	});
}




exports.Signup=function(req,res) {
	var getUser="select * from users where username='"+req.param("email")+"'";
	var Message="";	
	mysql.fetchData(function(err,results){
		if(err){
			throw err;
//			var json_responses = {"statusCode" : 500};
//			res.send(json_responses);
		}
		else 
		{
			if(results.length > 0)
			{
				console.log("User is already present");
				//Message="User is already present";
				var json_responses = {"statusCode" : 401};
				console.log(json_responses);
				res.send(json_responses);
			}
			else {    
				
				console.log("Inserting into the database");
				var getcount="SELECT COUNT(*) FROM User";
				
				var Fname=req.param("firstname");
				var Lname=req.param("lastname");
				var Email=req.param("email");
				var Password=req.param("password");
				var confirmPassword=req.param("confirmPassword");
				
				var putUser="insert into users(firstname,lastname,username,password) values('"+Fname+"','"+Lname+"','"+Email+"','"+Password+"');";
				mysql.fetchData(function(err2,result2){
				      if(err2){
					        throw err2;
				       }
				      else 
				      {
				    	   json_responses = {"statusCode" : 200};
							console.log(json_responses);
							res.send(json_responses);
				    	  
				      }
			    },putUser);         
				
			}
		}  
	},getUser);
};

////Without preference
exports.getData=function(req,res) {
			var str='http://localhost:8081/restlet/test/'+req.session.userId;
			console.log("api call is......."+str);
			var httpcall = request('GET', str, {
			  'headers': {
			    'user-agent': 'example-user-agent'
			  }
			});
			console.log("Sync call");
			console.log(httpcall.getBody('utf8'));
			var json_responses = {"Status" : "success","JsonData" : httpcall.getBody('utf8')};
			res.send(json_responses);
}

exports.logout=function(req,res) {
	req.session.userId="";			
	res.send(json_responses);
	var json_responses = {"Status" : "success"};
	res.send(json_responses);
}

//with country state and type
exports.getData1=function(req,res) {

		var fcountry=req.param("country");
		var fstate=req.param("state");
		var ftype=req.param("type");
		var userId1=req.session.userId + "";

		console.log("Country: " + fcountry);
		console.log("State: " + fstate);
		console.log("Type: " + ftype);
		console.log("User ID: " + userId1);
		
		var httpcall = request('POST', 'http://localhost:8081/restlet/test', {
			  json: { country: fcountry,
				  	  state:fstate,
				  	  type:ftype,
				  	  userId:userId1
				  }
			});
			
		console.log("Sync call in getData1");
		console.log(httpcall.getBody('utf8'));		
		var json_responses = {"Status" : "success","JsonData" : httpcall.getBody('utf8')};
		res.send(json_responses);
			 
}


exports.saveDataToDb = function(req,res) {
	
	var text = JSON.stringify(req.param("data"));	
	var obj = JSON.parse(text);			
	
	console.log("******************Data...yayyy !!!!"+ text);
	console.log("User ID from session is "+req.session.userId);
	
//	for (var i = 0; i < result["id"].size; i++) {
//		console.log(result.get("id"));
//	}
//	        console.log("Item name: " + result[i].id);
//			console.log("Item name: " + result[i].created_time);
//	        // console.log("Source: "+result[i][name].sourceUuid);
//			// console.log("Target: "+result[i][name].targetUuid);
}


exports.getUID = function(req,res) {	
	var uid = req.param("uid");
	
	console.log("******************user id in node...yayyy !!!!" + uid);
	console.log("User ID from session is "+req.session.userId);	
}

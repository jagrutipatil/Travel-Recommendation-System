
/*
 * GET home page.
 */
var ejs = require("ejs");
var mysql = require('./MYSQL');
var session=require('client-sessions');
var request = require('sync-request');

var https = require("https");
var http = require("http");

exports.homePage=function(req,res)
{
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

exports.GetLoginPage=function(req,res)
{
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

exports.GetSignUpPage=function(req,res)
{
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



exports.Login=function(req,res)
{
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

exports.redirectToHome=function(req,res)
{
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




exports.Signup=function(req,res)
{
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
exports.getData=function(req,res)
{
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

//with country state and type
exports.getData1=function(req,res)
{

		var fcountry=req.param("country");
		var fstate=req.param("state");
		var ftype=req.param("type");
		var userId1=req.session.userId + "";
		var str='http://localhost:8081/restlet/test/'+req.session.userId;
		console.log("User ID from session is "+req.session.userId);	
		var httpcall = request('GET', str, {
			  'headers': {
			    'user-agent': 'example-user-agent'
			  }
			});
//		var httpcall = request('POST', 'http://localhost:8081/restlet/test', {
//			  json: { country: fcountry,
//				  state:fstate,
//				  type:ftype,
//				  userId:req.session.userId}
//			});
			
			console.log("Sync call in getData1");
			console.log(httpcall.getBody('utf8'));
			var str={"forms":[{"maxTemp":66.0,"desc":"Historical Afghanistan","address":"Proich , Badakhshan , Afghanistan","name":"Proich","state":"Badakhshan","locationId":9,"minTemp":14.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":26.0,"desc":"Historical Afghanistan","address":"Eil , Badakhshan , Afghanistan","name":"Eil","state":"Badakhshan","locationId":10,"minTemp":14.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":48.0,"desc":"Historical Afghanistan","address":"Preshab , Badakhshan , Afghanistan","name":"Preshab","state":"Badakhshan","locationId":4,"minTemp":17.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":27.0,"desc":"Historical Afghanistan","address":"Aih , Badakhshan , Afghanistan","name":"Aih","state":"Badakhshan","locationId":5,"minTemp":14.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":26.0,"desc":"Historical Afghanistan","address":"Tass , Badakhshan , Afghanistan","name":"Tass","state":"Badakhshan","locationId":3,"minTemp":16.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":54.0,"desc":"Historical Afghanistan","address":"Brurjs , Badakhshan , Afghanistan","name":"Brurjs","state":"Badakhshan","locationId":2,"minTemp":12.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":36.0,"desc":"Historical Afghanistan","address":"Fljif , Badakhshan , Afghanistan","name":"Fljif","state":"Badakhshan","locationId":8,"minTemp":20.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":35.0,"desc":"Historical Afghanistan","address":"Jwaik , Badakhshan , Afghanistan","name":"Jwaik","state":"Badakhshan","locationId":7,"minTemp":10.0,"type":"Historical","currency":"INR","country":"Afghanistan"},{"maxTemp":68.0,"desc":"Historical Afghanistan","address":"Kounn , Badakhshan , Afghanistan","name":"Kounn","state":"Badakhshan","locationId":6,"minTemp":19.0,"type":"Historical","currency":"INR","country":"Afghanistan"}]};
			var json_responses = {"Status" : "success","JsonData" : httpcall.getBody('utf8')};
			 res.send(json_responses);
			 
}

exports.getUID = function(req,res){
	
	
	var uid=req.param("uid");
	
	console.log("******************user id in node...yayyy !!!!"+uid);
	console.log("User ID from session is "+req.session.userId);
	
}
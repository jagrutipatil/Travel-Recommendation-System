
/*
 * GET home page.
 */
var ejs = require("ejs");
var mysql = require('./MYSQL');
var session=require('client-sessions');


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
	var getUser="select * from admin where Username='"+req.param("email")+"' and Password='" + req.param("password") +"'";
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
				req.session.username=req.param("username");
				//console.log(req.session.username);
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
	var getUser="select * from admin where Username='"+req.param("email")+"'";
	var Message="";	
	mysql.fetchData(function(err,results){
		if(err){
			throw err;
		}
		else 
		{
			if(results.length > 0)
			{
				console.log("User is already present");
				Message="User is already present";
				ejs.renderFile('./views/Error.ejs', {Message : Message}, function(err, result) {
			        if (!err) {
			            res.end(result);
			        }
			        else {
			            res.end('An error occurred');
			            console.log(err);
			        }
			    });
			}
			else {    
				
				console.log("Inserting into the database");
				var getcount="SELECT COUNT(*) FROM User";
				
				var Fname=req.param("firstname");
				var Lname=req.param("lastname");
				var Email=req.param("email");
				var Password=req.param("password");
				var confirmPassword=req.param("confirmPassword");
				
				var putUser="insert into admin values('"+Email+"','"+Password+"','"+Fname+"','"+Lname+"');";
				mysql.fetchData(function(err2,result2){
				      if(err2){
					        throw err2;
				       }
				      else 
				      {
				    	  Message="User is added successfully please login with the credentials";
				    	  ejs.renderFile('./views/Signup.ejs',{Message : Message},function(err3, result3) {
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
				      }
			    },putUser);         
				
			}
		}  
	},getUser);
};

//exports.getRecomm=function(req,res)
//{
//
//	
//}



exports.getData=function(req,res)
{

	var options = {
			  host: 'localhost',
			  port: 8081,
			  path: '/restlet/test',
			  method: 'GET'
			};

			http.request(options, function(res) {
			  console.log('STATUS: ' + res.statusCode);
			  console.log('HEADERS: ' + JSON.stringify(res.headers));
			  
			  res.setEncoding('utf8');
			  var obj;
			 // console.log('data:'+JSON.stringify(res.data));
			  res.on('data', function (chunk) {
				  
				  console.log('BODY: ' + chunk);
				  console.log('********JSON Data*******' );
				  req.session.storeItems=JSON.parse(chunk);
				  var obj=JSON.parse(chunk);
				  console.log("Json data is*******");
				  for(var i=0;i<obj.forms.length;i++)
					  {
					  console.log(obj.forms[i]);
					  }
				  
			  });
			  console.log('out of res.on function before send');
			  //console.log(obj);
			  //res.send(obj); 
			}).end();
			
			
	

}

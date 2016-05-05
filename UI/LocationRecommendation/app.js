
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , http = require('http')
  , https = require('https')
  , path = require('path')
, session = require('client-sessions');
var index=require('./routes/index.js');
//var sample=require('./routes/sample.js');


var app = express();
app.use(session({   
	  
	cookieName: 'session',    
	secret: 'cmpe273_test_string',    
	duration: 30 * 60 * 1000,    
	activeDuration: 5 * 60 * 1000,  }));

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', index.homePage);
app.post('/LoginPage', index.GetLoginPage);
app.post('/SignUpPage',index.GetSignUpPage);

app.post('/Signup',index.Signup);
app.post('/Login',index.Login);
app.post('/getUID',index.getUID);
app.post('/success_login',index.redirectToHome);

//////



app.get('/getData',index.getData);
app.post('/getData1',index.getData1);

//////


//app.get('/test','http://localhost:8081/restlet/test');
//app.get('/users', user.list);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});

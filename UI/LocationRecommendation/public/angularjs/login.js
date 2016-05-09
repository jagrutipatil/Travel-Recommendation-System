//loading the 'login' angularJS module
var login = angular.module('LocationRecommendation', []);
//defining the login controller
login.controller('LocationCtrl', function($scope, $http) {
		
	$scope.UserAlreadyPresent = true;
	//$scope.unexpected_error = true;
	$scope.SuccessSignup=true;
	
	$scope.invalid_login = true;
	$scope.unexpected_error = true;
	//$scope.recommendation[]=new 
	$scope.signin = function() {
		$http({
			method : "POST",
			url : '/Login',
			data : {
				"email" : $scope.email,
				"password" : $scope.password
			}
		}).success(function(data) {	       
			if(data.statusCode==401)
				{
					$scope.invalid_login = false;
				}
			
		}).error(function(error) {
			console.log(error);
		});
	};

	////get data
	$scope.getData = function() {
		$http({
			method : "GET",
			url : '/getData',
		}).success(function(data) {
			$scope.status=data.Status;
			var temp=JSON.parse(data.JsonData);
			$scope.storeItems = temp.forms;		
		}).error(function(error) {
			console.log(error);
		});
	};
	
	////get data with preference
	$scope.getData1 = function() {
		$http({
			method : "POST",
			url : '/getData1',
			data : {
				"country" : $scope.country,
				"state" : $scope.state,
				"type":$scope.type
			}
		}).success(function(data) {
			$scope.status=data.Status;
			var temp=JSON.parse(data.JsonData);
			$scope.storeItems = temp.forms;		
		}).error(function(error) {
			console.log(error);
		});
	};

	$scope.logout = function() {
		$http({
			method : "POST",
			url : '/logout',
			data : {}
		}).success(function(data) {
		}).error(function(error) {
			console.log(error);
		});
	};
	
	$scope.Signup = function() {
		$http({
			method : "POST",
			url : '/Signup',
			data : {
				"firstname":$scope.firstname,
				"lastname":$scope.lastname,
				"email" : $scope.email,
				"password" : $scope.password,
				"confirmPassword":$scope.confirmPassword
			}
		}).success(function(data) {
			//console.log("inside success function");
			alert("inside success signup");
			alert(data.statusCode);
			//checking the response data for statusCode
			if (data.statusCode == 401) {
				$scope.UserAlreadyPresent = false;
				$scope.unexpected_error = true;
				$scope.SuccessSignup=true;
				//window.location.assign("/failLogin");
				window.alert("Invalid login");
			}
			else if(data.statusCode == 200)
				{
				$scope.UserAlreadyPresent = true;
				$scope.unexpected_error = true;
				$scope.SuccessSignup=false;
				//Making a get call to the '/redirectToHomepage' API
				//window.location = '/LoginPage';
				}
				 
		}).error(function(error) {
			$scope.unexpected_error = false;
			$scope.UserAlreadyPresent = true;
			$scope.SuccessSignup=true;
		});
	};	
})


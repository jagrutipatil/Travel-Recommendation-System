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
	       
			alert(data.StatusCode);
			if(data.statusCode==401)
				{
					$scope.invalid_login = false;
				}
			
		}).error(function(error) {
			alert(JSON.stringify(error));
		});
	};

	////get data
	$scope.getData = function()
	{
		alert("inside getData");
		$http({
			method : "GET",
			url : '/getData',
			data : {
				"country" : $scope.country,
				"state" : $scope.state,
				"type":$scope.type
			}
		}).success(function(data)
		{
			// checking the response data for statusCode
			$scope.status=data.Status;
			alert($scope.status);
			var temp=JSON.parse(data.JsonData);
			$scope.storeItems = temp.forms;
			alert(JSON.stringify($scope.storeItems));
			alert($scope.storeItems[0].maxTemp);
			//$route.reload();
		
		}).error(function(error)
		{
			alert("error");
		});
	};
	
	////get data with preference
	$scope.getData1 = function()
	{
		alert("inside getData1");
		$http({
			method : "POST",
			url : '/getData1',
			data : {
				"country" : $scope.country,
				"state" : $scope.state,
				"type":$scope.type
			}
		}).success(function(data)
		{
			// checking the response data for statusCode
			$scope.status=data.Status;
			alert($scope.status);
			var temp=JSON.parse(data.JsonData);
			$scope.storeItems = temp.forms;
			alert(JSON.stringify($scope.storeItems));
			alert($scope.storeItems[0].maxTemp);
			//$route.reload();
		
		}).error(function(error)
		{
			alert("error");
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


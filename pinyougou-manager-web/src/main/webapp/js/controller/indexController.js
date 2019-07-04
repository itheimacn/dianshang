app.controller("indexController",function(loginService,$scope){
	$scope.showLoginName=function(){
		loginService.loginName().success(
		function(response){
			$scope.loginName=response.loginName;
		}		
		);
	}
	
});
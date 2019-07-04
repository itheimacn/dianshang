 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$scope.reg=function(){
		if($scope.password!=$scope.entity.password){
			alert("两次输入的密码不一致，请重新输入")
			$scope.password=""
			$scope.entity.password=""
				return;
		}
		userService.add($scope.entity,$scope.sysCode).success(function(response){
			alert(response.message);
		});
	}
	
	$scope.sendCode=function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请输入手机号");
			return;
		}
		userService.sendCode($scope.entity.phone).success(function(response){
			alert(response.message);
		});
	}
});	

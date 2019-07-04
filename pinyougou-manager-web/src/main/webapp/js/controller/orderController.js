app.controller('orderController',function($scope,$controller,orderService){
	$controller("baseController",{$scope:$scope});
	
	$scope.searchEntity={};
	//分页查询
	$scope.search=function(page,size){
		
		orderService.findList(page,size,$scope.searchEntity).success(
			function(response){
				
				$scope.list=response.rows;//显示当前页数据 	
				$scope.paginationConf.totalItems=response.total;//更新总记录数 
			}		
		);	
		
	}
	
	$scope.payTypeStr=['','在线支付','货到付款'];

	$scope.payStyle=['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];
	
	$scope.sources=['','app端','pc端','M端','微信端','手机qq端'];
	
	
	//状态
	  $scope.payStyleList=[{id:1,text:'未付款'},{id:2,text:'已付款'},{id:3,text:'未发货'},{id:4,text:'已发货'},{id:5,text:'交易成功'},{id:6,text:'交易关闭'},{id:7,text:'待评价'}];
	//付款方式
	  $scope.sourceTypeList=[{id:1,text:'app端'},{id:2,text:'pc端'},{id:3,text:'M端'},{id:4,text:'微信端'},{id:5,text:'手机qq端'}];
	  
	 
	  //批量删除
	  $scope.delete=function(){
		  orderService.delete($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
				}else{
					alert(response.message)

				}
			}	  
		  );
	  }
});
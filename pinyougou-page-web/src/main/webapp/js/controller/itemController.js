app.controller('itemController',function($scope,$http){
	
	$scope.num=1;
	
	$scope.specificationItems={};
	
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
	}
	
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};
	
	$scope.loadSku=function(){
		$scope.sku=skuList[0];		
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	//匹配两个对象是否相等
	$scope.matchObject=function(map1,map2){
		for(var key in map1){
			if(map1[key]!=map2[key]){
				return false;
			}
		}
		for(var key in map2){
			if(map2[key]!=map1[key]){
				return false;
			}
		}
		return true;
	}
	//根据规格查找sku
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
		if($scope.matchObject(skuList[i].spec,$scope.specificationItems)){
			$scope.sku=skuList[i];
			return ;
		}	
		}
		
	}
	
	//添加购物车
	//跨域请求
	$scope.addToCart=function(){
		//alert("skuId"+$scope.sku.id)
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
				function(response){
					if(response.success){
						location.href='http://localhost:9107/cart.html';
					}else{
						alert(response.message);
					}
				}
				
		);
		
	}
	
});
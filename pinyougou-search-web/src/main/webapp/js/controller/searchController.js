app.controller('searchController',function($scope,searchService,$location){
	
	//定义搜索对象的结构  category:商品分类
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortFiled':''};
	
	//搜索
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;	
				//$scope.searchMap.pageNo=1;
				//构建分页
				buildPageLabel();
				
			}
		);		
	}
	
	//构建分页
	buildPageLabel=function(){
		$scope.pageLabel=[];
		var firstPage=1;
		var lastPage=$scope.resultMap.totalPages;
		if($scope.resultMap.totalPages>5){
			if($scope.searchMap.pageNo<=3){
				lastPage=5;
			}else if($scope.searchMap.pageNo>=($scope.resultMap.totalPages-2)){
				firstPage=$scope.resultMap.totalPages-4;
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	
	//添加搜索项  改变searchMap的值
	$scope.addSearchItem=function(key,value){
		
		if(key=='category' || key=='brand' || key=='price'){//如果用户点击的是分类或品牌
			$scope.searchMap[key]=value;
			
		}else{//用户点击的是规格
			$scope.searchMap.spec[key]=value;		
		}
		$scope.search();//查询
	}
	
	//撤销搜索项
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand' || key=='price'){//如果用户点击的是分类或品牌
			$scope.searchMap[key]="";
		}else{//用户点击的是规格
			delete $scope.searchMap.spec[key];		
		}
		$scope.search();//查询
	}
	
	$scope.queryByPage=function(pageNo){
		if(pageNo<1 || pageNo>$scope.resultMap.totalpages){
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.isLastPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.firstDot=function(){
		if($scope.searchMap.pageNo<4){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.lastDot=function(){
		if($scope.searchMap.pageNo>$scope.resultMap.totalPages-3){
			return true;
		}else{
			return false;
		}
	}
	//排序
	$scope.sortSearch=function(sortFiled,sort){
		$scope.searchMap.sortFiled=sortFiled;
		$scope.searchMap.sort=sort;
		$scope.search();//查询
	}
	
	$scope.keywordsIsBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords= $location.search()['keywords'];
		$scope.search();//查询

	}
	
});
app.controller('contentController',function($scope,contentService){
	
	$scope.contentList=[];
	$scope.findCategoryList=function(categoryId){
		
		contentService.findCategoryList(categoryId).success(
				function(response){
					
					$scope.contentList[categoryId]=response;
				}
		);
	}
	
	$scope.search=function(){
		location.href='http://localhost:9104/search.html#?keywords='+$scope.keywords;
	}
	
})
app.service('contentService',function($http){
	
	this.findCategoryList=function(categoryId){
		
	return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
		
	}
	
});
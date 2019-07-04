app.service('orderService',function($http){
	this.findList=function(page,size,searchEntity){
		
		return $http.post('../order/findList.do?page='+page+'&size='+size,searchEntity);
	}
	
	this.delete=function(selectIds){
		return $http.get('../order/delete.do?ids='+selectIds);
	}
});
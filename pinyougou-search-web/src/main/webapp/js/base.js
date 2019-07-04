var app = angular.module('pinyougou', []);

app.filter('trustHtml',['$sce',function($sce){
	return function(data){//传入的参数需要被过滤的内容
		return $sce.trustAsHtml(data);//返回过滤后的内容（信任html的转换）
	}
}])
 //控制层 
app.controller('seckillGoodsController' ,function($scope,$location,$interval,seckillGoodsService){	
	 //读取列表数据绑定到表单中  
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list=response;
			}			
		);
	} 
	
	
	//查询实体 
	$scope.findOne=function(){	
		seckillGoodsService.findOne($location.search()['id']).success(
			function(response){
				$scope.entity= response;	
				
				allSecond=Math.floor( (  new Date($scope.entity.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
				time=$interval=(function(){
					if(allSecond>0){
						allSecond=allSecond-1;
						$scope.timeString=convertTimeString(allSecond);
					}
					if(allSecond<0){
						$interval.cancel(time);
						alert("秒杀已结束");
					}
				},1000);
				
			}
		);				
	}
	
	//转换秒为   天小时分钟秒格式  XXX天 10:22:33
	convertTimeString=function(allsecond){
		var days= Math.floor( allsecond/(60*60*24));//天数
		var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
		var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		var timeString="";
		if(days>0){
			timeString=days+"天 ";
		}
		return timeString+hours+":"+minutes+":"+seconds;
	}
});
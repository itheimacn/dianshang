 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	
	$scope.findOne=function(){
		var id= $location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages)
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);	
				
				for( var i=0;i<$scope.entity.itemList.length;i++ ){
					$scope.entity.itemList[i].spec = 
					JSON.parse( $scope.entity.itemList[i].spec);		
								}	
			}
		);				
	}
	
	//保存 
	$scope.save=function(){	
		$scope.entity.goodsDesc.introduction=editor.html();

		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
		        	location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
			function(response){
				if(response.success){
					$scope.image_entity.url= response.message;
					alert(response.message);
				}else{
					alert(response.message);					
				}
			}		
		);	
	}
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}}
	$scope.addToentity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
	$scope.deleEntity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//查询以及商品分类
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCat1List=response;
		});
	}
	
	//查询二级商品分类
	
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat2List=response;
			$scope.itemCat3List=[];
		});
	});
	
	//查询三级商品分类
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat3List=response;
		});
	});
	
	//获取模板id
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.goods.typeTemplateId=response.typeId;
		});
	});
	
	//关联品牌
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.typeTemplate=response;
          $scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds);
          if($location.search()['id']==null){
        	  
        	  $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)
          }
		}
		
		
	);		
	
	
		typeTemplateService.findSpecList(newValue).success(function(response){
			$scope.specList=response;
			alert($scope.specList)
			
		});
	
	});
	//更改规格选项，多选和取消
	$scope.updateSpecAttribute=function($event,name,value){
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
				}else{//取消勾选
						object.attributeValue.splice(object.attributeValue.indexOf(value),1);
						//如果选项都取消了，删除整条记录删除
						if(object.attributeValue.length==0){
							$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
						}
							}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
		}
	}
	
	//创建sku列表
	
	$scope.createItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}]
		
		var items=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}
	
	addColumn=function(list,columnNames,columnValues){
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<columnValues.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow))
				newRow.spec[columnNames]=columnValues[j];
				newList.push(newRow);
			}
		} 
		return newList;
	}
	
	$scope.status=['未审核','已审核','审核未通过','已关闭'];
	
	
	$scope.itemCatList=[];
	//查询商品分类列表
	$scope.findItemList=function(){
		itemCatService.findAll().success(function(response){
			for(var i=0;i<response.length;i++){
				$scope.itemCatList[response[i].id]=response[i].name;
				
			}
		});
	}
	
	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var item=$scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(item,'attributeName',specName);
		if(object!=null){
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	

	
	
});	

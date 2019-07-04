package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;

import entity.PageResult;
import result.Result;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;
	@RequestMapping("/findList")
	public PageResult findList(@RequestBody TbOrder order,int page,int size){
		
		//System.out.println(order.getReceiver());
		System.out.println(order.getOrderId());
		
		return  orderService.findPage(order,page, size);
		 
	}
	
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			orderService.delete(ids);
			return new Result(true, "新增成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "新增失败");

		}
	}
}

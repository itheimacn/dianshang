package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;


import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import result.Result;
import util.IdWorker;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;
	
	@RequestMapping("/createNative")
	public Map createNative() {
		//获取登录用户名
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		//从redis中获取日志信息
		TbPayLog payLog = orderService.searchPayLogFromRedis(username);
		if (payLog!=null) {
			
			return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
		}else {
			return new HashMap<>();
		}
	}
	
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		Result result=null;
		int i=0;
		while(true) {
			Map map = weixinPayService.queryPayStatus(out_trade_no);
			if (map.get("trade_state").equals("SUCCESS")) {
				 result=new Result(true,"支付成功");
				 orderService.updateOrderStatus(out_trade_no, (String) map.get("transaction_id"));
				break;
			}
			if (map==null) {
				result=new Result(false, "支付发生错误");
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
			if (i>=100) {
				result =new Result(false, "支付超时");
				break;
			}
			}
		return result;
		}
		
}

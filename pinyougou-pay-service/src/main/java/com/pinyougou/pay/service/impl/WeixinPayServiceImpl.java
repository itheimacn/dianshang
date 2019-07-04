package com.pinyougou.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;

import util.HttpClient;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
	@Value("${appid}")
	private String appid;
	@Value("${partner}")
	private String mch_id;
	@Value("${partnerkey}")
	private String partnerkey;
	
	@Override
	public Map createNative(String out_trade_no, String total_fee) {
		Map param = new HashMap<>();
		param.put("appid", appid);
		param.put("mch_id",mch_id);
		param.put("nonce_str",WXPayUtil.generateNonceStr());
		param.put("body", "品优购");
		param.put("out_trade_no", out_trade_no);
		param.put("total_fee",total_fee);
		param.put("spbill_create_ip", "127.0.0.1");
		param.put("notify_url", "http://www.itcast.cn");
		param.put("trade_type", "NATIVE");
		
		try {
			String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
			System.out.println(xmlParam);
			
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			client.setHttps(true);
			client.setXmlParam(xmlParam);
			client.post();
			String content = client.getContent();
			Map<String, String> paramMap = WXPayUtil.xmlToMap(content);
			Map map = new HashMap<>();
			map.put("code_url", paramMap.get("code_url"));
			map.put("out_trade_no", out_trade_no);
			map.put("total_fee", total_fee);
			
			return map;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<>();
		}
		
	}

	@Override
	public Map queryPayStatus(String out_trade_no) {
		//封装参数
		Map param = new HashMap<>();
		param.put("appid", appid);
		param.put("mch_id",mch_id);
		param.put("out_trade_no", out_trade_no);
		param.put("nonce_str",WXPayUtil.generateNonceStr());
		try {
			String xmlParam = WXPayUtil.generateSignedXml(param,partnerkey);
			//发送请求
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			client.setHttps(true);
			client.setXmlParam(xmlParam);
			client.post();
			//获取结果
			String content = client.getContent();
			Map<String, String> map = WXPayUtil.xmlToMap(content);
			return map;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
	}

}

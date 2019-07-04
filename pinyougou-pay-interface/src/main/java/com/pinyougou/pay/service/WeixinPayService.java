package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
 
	/**
	 * 生成威胁你支付二维码
	 * @param out_trade_no
	 * @param total_fee
	 * @return
	 */
	Map createNative(String out_trade_no,String total_fee);
	/**
	 * 根据订单号查询订单状态
	 * @param out_trade_no
	 * @return
	 */
	Map queryPayStatus(String out_trade_no);
}

package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	@Override
	public List<Cart> addGoodsToCart(List<Cart> cartList, Long itemId, Integer num) {
		//1.根据商铺skuId查询商品
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			throw new RuntimeException("商品不存在");
		}if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态不合法");
		}
		//2.得到商家Id
		String sellerId = item.getSellerId();
		
		//3根据商家Id查询购物车列表是否有该商家
		Cart cart = searchCartBySellerId(cartList,sellerId);
		if (cart==null) {//4如果没有该购物车
			//4.1创建购物车对象
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			List<TbOrderItem> orderItemList = new ArrayList<>();//创建购物车明细列表
			//创建新的购物车明细对象
			TbOrderItem orderItem = createOrderItem(item,num);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList );
			
			//4.2 并把购物车对象添加到购物车商品列表中
			cartList.add(cart);
		}else {//5 如果有

			// 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			if (orderItem==null) {
				//5.1. 如果没有，新增购物车明细对象
				orderItem=createOrderItem(item,num);
				cart.getOrderItemList().add(orderItem);
				
			}else {
				//5.2. 如果有，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
				
				//如果数量操作后小于等于0，则移除
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				
				//如果移除后cart的明细数量为0，则将cart移除
				if (cart.getOrderItemList().size()==0) {
					cartList.remove(cart);
				}
			}
		}

		return cartList;
	}
	//根据商家id查询购物车列表中的购物车对象
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		for(Cart cart :cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
			
		}
		return null;
	}
	
	// 查询购物车明细列表中是否存在该商品
	public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId) {
		for(TbOrderItem orderItem :orderItemList) {
			if (orderItem.getItemId().longValue()==itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	//创建订单明细
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		
		if(num<=0){
			throw new RuntimeException("数量非法");
		}
		
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	@Autowired
	private RedisTemplate redisTemplate;
	//从redis中提取购物车
	@Override
	public List<Cart> findCartListFromRedis(String userName) {
		System.out.println("从redis中提取购物车数据....."+userName);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
		if (cartList==null) {
			cartList=new ArrayList<>();
		}
		return cartList;
	}
	//把购物车列表存入redis

	@Override
	public void saveCartListToRedis(String userName, List<Cart> cartList) {
		System.out.println("向redis存入购物车数据....."+userName);
		redisTemplate.boundHashOps("cartList").put(userName, cartList);
		
	}
	
	//合并两个购物车列表
	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		System.out.println("合并购物车");
		for(Cart cart :cartList2) {
			for(TbOrderItem orderItem :cart.getOrderItemList()) {
				cartList1 = addGoodsToCart(cartList1,orderItem.getItemId(), orderItem.getNum());
			}
		}
		return cartList1;
	}

	
}

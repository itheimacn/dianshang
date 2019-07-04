package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

public interface CartService {

	
	public List<Cart> addGoodsToCart(List<Cart> cartList,Long itemId,Integer num);
	//从redis中提取购物车
	List<Cart> findCartListFromRedis(String userName);
	
	//把购物车列表存入redis
	
	void saveCartListToRedis(String userName,List<Cart> cartList);
	
	//合并两个购物车列表，cookie和redis
	List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}

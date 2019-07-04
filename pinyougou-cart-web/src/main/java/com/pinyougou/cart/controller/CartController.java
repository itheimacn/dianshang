package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import result.Result;

@RestController
@RequestMapping("/cart")
public class CartController {
	@Reference(timeout=6000)
	private CartService cartService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("从cookie中提取购物车");
		String cartList = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartList==null || cartList=="") {
			cartList="[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartList,Cart.class);
		if (name.equals("anonymousUser")) {//如果未登录
			
			return cartList_cookie;
		}else {//如果登录
			System.out.println("从reids中提取购物车");
			List<Cart> cartList_redis = cartService.findCartListFromRedis(name);
			if (cartList_cookie.size()>0) {
				List<Cart> cartList_hebing = cartService.mergeCartList(cartList_cookie, cartList_redis);
				cartService.saveCartListToRedis(name, cartList_hebing);
				util.CookieUtil.deleteCookie(request, response, "cartList");
				System.out.println("执行了合并购物车的逻辑");
				return cartList_hebing;
			}
			return cartList_redis;
		}
		
	}
	//springMVC的版本在4.2或以上版本，可以使用注解实现跨域
	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins="http://localhost:9105")
	public Result addGoodsToCartList(Long itemId,Integer num) {
		
		//response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
		//response.setHeader("Access-Control-Allow-Credentials", "true");
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(name);
		
		try {
			List<Cart> cartList = findCartList();
			cartList = cartService.addGoodsToCart(cartList, itemId, num);
			if (name.equals("anonymousUser")) {//如果未登录
				System.out.println("向cookie中存储购物车");
				util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600*24, "UTF-8");
				
			}else {//如果登录了
				cartService.saveCartListToRedis(name, cartList);
			}
			
			return new Result(true, "添加成功到cookie");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
		
	}
}

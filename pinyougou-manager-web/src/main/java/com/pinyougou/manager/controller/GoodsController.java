package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
//import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;

import entity.PageResult;
import result.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	private Destination topicPageDeleteDestination;
	@Reference
	private GoodsService goodsService;
	@Autowired
	private Destination topicPageDestination;//用来生成商品详情页的消息目标
	//@Reference(timeout=100000)
	//private ItemSearchService itemSearchService;
	
	//@Reference
	//private ItemPageService itemPageService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		Goods goods2 = goodsService.findOne(goods.getGoods().getId());
		if (!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)) {
			return new Result(false, "非法操作");
			
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	@Autowired
	private Destination queueSolrDeleteDestination;
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			
			jmstemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					
					return session.createObjectMessage(ids);
				}
			});
			//itemSearchService.deleteByGoodsId(Arrays.asList(ids));
			
			jmstemplate.send(topicPageDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					
					return session.createObjectMessage(ids);
				}
			});
			
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	@Autowired
	private JmsTemplate jmstemplate;
	@Autowired
	private Destination queueSolrDestination;//用来导入solr索引库的消息目标
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		
		return goodsService.findPage(goods, page, rows);		
	}
	
	@RequestMapping("/updateStatus")
	public Result updateStatus(final Long[] ids, String status) {
		
		try {
			goodsService.updateStatus(ids, status);
			if("1".equals(status)){//如果是审核通过 
				//得到需要导入的SKU列表
				List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
				//导入到solr
			//	itemSearchService.importList(itemList);		
				final String jsonString = com.alibaba.fastjson.JSON.toJSONString(itemList);
				jmstemplate.send(queueSolrDestination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						// TODO Auto-generated method stub
						return session.createTextMessage(jsonString);
					}
				});
				for(final Long goodsId :ids) {
					//itemPageService.genItemHtml(goodsId);
					jmstemplate.send(topicPageDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(goodsId+"");
						}
					});	
				}
				
				
			}
			return new Result(true, "成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
	
	
	
}

package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;


//商品详情页
@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	
	@Override
	public boolean genItemHtml(Long goodsId) {
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		try {
			Template template = configuration.getTemplate("item.html");
			Map dataModel = new HashMap<>();
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			
			dataModel.put("itemCat1", itemCat1);
			dataModel.put("itemCat2", itemCat2);
			dataModel.put("itemCat3", itemCat3);
			
			//sku列表
			
			TbItemExample example= new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);//spu  Id
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");//按是否默认字段进行降序排序，目的是返回结果第一个为默认sku
			List<TbItem> itemList = itemMapper.selectByExample(example);
			dataModel.put("itemList", itemList);
			
			Writer out = new FileWriter(pagedir+goodsId+".html");
			template.process(dataModel, out);
			out.close();
			return true;
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		
		
	}


	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		try {
			for(Long goodsId :goodsIds) {
				new File(pagedir+goodsId+".html").delete();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}

package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private SolrTemplate solrTemplate;
	public void importData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example );
		System.out.println("商品列表~~~~~~~~~");
		for(TbItem item:list) {
			System.out.println(item.getBrand()+" "+item.getTitle()+" "+item.getPrice());
			String spec = item.getSpec();
			Map specMap = JSON.parseObject(spec,Map.class);//从数据库中提取的json字符串转为map
			item.setSpecMap(specMap);
		}
		System.out.println("It's over~~~~~~~~");
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
	}
	
	public void del() {
		
//		Query query = new SimpleQuery("*:*");
//		Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
//		query.addCriteria(criteria);
//		solrTemplate.delete(query);
//		solrTemplate.commit();
	}
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
//		solrUtil.importData();
	
	}
	
}

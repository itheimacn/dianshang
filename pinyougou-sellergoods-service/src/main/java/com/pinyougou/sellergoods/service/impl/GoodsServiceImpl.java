package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.border.TitledBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goosDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());	
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goosDescMapper.insert(goods.getGoodsDesc());
		
		seveItemList(goods);
	}
	
	
	
	public void seveItemList(Goods goods) {
		List<TbItem> itemList = goods.getItemList();
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			for(TbItem item :itemList) {
				String title=goods.getGoods().getGoodsName();//spu商品名称
				  Map<String,Object> map=JSON.parseObject(item.getSpec());
				  for(String key:map.keySet()) {
					  title +=map.get(key);
				  }
				  item.setTitle(title);
				  item.setCategoryid(goods.getGoods().getCategory3Id());
				  item.setGoodsId(goods.getGoods().getId());
				  item.setSellerId(goods.getGoods().getSellerId());
				  item.setCreateTime(new Date());
				  item.setUpdateTime(new Date());
				  
				  TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				  item.setCategory(itemCat.getName());
				  TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
				  item.setBrand(brand.getName());
				  TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
				  item.setSeller(seller.getNickName());
				  
				 List<Map> images = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class); 
				 if (images.size()>0) {
					 item.setImage((String)(images.get(0).get("url")));					
				}
				  
				  itemMapper.insert(item);
			}
		}else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			item.setPrice(goods.getGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(99999);
			item.setSpec("{}");
			
			 item.setCategoryid(goods.getGoods().getCategory3Id());
			  item.setGoodsId(goods.getGoods().getId());
			  item.setSellerId(goods.getGoods().getSellerId());
			  item.setCreateTime(new Date());
			  item.setUpdateTime(new Date());
			  
			  TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			  item.setCategory(itemCat.getName());
			  TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			  item.setBrand(brand.getName());
			  TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			  item.setSeller(seller.getNickName());
			  
			 List<Map> images = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class); 
			 if (images.size()>0) {
				 item.setImage((String)(images.get(0).get("url")));					
			}
			 itemMapper.insert(item);  
		}
		
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新主表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更改副表
		goosDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//删除原有的sku数据列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example );
		//插入新的sku数据列表
		seveItemList(goods);
		
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbgoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbgoods);
		TbGoodsDesc goodsDesc = goosDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		
		
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);//查询条件：商品ID
		List<TbItem> itemList = itemMapper.selectByExample(example);		
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);

		}		
		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//只有当isDelete为null时才查询，其他都不显示，为逻辑删除
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
	
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void updateStatus(Long[] ids, String status) {
			for(long id :ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
			
		}
		/**
		 * 根据spu的Id集合查询sku列表
		 * @param goodsId
		 * @param status
		 * @return
		 */
		
		public List<TbItem>	findItemListByGoodsIdListAndStatus(Long []goodsIds,String status){
			
			System.out.println(goodsIds);
			System.out.println(status);
			TbItemExample example=new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo(status);//状态
			criteria.andGoodsIdIn( Arrays.asList(goodsIds));//指定条件：SPUID集合
			System.out.println(itemMapper.selectByExample(example)+"~~~~~~~~~~~~~~~~~~~~~~~~~");
			return itemMapper.selectByExample(example);
		}
	}

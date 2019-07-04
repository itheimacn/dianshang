package com.pinyougou.sellergoods.service;
/**
 * 品牌接口
 * @author admin
 *
 */

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {

	List<TbBrand> findAll();
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);
	
	/**
	 * 添加品牌
	 * @param brand
	 */
	void add(TbBrand brand);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	TbBrand findOne(long id);
	/**
	 * 更改
	 * @param brand
	 */
	void update(TbBrand brand);
	/**
	 * 删除品牌
	 * @param id
	 */
	void deleteOne(long[] ids);
	
	/**\
	 * 根据条件分页查询
	 * @param brand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageResult findPage(TbBrand brand,int pageNum,int pageSize);
	/**
	 * 查找品牌下拉列表
	 * @return
	 */
	List<Map> selectOptionList();
}

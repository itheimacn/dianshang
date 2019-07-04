package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
@Service
@Transactional
public class BrandServiceImpl implements BrandService {


	@Autowired
	private TbBrandMapper brandMapper;
	
	/**、
	 * 品牌下拉列表
	 */
	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}
	
	
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		return brandMapper.selectByExample(null);
	}
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
		
	}
	
	@Override
	public TbBrand findOne(long id) {
		
		return brandMapper.selectByPrimaryKey(id);
	}
	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
		
	}
	
	
/**
 * 删除一个
 */
	@Override
	public void deleteOne(long[] ids) {
		for(long id:ids) {
			
			brandMapper.deleteByPrimaryKey(id);
			
		}
	}

	//条件查询
	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria createCriteria = example.createCriteria();
		if (brand!=null) {
			if (brand.getName()!=null && brand.getName().length()>0) {
				createCriteria.andNameLike("%"+brand.getName()+"%");				
			}
			if (brand.getFirstChar()!=null &&brand.getFirstChar().length()>0) {
				createCriteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
		}
		Page<TbBrand> brandExample = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(brandExample.getTotal(), brandExample.getResult());
	}

}

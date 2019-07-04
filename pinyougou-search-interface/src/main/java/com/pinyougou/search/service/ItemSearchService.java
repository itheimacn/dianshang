package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	/**
	 * 搜索方法
	 * @param searchMap
	 * @return
	 */
	Map search(Map searchMap);
	
	void importList(List list);
	
	void deleteByGoodsId(List goodsIds);
}

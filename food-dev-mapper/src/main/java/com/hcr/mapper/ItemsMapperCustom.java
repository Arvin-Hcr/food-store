package com.hcr.mapper;

import com.hcr.vo.ItemCommentVO;
import com.hcr.vo.SearchItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {

    public List<ItemCommentVO> queryItemComments(@Param("paramsMap")Map<String,Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);
}

package com.hcr.service.impl;

import com.hcr.mapper.CategoryMapper;
import com.hcr.mapper.CategoryMapperCustom;
import com.hcr.pojo.Category;
import com.hcr.service.CategoryService;

import com.hcr.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final int ONE = 1;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {
        Example example = new Example(Category.class);
        Example.Criteria  criteria = example.createCriteria();
        criteria.andEqualTo("type",ONE);
        List<Category> result = categoryMapper.selectByExample(example);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }
}

package com.hcr.service.impl;

import com.hcr.mapper.CarouselMapper;
import com.hcr.pojo.Carousel;
import com.hcr.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;
    @Override
    public List<Carousel> queryAll(Integer isShow) {

        Example example = new Example(Carousel.class);
        //倒序排列
        example.orderBy("sort").desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isShow",isShow);

        List<Carousel> result = carouselMapper.selectByExample(example);
        return result;
    }
}

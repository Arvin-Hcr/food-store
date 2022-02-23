package com.hcr.service;

import com.hcr.pojo.Carousel;

import java.util.List;

public interface CarouselService {

    /**
     * 查询所有轮播图列表
     */
    public List<Carousel> queryAll(Integer isShow);
}


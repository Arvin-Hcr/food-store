package com.hcr.controller;

import com.hcr.menus.YseOrNo;
import com.hcr.pojo.Carousel;
import com.hcr.pojo.Category;
import com.hcr.service.CarouselService;
import com.hcr.service.CategoryService;
import com.hcr.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel(){
        List<Carousel> list = carouselService.queryAll(YseOrNo.YSE.type);
        return JSONResult.ok(list);
    }

    /**
     * 首页分类展示需求
     * 1.第一次刷新主页要查询大分类，渲染展示到首页
     * 2.如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类（一级分类）",notes = "获取商品分类（一级分类）",httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats(){
        List<Category> list = categoryService.queryAllRootLevelCat();
        return JSONResult.ok(list);
    }
}

package com.hcr.controller;

import com.hcr.menus.YseOrNo;
import com.hcr.pojo.Carousel;
import com.hcr.pojo.Category;
import com.hcr.service.CarouselService;
import com.hcr.service.CategoryService;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.RedisOperator;
import com.hcr.vo.CategoryVO;
import com.hcr.vo.NewItemsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.repository.query.RedisOperationChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel(){
        List<Carousel> list = new ArrayList<>();
        String carousel = redisOperator.get("carousel");
        if (StringUtils.isBlank(carousel)){
            list = carouselService.queryAll(YseOrNo.YSE.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(list));
        }else {
            list = JsonUtils.jsonToList(carousel,Carousel.class);
        }
        return JSONResult.ok(list);
    }

    /**
     * 1.后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
     * 2.定时重置，比如每天凌晨三点重置，若批量重置最后设置在不同时间
     * 3.每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了再重置
     */




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

    @ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(@ApiParam(name = "rootCatId",value = "一级分类id",required = true)
                                 //路径变量
                             @PathVariable Integer rootCatId){
        if (rootCatId == null){
            return JSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> list = new ArrayList<>();
        String catStr = redisOperator.get("subCat:" + rootCatId);
        if (StringUtils.isBlank(catStr)){
            list =  categoryService.getSubCatList(rootCatId);
            if(list != null && list.size() >0){
                redisOperator.set("subCat:" + rootCatId,JsonUtils.objectToJson(list));
            }else {
                redisOperator.set("subCat:" + rootCatId,JsonUtils.objectToJson(list),5*60);
            }
        }else {
            list = JsonUtils.jsonToList(catStr,CategoryVO.class);
        }

        return JSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(@ApiParam(name = "rootCatId", value = "一级分类id", required = true)
                                  @PathVariable Integer rootCatId){
        if (rootCatId == null){
            return JSONResult.errorMsg("分类不存");
        }
        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return JSONResult.ok(list);
    }
}

package com.hcr.controller;

import com.hcr.pojo.Items;
import com.hcr.pojo.ItemsImg;
import com.hcr.pojo.ItemsParam;
import com.hcr.pojo.ItemsSpec;
import com.hcr.service.ItemService;
import com.hcr.utils.JSONResult;
import com.hcr.utils.PagedGridResult;
import com.hcr.vo.CommentLevelCountsVO;
import com.hcr.vo.ItemInfoVO;
import com.hcr.vo.ShopCartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口",tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController{

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "商品id", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId){
        if (StringUtils.isBlank(itemId)){
            return JSONResult.errorException(null);
        }

        Items items = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(items);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return JSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId){
        if (StringUtils.isBlank(itemId)){
            return JSONResult.errorMsg(null);
        }
        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);
        return JSONResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论", httpMethod = "GET")
    @GetMapping("/comments")
    public JSONResult comments(@ApiParam(name = "itemId", value = "商品id",required = true)
                               @RequestParam String itemId,
                               @ApiParam(name = "level", value = "评价等级",required = false)
                               @RequestParam Integer level,
                               @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
                               @RequestParam Integer page,
                               @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
                               @RequestParam Integer pageSize){
        if (StringUtils.isBlank(itemId)){
            return JSONResult.errorMsg(null);
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.queryPagedComments(itemId,level,page,pageSize);
        return JSONResult.ok(gridResult);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public JSONResult search(@ApiParam(name = "keywords", value = "关键字",required = true )
                               @RequestParam String keywords,
                               @ApiParam(name = "sort", value = "排序",required = false)
                               @RequestParam String sort,
                               @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
                               @RequestParam Integer page,
                               @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
                               @RequestParam Integer pageSize){
        if (StringUtils.isBlank(keywords)){
            return JSONResult.errorMsg(null);
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.searchItems(keywords,sort,page,pageSize);
        return JSONResult.ok(gridResult);
    }

    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public JSONResult catItems(@ApiParam(name = "catId", value = "三级分类id",required = true )
                             @RequestParam Integer catId,
                             @ApiParam(name = "sort", value = "排序",required = false)
                             @RequestParam String sort,
                             @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
                             @RequestParam Integer page,
                             @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
                             @RequestParam Integer pageSize){
        if (catId == null){
            return JSONResult.errorMsg(null);
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.searchItems(catId,sort,page,pageSize);
        return JSONResult.ok(gridResult);
    }

    // 用于用户长时间未登录网站，刷新购物车中的数据（主要是商品价格），类似京东淘宝
    @ApiOperation(value = "根据商品规格ids查找最新的商品数据", notes = "根据商品规格ids查找最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public JSONResult refresh(  @ApiParam(name = "itemSpecIds", value = "拼接的规格ids", required = true, example = "1001,1003,1005")
                                @RequestParam String itemSpecIds) {

        if (StringUtils.isBlank(itemSpecIds)) {
            return JSONResult.ok();
        }

        List<ShopCartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);

        return JSONResult.ok(list);
    }
}

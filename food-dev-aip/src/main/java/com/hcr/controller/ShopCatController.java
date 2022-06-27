package com.hcr.controller;

import com.hcr.bo.ShopcartBO;
import com.hcr.service.impl.center.BaseService;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.RedisOperator;
import com.hcr.vo.ShopCartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import sun.util.calendar.BaseCalendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopCatController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ShopCatController.class);

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(@RequestParam String userId,
                          @RequestBody ShopcartBO shopcartBO,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        //判断用户是否登录，若没有登录则存储cookie中(前端会提前做好判断)
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        logger.info("属性为：{}", shopcartBO);

        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //需要判断当前购物车中包含已经存在的商品，如果存在则会累加购买数量
        //使用 ： 可在作为类似文件夹目录在redis客户端更直观得展示出每一级，不使用的话会在一个文件夹中全部展示出来
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartBOList = null;
        if (StringUtils.isNoneBlank(shopcartJson)) {
            //redis中已经有购物车了
            shopcartBOList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            //判断购物车汇是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopcartBO sc : shopcartBOList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopcartBOList.add(shopcartBO);
            }
        } else {
            //redis中没有购物车
            shopcartBOList = new ArrayList<>();
            //直接添加购物车中
            shopcartBOList.add(shopcartBO);
        }
        //覆盖现有redis中的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBOList));

        return JSONResult.ok();

    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public JSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNoneBlank(shopcartJson)){
            //redis已经有购物车
            List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartJson,ShopcartBO.class);
            //判断购物车中是否存在已有要删除的商品，如果有的话则删除
            for (ShopcartBO sc : shopcartBOList){
                String tempSpecId = sc.getSpecId();
                if (tempSpecId.equals(itemSpecId)){
                    shopcartBOList.remove(sc);
                    break;
                }
            }
            //覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartBOList));
        }

        return JSONResult.ok();
    }
}

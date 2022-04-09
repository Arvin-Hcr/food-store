package com.hcr.center;

import com.hcr.controller.BaseController;
import com.hcr.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关接口"})
public class MyOrdersController extends BaseController {

    @ApiOperation(value = "获取订单状态数据概况", notes = "获取订单状态数概况", httpMethod = "POST")
    @PostMapping("/statusCounts")
    public JSONResult statusCounts(){
        return null;

    }
}

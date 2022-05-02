package com.hcr.center;

import com.hcr.controller.BaseController;
import com.hcr.menus.YseOrNo;
import com.hcr.pojo.OrderItems;
import com.hcr.pojo.Orders;
import com.hcr.service.center.MyCommentsService;
import com.hcr.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public JSONResult pending(@ApiParam(name = "userId", value = "用户id", required = true)
                              @RequestParam String userId,
                              @ApiParam(name = "orderId", value = "订单id", required = true)
                              @RequestParam String orderId){

        //判断用户和订单是否关联
        JSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()){
            return checkResult;
        }
        //判断该笔订单是否已经评价过，评价过就不再继续
        Orders orders = (Orders) checkResult.getData();
        if (orders.getIsComment() == YseOrNo.YSE.type){
            return JSONResult.errorMsg("该笔订单已经评价");
        }
        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);
        return JSONResult.ok(list);
    }
}

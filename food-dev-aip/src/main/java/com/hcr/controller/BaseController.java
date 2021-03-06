package com.hcr.controller;

import com.hcr.pojo.Orders;
import com.hcr.pojo.Users;
import com.hcr.service.center.MyOrdersService;
import com.hcr.utils.JSONResult;
import com.hcr.utils.RedisOperator;
import com.hcr.vo.UsersVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {

    @Autowired
    private RedisOperator redisOperator;

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    public static final String REDIS_USER_TOKEN = "REDIS_USER_TOKEN";

    // 支付中心的调用地址  调用支付端nginx，若待端口号则是对外开放不走nginx
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                       |-> 回调通知的url
    //内网穿透，即可访问本地服务器 【http://uyyqw9.natappfree.cc 免费注册的每次使用需要重新Forwarding】
    String payReturnUrl = "http://9tjj6q.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
            File.separator + "images" +
            File.separator + "foodie" +
            File.separator + "faces";
//    public static final String IMAGE_USER_FACE_LOCATION = "/workspaces/images/foodie/faces";


    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public JSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return JSONResult.errorMsg("订单不存在！");
        }
        return JSONResult.ok(order);
    }

    public UsersVO conventUsersVO(Users users){

        String uniqueToken = UUID.randomUUID().toString().trim();  //可使用base64进行加密来作为token
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),uniqueToken); //不会过期，永久有效

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}

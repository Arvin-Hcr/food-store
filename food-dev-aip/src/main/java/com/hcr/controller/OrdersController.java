package com.hcr.controller;

import com.hcr.bo.ShopcartBO;
import com.hcr.bo.SubmitOrderBO;
import com.hcr.menus.OrderStatusEnum;
import com.hcr.menus.PayMethod;
import com.hcr.pojo.OrderStatus;
import com.hcr.service.OrderService;
import com.hcr.utils.CookieUtils;
import com.hcr.utils.JSONResult;
import com.hcr.utils.JsonUtils;
import com.hcr.utils.RedisOperator;
import com.hcr.vo.MerchantOrdersVO;
import com.hcr.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RestController
@RequestMapping("/orders")
public class OrdersController extends BaseController{

    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public JSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                             HttpServletRequest request,
                             HttpServletResponse response){

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type
                && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type){
            return JSONResult.errorMsg("支付方式不支持!");
        }
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)){
            return JSONResult.errorMsg("购物车数据不正确");
        }
        List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartJson,ShopcartBO.class);

        //1. 创建订单
        OrderVO orderVO = orderService.createOrder(shopcartBOList,submitOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        /**
         * 1001
         * 2002 -> 用户购买
         * 3003 -> 用户购买
         * 4004
         */
        //清理覆盖现有的redis汇总的购物车数据
        shopcartBOList.removeAll(orderVO.toBeRemovedShopcartdList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(),JsonUtils.objectToJson(shopcartBOList));
        // 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartBOList), true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        // 请求头设置,格式 application/json
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //提交参数
        httpHeaders.add("imoocUserId","imooc");
        httpHeaders.add("password","imooc");
        // 组装请求体
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO,httpHeaders);
        //发送post请求，以JSONResult类型接收响应结果
        ResponseEntity<JSONResult> resultResponseEntity =
                                    restTemplate.postForEntity(paymentUrl,entity,JSONResult.class);

        JSONResult paymentResult = resultResponseEntity.getBody();
        if (paymentResult.getStatus() != 200){
            logger.error("发送错误：{}",paymentResult.getMsg());
            return JSONResult.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        return JSONResult.ok(orderId);
    }

    /**
     * 回调支付中心更新状态
     * @param merchantOrderId
     * @return
     */
    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public JSONResult getPaidOrderInfo(String orderId) {

        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return JSONResult.ok(orderStatus);
    }
}

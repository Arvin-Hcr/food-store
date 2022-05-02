package com.hcr.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.hcr.bo.center.OrderItemsCommentBO;
import com.hcr.mapper.ItemsCommentsMapperCustom;
import com.hcr.mapper.OrderItemsMapper;
import com.hcr.mapper.OrderStatusMapper;
import com.hcr.mapper.OrdersMapper;
import com.hcr.menus.YseOrNo;
import com.hcr.org.n3r.idworker.Sid;
import com.hcr.pojo.OrderItems;
import com.hcr.pojo.OrderStatus;
import com.hcr.pojo.Orders;
import com.hcr.service.center.MyCommentsService;
import com.hcr.utils.PagedGridResult;
import com.hcr.vo.MyCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private  Sid sid;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList) {

        //1.保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList){
            oic.setCommentId(sid.nextShort());
        }
        Map<String ,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("commentList",commentList);
        itemsCommentsMapperCustom.saveComments(map);

        //2.修改订单表已评价 orders
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YseOrNo.YSE.type);
        ordersMapper.updateByPrimaryKeySelective(orders);

        //3.修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        Map<String ,Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
        return setterPagedGrid(list,page);
    }


}

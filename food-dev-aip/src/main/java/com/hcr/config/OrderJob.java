package com.hcr.config;

import com.hcr.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    //根据实际业务需求更改时间
    @Scheduled(cron = "0/3 * * * * ?")
    public void autoCloseOrder(){
        orderService.closeOrder();
    }
}

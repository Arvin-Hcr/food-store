package com.hcr.vo;

import com.hcr.bo.ShopcartBO;

import java.util.List;

public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopcartdList;

    public List<ShopcartBO> toBeRemovedShopcartdList() {
        return toBeRemovedShopcartdList;
    }

    public void toBeRemovedShopcartdList(List<ShopcartBO> toBeRemovedShopcartdList) {
        this.toBeRemovedShopcartdList = toBeRemovedShopcartdList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}
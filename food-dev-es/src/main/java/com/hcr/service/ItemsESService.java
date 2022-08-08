package com.hcr.service;


import com.hcr.utils.PagedGridResult;

public interface ItemsESService {

    public PagedGridResult searhItems(String keywords,
                                      String sort,
                                      Integer page,
                                      Integer pageSize);

}

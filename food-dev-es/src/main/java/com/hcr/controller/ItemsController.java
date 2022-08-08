package com.hcr.controller;

import com.hcr.service.ItemsESService;

import com.hcr.utils.JSONResult;
import com.hcr.utils.PagedGridResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/es/search")
    public JSONResult search(
                            String keywords,
                            String sort,
                            Integer page,
                            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return JSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        PagedGridResult grid = itemsESService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return JSONResult.ok(grid);
    }

}

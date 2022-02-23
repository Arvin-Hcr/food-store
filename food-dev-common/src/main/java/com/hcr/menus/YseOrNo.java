package com.hcr.menus;

import io.swagger.models.auth.In;

/**
 * @Desc: 是否 枚举
 */
public enum YseOrNo {

    NO(0,"否"),
    YSE(1,"是");

    public final Integer type;
    public final String value;

    YseOrNo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}

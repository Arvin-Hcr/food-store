package com.hcr.service.center;

import com.hcr.pojo.Users;

public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);
}

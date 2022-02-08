package com.hcr.service;

import com.hcr.bo.UserBO;
import com.hcr.pojo.Users;

public interface UserService {

    /**
     * 判断用户名是否存在
     * @param userName
     * @return
     */
    public boolean queryUsernameIsExist(String userName);

    /**
     * 创建用户
     * @param userBO
     * @return
     */
    public Users createUser(UserBO userBO);
}

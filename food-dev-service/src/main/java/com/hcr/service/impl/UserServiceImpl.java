package com.hcr.service.impl;

import com.hcr.bo.UserBO;
import com.hcr.mapper.UsersMapper;
import com.hcr.menus.Sex;
import com.hcr.pojo.Users;
import com.hcr.service.UserService;
import com.hcr.utils.DateUtils;
import com.hcr.utils.MD5Utils;
import com.hcr.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

//快捷鍵ctrl+i kuai jie bu chong jian
@Service
public class UserServiceImpl implements UserService {

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String userName) {
        Example userExample = new Example(Users.class);
        Example.Criteria userExampleCriteria = userExample.createCriteria();
        userExampleCriteria.andEqualTo("username",userName);
        Users usersResult = usersMapper.selectOneByExample(userExample);
        return usersResult == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)  //创建用，用require 为必须有一个事务，出错可回滚
    @Override
    public Users createUser(UserBO userBO) {
        Users users = new Users();
        //随机ID，后续修改为全局唯一索引ID
        users.setId(RandomUtil.getRandomDateNum());
        users.setUsername(userBO.getUsername());
        try {
            //对密码进行md5加密
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //默认用户昵称同用户名
        users.setNickname(userBO.getUsername());
        //默认头像
        users.setFace(USER_FACE);
        //默认生日
        users.setBirthday(DateUtils.formatStringToDate("1900-01-01"));
        //默认性别保密
        users.setSex(Sex.secret.type);   //公共化

        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());
        usersMapper.insert(users);

        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        //检索信息
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username",username);
        userCriteria.andEqualTo("password",password);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }
}

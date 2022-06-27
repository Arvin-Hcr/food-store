package com.hcr.service.impl;

import com.hcr.bo.AddressBO;
import com.hcr.mapper.UserAddressMapper;
import com.hcr.menus.YseOrNo;
import com.hcr.org.n3r.idworker.Sid;
import com.hcr.pojo.UserAddress;
import com.hcr.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper addressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return addressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        //1.判断当前用户是否存在地址，如果没有，则新增为‘默认地址’
        Integer isDefault = 0;
        //调用queryAll方法查询所有地址
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (addressList == null || addressList.isEmpty() || addressList.size() == 0){
            isDefault = 1;
        }
        String addressId = sid.nextShort();

        //2.保存地址到数据库
        UserAddress newAddress = new UserAddress();
        //深拷贝
        BeanUtils.copyProperties(addressBO,newAddress);

        newAddress.setId(addressId);
        newAddress.setIsDefault(isDefault);
        newAddress.setCreatedTime(new Date());
        newAddress.setUpdatedTime(new Date());

        addressMapper.insert(newAddress);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();
        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,pendingAddress);

        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(new Date());

        addressMapper.updateByPrimaryKeySelective(pendingAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        //用实体SQL会进行两个条件的拼接操作
        addressMapper.delete(address);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        //1.查找默认地址，设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YseOrNo.YSE.type);
        List<UserAddress> list = addressMapper.select(queryAddress);
        //防止用户默认地址紊乱，将查询出来的地址全部设置为不默认
        for (UserAddress ua : list){
            ua.setIsDefault(YseOrNo.NO.type);
            addressMapper.updateByPrimaryKeySelective(ua);
        }

        //2.根据地址id修改为默认地址
        UserAddress defatultAddress = new UserAddress();
        defatultAddress.setId(addressId);
        defatultAddress.setUserId(userId);
        defatultAddress.setIsDefault(YseOrNo.YSE.type);

        addressMapper.updateByPrimaryKeySelective(defatultAddress);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {

        UserAddress singleAddress = new UserAddress();
        singleAddress.setId(addressId);
        singleAddress.setUserId(userId);

        return addressMapper.selectOne(singleAddress);
    }
}

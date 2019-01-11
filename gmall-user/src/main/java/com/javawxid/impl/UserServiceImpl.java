package com.javawxid.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.javawxid.bean.UserAddress;
import com.javawxid.bean.UserInfo;
import com.javawxid.mapper.UserAddressMapper;
import com.javawxid.mapper.UserInfoMapper;
import com.javawxid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAddressList() {
        return userAddressMapper.selectAll();
    }



    @Override
    public List<UserAddress> getAddressListByUserId(String userId) {

        UserAddress userAddress =  new UserAddress();
        userAddress.setUserId(userId);
        List<UserAddress> userAddresses = userAddressMapper.select(userAddress);

        return userAddresses;
    }

    @Override
    public UserAddress getAddressListById(String addressId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        UserAddress userAddress1 = userAddressMapper.selectOne(userAddress);

        return userAddress1;
    }

    @Override
    public List<UserInfo> getUserList() {

        //List<UserInfo> userInfos = userInfoMapper.selectUserList();

        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }
}

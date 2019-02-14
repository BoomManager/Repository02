package com.javawxid.service;

import com.javawxid.bean.UserAddress;
import com.javawxid.bean.UserInfo;

import java.util.List;

public interface UserService {


    List<UserAddress> getAddressList();

    List<UserAddress> getAddressListByUserId(String userId);

    UserAddress getAddressListById(String addressId);

    List<UserInfo> getUserList();

    UserInfo login(UserInfo userInfo);

    void addUserCache(UserInfo userLogin);

    UserAddress getAddressById(String addressId);

    UserInfo getUserCache(String id);

    UserInfo getUserById(String userId);
}

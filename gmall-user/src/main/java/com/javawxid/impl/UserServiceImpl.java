package com.javawxid.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.javawxid.bean.SkuInfo;
import com.javawxid.bean.UserAddress;
import com.javawxid.bean.UserInfo;
import com.javawxid.mapper.UserAddressMapper;
import com.javawxid.mapper.UserInfoMapper;
import com.javawxid.service.UserService;
import com.javawxid.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;

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

        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        // 先查缓存
        UserInfo userParam = new UserInfo();
        userParam.setLoginName(userInfo.getLoginName());
        userParam.setPasswd(userInfo.getPasswd());
        UserInfo userLogin = userInfoMapper.selectOne(userParam);
        return userLogin;
    }

    @Override
    public void addUserCache(UserInfo userLogin) {
        Jedis jedis = redisUtil.getJedis();
        // 设置用户缓存
        jedis.setex("user:"+userLogin.getId()+":info",60*60*24, JSON.toJSONString(userLogin));
        jedis.close();
    }
    @Override
    public UserInfo getUserCache(String id) {
        UserInfo userInfo = new UserInfo();
        Jedis jedis = redisUtil.getJedis();
        String user = jedis.get("user:" + id + ":info");
        userInfo = JSON.parseObject(user, UserInfo.class);
        return userInfo;
    }

    @Override
    public UserInfo getUserById(String userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        return userInfoMapper.selectByPrimaryKey(userInfo);
    }

    @Override
    public UserAddress getAddressById(String addressId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        UserAddress userAddress1 = userAddressMapper.selectOne(userAddress);

        return userAddress1;
    }


}

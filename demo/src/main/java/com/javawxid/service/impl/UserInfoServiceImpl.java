package com.javawxid.service.impl;

import com.javawxid.bean.UserInfo;
import com.javawxid.mapper.UserInfoMapper;
import com.javawxid.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> getUserInfo() {
        List<UserInfo> userInfoList= userInfoMapper.selectAll();
        return userInfoList;
    }
}

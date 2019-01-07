package com.javawxid.mapper;

import com.javawxid.bean.UserInfo;
import tk.mybatis.mapper.common.Mapper;

public interface UserInfoMapper extends Mapper<UserInfo> {

    Integer deleteById(Integer id);

    Integer updateById(UserInfo userInfo);
}

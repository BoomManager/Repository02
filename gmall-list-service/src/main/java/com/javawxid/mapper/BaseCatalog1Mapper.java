package com.javawxid.mapper;

import com.javawxid.bean.BaseCatalog1;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCatalog1Mapper extends Mapper<BaseCatalog1> {

    List<BaseCatalog1> selectCatalog1();

}

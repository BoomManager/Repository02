package com.javawxid.service;

import com.javawxid.bean.BaseAttrInfo;
import com.javawxid.bean.BaseCatalog1;
import com.javawxid.bean.BaseCatalog2;
import com.javawxid.bean.BaseCatalog3;

import java.util.List;

public interface AttrService {

    List<BaseCatalog1> getCatalog1();
    List<BaseCatalog2> getCatalog2(String catalog1Id);
    List<BaseCatalog3> getCatalog3(String catalog2Id);
    List<BaseAttrInfo> getAttrList(String catalog3Id);
}

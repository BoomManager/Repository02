package com.javawxid.service;

import com.javawxid.bean.SkuInfo;

import java.util.List;

public interface SkuService {
    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuInfo(String skuId);

    SkuInfo item(String skuId, String remoteAddr);

    List<SkuInfo> SkuListByCatalog3Id(String s);


}

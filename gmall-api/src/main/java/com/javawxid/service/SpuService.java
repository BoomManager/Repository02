package com.javawxid.service;


import com.javawxid.bean.BaseSaleAttr;
import com.javawxid.bean.SpuImage;
import com.javawxid.bean.SpuInfo;
import com.javawxid.bean.SpuSaleAttr;

import java.util.List;

public interface SpuService {
    List<SpuInfo> spuList(String catalog3id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(String spuId);

    List<SpuImage> spuImageList(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String spuId, String skuId);
}

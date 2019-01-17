package com.javawxid.mapper;

import com.javawxid.bean.SkuInfo;
import com.javawxid.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    List<SkuInfo> selectSkuSaleAttrValueListBySpu(String spuId);
}

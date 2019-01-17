package com.javawxid.mapper;

import com.javawxid.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr>{
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") String spuId, @Param("skuId") String skuId);
}

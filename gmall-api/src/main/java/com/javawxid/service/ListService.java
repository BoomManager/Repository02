package com.javawxid.service;


import com.javawxid.bean.SkuLsInfo;
import com.javawxid.bean.SkuLsParam;

import java.util.List;

public interface ListService {
    List<SkuLsInfo> list(SkuLsParam skuLsParam);
}

package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.javawxid.bean.SkuImage;
import com.javawxid.bean.SkuInfo;
import com.javawxid.bean.SkuSaleAttrValue;
import com.javawxid.bean.SpuSaleAttr;
import com.javawxid.service.SkuService;
import com.javawxid.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String doItem(@PathVariable("skuId") String skuId, ModelMap map,HttpServletRequest request) {
        SkuInfo skuInfo = skuService.item(skuId,request.getRemoteAddr());
        map.addAttribute("skuInfo", skuInfo);
        String spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(spuId, skuId);
        map.addAttribute("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);

        List<SkuInfo> skuInfos = spuService.getSkuSaleAttrValueListBySpu(spuId);
        HashMap<Object, Object> skuMap = new HashMap<>();
        for (SkuInfo info : skuInfos) {
            String id = info.getId();
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            String k = "";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                String saleAttrValueId = skuSaleAttrValue.getSaleAttrValueId();
                k = k + "|" + saleAttrValueId;
            }
            skuMap.put(k,id);
        }
        map.put("skuMap", JSON.toJSONString(skuMap));

        return "item";
    }





}

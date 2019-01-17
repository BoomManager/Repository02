package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.bean.SkuInfo;
import com.javawxid.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SkuController {

    @Reference
    SkuService skuService;

    @RequestMapping("saveSku")
    @ResponseBody
    public String saveSku(SkuInfo skuInfo){

        skuService.saveSku(skuInfo);

        return "success";
    }
}

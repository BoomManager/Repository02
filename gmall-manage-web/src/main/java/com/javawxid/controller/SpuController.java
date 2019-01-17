package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.bean.BaseSaleAttr;
import com.javawxid.bean.SpuImage;
import com.javawxid.bean.SpuInfo;
import com.javawxid.bean.SpuSaleAttr;
import com.javawxid.service.SpuService;
import com.javawxid.util.ManageUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SpuController {

    @Reference
    SpuService spuService;


    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){

        List<SpuImage> spuImages = spuService.spuImageList(spuId);

        return spuImages;
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){

        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);

        return spuSaleAttrs;
    }



    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){

        //调用上传工具类
        String imgUrl = ManageUploadUtil.imgUpolad(multipartFile);

        return imgUrl;
    }


    @RequestMapping("saveSpu")
    @ResponseBody
    public String saveSpu(SpuInfo spuInfo){

       spuService.saveSpu(spuInfo);

        return "success";
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){

        List<BaseSaleAttr> baseSaleAttrs = spuService.baseSaleAttrList();

        return baseSaleAttrs;
    }

    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){

        List<SpuInfo> spuInfos = spuService.spuList(catalog3Id);

        return spuInfos;
    }
}

package com.javawxid.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.config.annotation.Service;
import com.javawxid.bean.*;
import com.javawxid.mapper.*;
import com.javawxid.service.SkuService;
import com.javawxid.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        String skuId = skuInfo.getId();
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfos = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);

        skuInfos.setSkuImageList(skuImages);

        return skuInfos;
    }

    @Override
    public SkuInfo item(String skuId,String ip) {
        System.out.println(ip+"访问"+skuId+"商品");
        SkuInfo skuInfo = null;
        //从redis获取redis的客户端jedis
        Jedis jedis = redisUtil.getJedis();
        // 从缓存中取出skuId的数据jedis.set
        String skuInfoStr = jedis.get("sku:"+skuId+":info");
        //Json格式转成实体类类型
        skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);
        //从db中取出sku的数据
        //缓存中没有
        if(skuInfo == null){
            System.out.println(ip+"发现缓存中没有"+skuId+"商品数据，申请分布式锁");
            // 拿到分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10000);
            if(StringUtils.isBlank(OK)){
                System.out.println(ip+"分布式锁申请失败，三秒后开始自旋");
                // 缓存锁被占用，等一会儿继续申请
                try {
                    Thread.sleep(3000);//让它等3秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return item(skuId,ip);//自旋，这里没有启动新线程，item(skuId,ip);才会启动新线程
            }else{
                System.out.println(ip+"分布式锁申请成功，访问数据库");
                // 拿到缓存锁，可以访问数据库
                skuInfo = getSkuInfo(skuId);
            }
            System.out.println(ip+"成功访问数据库后，归还锁，将"+skuId+"商品放入缓存");
            jedis.del("sku:"+skuId+":lock");
        }
        //关闭redis客户端
        jedis.close();
        return skuInfo;
    }

    @Override
    public List<SkuInfo> SkuListByCatalog3Id(String catalog3Id) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : skuInfos) {
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(info.getId());
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
            info.setSkuAttrValueList(skuAttrValues);
        }

        return skuInfos;
    }

    @Override
    public SkuInfo getSkuById(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo = skuInfoMapper.selectOne(skuInfo);
        return skuInfo;
    }
}

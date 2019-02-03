package com.javawxid.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.javawxid.bean.CartInfo;
import com.javawxid.mapper.CartInfoMapper;
import com.javawxid.service.CartService;
import com.javawxid.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public CartInfo exists(CartInfo exists) {
        CartInfo cartInfo = cartInfoMapper.selectOne(exists);
        return cartInfo;
    }

    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void updateCart(CartInfo ifCart) {
        Example e = new Example(CartInfo.class);
        e.createCriteria().andEqualTo("userId",ifCart.getUserId()).andEqualTo("skuId",ifCart.getSkuId());
        cartInfoMapper.updateByExampleSelective(ifCart,e);// update cart_info set... where user_id = ? and sku_id = ?
    }

    @Override
    public void flushCartCacheByUser(String userId) {
        // hash
        List<CartInfo> cartInfos =  getCartInfosByUserId(userId);

        Jedis jedis = redisUtil.getJedis();

        if(cartInfos!=null){
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfos) {
                stringStringHashMap.put(cartInfo.getId(), JSON.toJSONString(cartInfo));
            }
            //同时将多个 field-value (域-值)对设置到哈希表 key 中。
            jedis.hmset("cart:"+userId+":info",stringStringHashMap);
        }

        jedis.close();


    }

    @Override
    public List<CartInfo> cartListFromCache(String userId) {

        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        //返回哈希表所有域的值。
        List<String> hvals = jedis.hvals("cart:" + userId + ":info");
        for (String hval : hvals) {
            CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
            cartInfos.add(cartInfo);
        }
        return cartInfos;
    }

    private List<CartInfo> getCartInfosByUserId(String userId) {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartInfoMapper.select(cartInfo);

        return cartInfos;
    }
}

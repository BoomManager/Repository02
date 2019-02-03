package com.javawxid.service;

import com.javawxid.bean.CartInfo;

import java.util.List;

public interface CartService {

    CartInfo exists(CartInfo exists);

    void saveCart(CartInfo cartInfo);

    void updateCart(CartInfo ifCart);

    void flushCartCacheByUser(String userId);

    List<CartInfo> cartListFromCache(String userId);
}

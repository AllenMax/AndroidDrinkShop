package com.example.allen.androiddrinkshop.Database.DataSource;

import java.util.List;

import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import io.reactivex.Flowable;

public interface ICartDataSource {

    Flowable<List<Cart>> getCartItems();
    Flowable<List<Cart>> getCartItemById(int cartItemId);
    int countCartItems();
    float sumPrice();
    void emptyCart();
    void insertToCart(Cart...carts);
    void updateCart(Cart...carts);
    void deleteCartItem(Cart cart);
}

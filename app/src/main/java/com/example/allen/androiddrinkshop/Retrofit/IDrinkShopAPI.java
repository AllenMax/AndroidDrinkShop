package com.example.allen.androiddrinkshop.Retrofit;

import com.example.allen.androiddrinkshop.Model.Banner;
import com.example.allen.androiddrinkshop.Model.Category;
import com.example.allen.androiddrinkshop.Model.CheckUserResponse;
import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.Model.Order;
import com.example.allen.androiddrinkshop.Model.Store;
import com.example.allen.androiddrinkshop.Model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IDrinkShopAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkExistsUser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone,
                               @Field("name") String name,
                               @Field("birthdate") String birthdate,
                               @Field("address") String address);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    Observable<List<Banner>> getBanners();

    @GET("getmenu.php")
    Observable<List<Category>> getMenu();

    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuID);

    @GET("getalldrinks.php")
    Observable<List<Drink>> getAllDrinks();

    @FormUrlEncoded
    @POST("submitorder.php")
    Call<String> insertNewOrder(@Field("price") float orderPrice,
                                @Field("orderDetail") String orderDetail,
                                @Field("comment") String orderComment,
                                @Field("address") String orderAddress,
                                @Field("phone") String phone);

    @FormUrlEncoded
    @POST("getorder.php")
    Observable<List<Order>> getAllOrders(@Field("userPhone") String userPhone,
                                         @Field("status") String status);

    @FormUrlEncoded
    @POST("cancelorder.php")
    Call<String> cancelOrder(@Field("orderId") String orderId,
                             @Field("userPhone") String userPhone);

    @FormUrlEncoded
    @POST("getnearbystore.php")
    Observable<List<Store>> getNearbyStore(@Field("lat") String lat,
                                           @Field("lng") String lng);
}

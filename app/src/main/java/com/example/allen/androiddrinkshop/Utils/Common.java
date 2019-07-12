package com.example.allen.androiddrinkshop.Utils;

import com.example.allen.androiddrinkshop.Database.DataSource.CartRepository;
import com.example.allen.androiddrinkshop.Database.DataSource.FavoriteRepository;
import com.example.allen.androiddrinkshop.Database.Local.ProjectRoomDatabase;
import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.allen.androiddrinkshop.Model.Category;
import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.Model.Order;
import com.example.allen.androiddrinkshop.Model.User;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {
    //In Emulator, localhost = 10.0.2.2
//    private static final String BASE_URL ="http://10.0.2.2/drinkshop/";

    //In Phone, http://dev.cs.smu.ca
    private static final String BASE_URL = "http://dev.cs.smu.ca/~a_mathew/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;
    public static Order currentOrder = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();

    //Hold Field
    public static int sizeOfCup = -1; //-1 : no choice (error), 0:M, 1:L
    public static int sugar = -1; //-1 : no choice (error), 0:M, 1:L
    public static int ice = -1; //-1 : no choice (error), 0:M, 1:L

    //Database
    public static ProjectRoomDatabase projectRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;


    public static IDrinkShopAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static String convertCodeToStatus(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Cancelled";

            default:
                return "orderStatus Error";
        }
    }
}

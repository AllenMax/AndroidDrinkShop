package com.example.allen.androiddrinkshop.Database.Local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;

@Database(entities = {Cart.class, Favorite.class}, version = 1)
public abstract class ProjectRoomDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();
    private static ProjectRoomDatabase instance;

    public static ProjectRoomDatabase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context,ProjectRoomDatabase.class,"EDMT_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}

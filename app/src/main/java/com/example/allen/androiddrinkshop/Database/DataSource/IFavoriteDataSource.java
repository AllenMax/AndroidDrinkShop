package com.example.allen.androiddrinkshop.Database.DataSource;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavoriteDataSource {

    Flowable<List<Favorite>> getFavItems();

    int isFavorite(int itemId);

    void insertToFavorite(Favorite...favorites);

    void delete(Favorite favorite);
}

package com.example.allen.androiddrinkshop.Database.DataSource;

import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public class FavoriteRepository implements IFavoriteDataSource{

    private IFavoriteDataSource iFavoriteDataSource;
    private static FavoriteRepository instance;

    public FavoriteRepository(IFavoriteDataSource iFavoriteDataSource) {
        this.iFavoriteDataSource = iFavoriteDataSource;
    }

    public static FavoriteRepository getInstance(IFavoriteDataSource iFavoriteDataSource)
    {
        if (instance == null){
            instance= new FavoriteRepository(iFavoriteDataSource);
        }
        return instance;
    }

    @Override
    public Flowable<List<Favorite>> getFavItems() {
        return iFavoriteDataSource.getFavItems();
    }

    @Override
    public int isFavorite(int itemId) {
        return iFavoriteDataSource.isFavorite(itemId);
    }

    @Override
    public void insertToFavorite(Favorite... favorites) {
        iFavoriteDataSource.insertToFavorite(favorites);
    }

    @Override
    public void delete(Favorite favorite) {
        iFavoriteDataSource.delete(favorite);
    }
}

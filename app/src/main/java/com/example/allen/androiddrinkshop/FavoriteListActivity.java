package com.example.allen.androiddrinkshop;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.allen.androiddrinkshop.Adapter.FavoriteAdapter;
import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.example.allen.androiddrinkshop.Utils.RecylerItemTouchHelper;
import com.example.allen.androiddrinkshop.Utils.RecylerItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class FavoriteListActivity extends AppCompatActivity implements RecylerItemTouchHelperListener {

    RecyclerView recycler_favorite;

    RelativeLayout rootLayout;

    CompositeDisposable compositeDisposable;

    FavoriteAdapter favoriteAdapter;
    List<Favorite> favoritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        compositeDisposable = new CompositeDisposable();

        rootLayout = findViewById(R.id.rootLayout);

        recycler_favorite = findViewById(R.id.recycler_favorite);
        recycler_favorite.setLayoutManager(new LinearLayoutManager(this));
        recycler_favorite.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecylerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_favorite);

        loadFavoriteItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void loadFavoriteItem() {
        compositeDisposable.add(Common.favoriteRepository.getFavItems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Favorite>>() {
            @Override
            public void accept(List<Favorite> favorites) throws Exception {
                displayFavoriteAdapter(favorites);
            }
        }));
    }

    private void displayFavoriteAdapter(List<Favorite> favorites) {

        if(favorites.size() == 0){
            Toast.makeText(this, "Currently No Items In Your Favorite List", Toast.LENGTH_SHORT).show();
        }
        else{
            favoritesList = favorites;
            favoriteAdapter = new FavoriteAdapter(this,favorites);
            recycler_favorite.setAdapter(favoriteAdapter);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof  FavoriteAdapter.FavoriteViewHolder)
        {
            String name = favoritesList.get(viewHolder.getAdapterPosition()).name;

            final Favorite deletedItem = favoritesList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Deleted item from adapter
            favoriteAdapter.removeItem(deletedIndex);
            //Delete item from Room Database
            Common.favoriteRepository.delete(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append("removed from Favorites List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.favoriteRepository.insertToFavorite(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.BLUE);
            snackbar.show();
        }
    }
}

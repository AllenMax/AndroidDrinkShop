package com.example.allen.androiddrinkshop;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allen.androiddrinkshop.Adapter.DrinkAdapter;
import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {

    TextView txt_banner_name;
    RecyclerView recycler_drinks;

    IDrinkShopAPI mService;

    //Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        mService = Common.getAPI();

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        recycler_drinks = findViewById(R.id.recycler_drinks);
        recycler_drinks.setLayoutManager(new GridLayoutManager(this,2));
        recycler_drinks.setHasFixedSize(true);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                loadListDrink(Common.currentCategory.ID);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                loadListDrink(Common.currentCategory.ID);
            }
        });

        txt_banner_name = findViewById(R.id.txt_menu_name);
        txt_banner_name.setText(Common.currentCategory.Name);
        
        loadListDrink(Common.currentCategory.ID);
    }

    private void loadListDrink(String menuId) {
        compositeDisposable.add(mService.getDrink(menuId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrinksImages(drinks);
                    }
                }));

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void displayDrinksImages(List<Drink> drinks) {
        DrinkAdapter adapter = new DrinkAdapter(this,drinks);
        recycler_drinks.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }

//    //EXIT Application when BACK Button is clicked
//    boolean isBackButtonClicked = false;
//
//    @Override
//    public void onBackPressed() {
//        if(isBackButtonClicked){
//            super.onBackPressed();
//            return;
//        }
//        this.isBackButtonClicked = true;
//        Toast.makeText(this, "Please Click BACK again to EXIT", Toast.LENGTH_SHORT).show();
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        isBackButtonClicked = false;
    }
}

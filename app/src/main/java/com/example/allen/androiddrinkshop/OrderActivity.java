package com.example.allen.androiddrinkshop;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.allen.androiddrinkshop.Adapter.OrderAdapter;
import com.example.allen.androiddrinkshop.Model.Order;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderActivity extends AppCompatActivity {

    RecyclerView recycler_orders;
    BottomNavigationView bottomNavigationView;

    IDrinkShopAPI mService;

    //Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mService = Common.getAPI();

        recycler_orders = findViewById(R.id.recycler_orders);
        recycler_orders.setLayoutManager(new LinearLayoutManager(this));
        recycler_orders.setHasFixedSize(true);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.order_new) {
                    loadOrder("0");
                } else if (menuItem.getItemId() == R.id.order_cancel) {
                    loadOrder("-1");
                } else if (menuItem.getItemId() == R.id.order_processing) {
                    loadOrder("1");
                } else if (menuItem.getItemId() == R.id.order_shipping) {
                    loadOrder("2");
                } else if (menuItem.getItemId() == R.id.order_shipped) {
                    loadOrder("3");
                }
                return true;
            }
        });

        loadOrder("0");

    }

    private void loadOrder(String statusCode) {
        compositeDisposable.add(mService.getAllOrders(Common.currentUser.getPhone(), statusCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Order>>() {
                    @Override
                    public void accept(List<Order> orders) throws Exception {
                        displayOrders(orders);
                    }
                }));
    }

    private void displayOrders(List<Order> orders) {
        OrderAdapter orderAdapter = new OrderAdapter(this, orders);
        recycler_orders.setAdapter(orderAdapter);
    }

    @Override
    protected void onResume() {
        loadOrder("0");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        compositeDisposable.clear();
        super.onStart();
    }
}

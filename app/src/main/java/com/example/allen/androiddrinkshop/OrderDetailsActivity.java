package com.example.allen.androiddrinkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allen.androiddrinkshop.Adapter.OrderDetailsAdapter;
import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    public TextView txt_order_id, txt_order_price,txt_order_address,txt_order_comment,txt_order_status;
    Button btn_cancel;

    RecyclerView recycler_order_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        txt_order_id = findViewById(R.id.txt_order_id);
        txt_order_price = findViewById(R.id.txt_order_price);
        txt_order_address = findViewById(R.id.txt_order_address);
        txt_order_comment = findViewById(R.id.txt_order_comment);
        txt_order_status = findViewById(R.id.txt_order_status);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleOrder();
            }
        });

        recycler_order_details = findViewById(R.id.recycler_order_details);
        recycler_order_details.setLayoutManager(new LinearLayoutManager(this));
        recycler_order_details.setHasFixedSize(true);

        txt_order_id.setText(new StringBuilder("#").append(Common.currentOrder.getOrderId()).toString());
        txt_order_price.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()).toString());
        txt_order_address.setText(Common.currentOrder.getOrderAddress());
        txt_order_comment.setText(Common.currentOrder.getOrderComment());
        txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(Common.currentOrder.getOrderStatus())));

        displayOrderDetails();
        
    }

    private void cancleOrder() {
        IDrinkShopAPI drinkShopAPI = Common.getAPI();
        drinkShopAPI.cancelOrder(String.valueOf(Common.currentOrder.getOrderId()),
                Common.currentUser.getPhone())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(OrderDetailsActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                        if(response.body().contains("Order has been cancelled")){
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("DEBUG_FLAG", t.getMessage());
                    }
                });
    }

    private void displayOrderDetails() {
        List<Cart> orderDetalsList = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>(){}.getType());
        recycler_order_details.setAdapter(new OrderDetailsAdapter(this,orderDetalsList));
    }
}

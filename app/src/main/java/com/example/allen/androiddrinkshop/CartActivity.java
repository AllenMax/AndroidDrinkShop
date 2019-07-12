package com.example.allen.androiddrinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.allen.androiddrinkshop.Adapter.CartAdapter;
import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import com.example.allen.androiddrinkshop.Model.Order;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.example.allen.androiddrinkshop.Utils.RecylerItemTouchHelper;
import com.example.allen.androiddrinkshop.Utils.RecylerItemTouchHelperListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecylerItemTouchHelperListener {

    RecyclerView recycler_cart;
    Button btn_place_order;

    CartAdapter cartAdapter;
    List<Cart> cartList = new ArrayList<>();

    RelativeLayout rootLayout;

    CompositeDisposable compositeDisposable;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();

        rootLayout = findViewById(R.id.rootLayout);

        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecylerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.cartRepository.countCartItems()!=0){
                    placeOrder();
                }
                else{
                    Toast.makeText(CartActivity.this, "Invalid: Cart Cannot be Empty.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        loadCartItem();
    }

    private void placeOrder() {




        if (Common.currentUser != null){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Submit Order");

            View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.dialog_submit_order,null);

            final EditText edt_comment = submit_order_layout.findViewById(R.id.edit_comment);
            final EditText edt_other_address = submit_order_layout.findViewById(R.id.edt_other_address);

            final RadioButton rdi_user_address = submit_order_layout.findViewById(R.id.rdi_user_address);
            final RadioButton rdi_other_address = submit_order_layout.findViewById(R.id.rdi_user_address);

            //Event
            rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        edt_other_address.setEnabled(false);
                    }
                }
            });

            rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        edt_other_address.setEnabled(true);
                    }
                }
            });

            builder.setView(submit_order_layout);

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String orderComment = edt_comment.getText().toString();
                    final String orderAddress;
                    if(rdi_user_address.isChecked()){
                        orderAddress = Common.currentUser.getAddress();
                    }
                    else if (rdi_other_address.isChecked()){
                        orderAddress = edt_other_address.getText().toString();
                    }
                    else {
                        orderAddress = "";
                    }

                    //Submit Order
                    compositeDisposable.add(
                            Common.cartRepository.getCartItems()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {
                                            if(!TextUtils.isEmpty(orderAddress)){
                                                sendOrderToServer(Common.cartRepository.sumPrice(),carts,
                                                        orderComment,orderAddress);
                                            }
                                            else{
                                                Toast.makeText(CartActivity.this, "Order Address Can't be null", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                    );
                }
            });

            builder.show();
        }
        else{
            //Requires Login

//            startActivity(new Intent(CartActivity.this, MainActivity.class));
//            Toast.makeText(this, "Please Login or Register Account", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("LOGIN");
            builder.setMessage("Please Login or register account to submit order");
            builder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();
                }
            }).show();
        }

    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String finalOrderAddress) {
        if (carts.size() > 0){
            String orderDetail = new Gson().toJson(carts);

            mService.insertNewOrder(sumPrice,orderDetail,orderComment,finalOrderAddress,Common.currentUser.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            Toast.makeText(CartActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();

                            //Clear Cart
                            Common.cartRepository.emptyCart();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ERROR",t.getMessage());
                        }
                    });
        }
    }


    private void loadCartItem() {
        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayCartItem(carts);
                    }
                })
        );
    }

    private void displayCartItem(List<Cart> carts) {
        cartList = carts;
        cartAdapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(cartAdapter);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
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
        loadCartItem();
//        isBackButtonClicked = false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof  CartAdapter.CartViewHolder)
        {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Deleted item from adapter
            cartAdapter.removeItem(deletedIndex);
            //Delete item from Room Database
            Common.cartRepository.deleteCartItem(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append("Removed from Favorites List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.BLUE);
            snackbar.show();
        }
    }
}

package com.example.allen.androiddrinkshop.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import com.example.allen.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.allen.androiddrinkshop.Interface.IItemClickListener;
import com.example.allen.androiddrinkshop.Model.Category;
import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.R;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.drink_item_layout,null);
        return new DrinkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder drinkViewHolder, final int position) {

        //Load Image
        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(drinkViewHolder.img_product);

        drinkViewHolder.txt_drink_name.setText(drinkList.get(position).Name);
        drinkViewHolder.txt_drink_price.setText(new StringBuilder("$").append(drinkList.get(position).Price).toString());

        //Add to Cart System
        drinkViewHolder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.toppingAdded = new ArrayList<>();
                showAddToCartDialog(position);
            }
        });

        //Favorite System

        if(Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID)) == 1){
            drinkViewHolder.btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
        else{
            drinkViewHolder.btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }


        drinkViewHolder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID)) != 1){
                    assignFavorite(drinkList.get(position),true);
                    drinkViewHolder.btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                else{
                    assignFavorite(drinkList.get(position),false);
                    drinkViewHolder.btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }

                }
        });


        //Event
        drinkViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                //Stat new Activity

            }
        });


    }

    private void assignFavorite(Drink drink, boolean isAdd) {
        Favorite favorite = new Favorite();
        favorite.id = drink.ID;
        favorite.link = drink.Link;
        favorite.name = drink.Name;
        favorite.price = drink.Price;
        favorite.menuId = drink.MenuId;

        if(isAdd){
            Common.favoriteRepository.insertToFavorite(favorite);
        }
        else{
            Common.favoriteRepository.delete(favorite);
        }

    }


    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    private void showAddToCartDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_to_cart,null);

        //View
        ImageView img_product_dialog = itemView.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = itemView.findViewById(R.id.txt_count);
        TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);

        EditText edit_comment = itemView.findViewById(R.id.edit_comment);

        RadioButton rdi_sizeM = itemView.findViewById(R.id.rdi_sizeM);
        RadioButton rdi_sizeL = itemView.findViewById(R.id.rdi_sizeL);

        rdi_sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sizeOfCup=0;
                }
            }
        });
        rdi_sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sizeOfCup=1;
                }
            }
        });

        RadioButton rdi_sugar_100 = itemView.findViewById(R.id.rdi_sugar_100);
        RadioButton rdi_sugar_70 = itemView.findViewById(R.id.rdi_sugar_70);
        RadioButton rdi_sugar_50 = itemView.findViewById(R.id.rdi_sugar_50);
        RadioButton rdi_sugar_30 = itemView.findViewById(R.id.rdi_sugar_30);
        RadioButton rdi_sugar_free = itemView.findViewById(R.id.rdi_sugar_free);

        rdi_sugar_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sugar=0;
                }
            }
        });

        rdi_sugar_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sugar=30;
                }
            }
        });

        rdi_sugar_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sugar=50;
                }
            }
        });

        rdi_sugar_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sugar=70;
                }
            }
        });

        rdi_sugar_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.sugar=100;
                }
            }
        });

        RadioButton rdi_ice_100 = itemView.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = itemView.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = itemView.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = itemView.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = itemView.findViewById(R.id.rdi_ice_free);

        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.ice=0;
                }
            }
        });

        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.ice=30;
                }
            }
        });

        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.ice=50;
                }
            }
        });

        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.ice=70;
                }
            }
        });

        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.ice=100;
                }
            }
        });

        RecyclerView recycler_toppings = itemView.findViewById(R.id.recycler_toppings);
        recycler_toppings.setLayoutManager(new LinearLayoutManager(context));
        recycler_toppings.setHasFixedSize(true);

        ToppingAdapter toppingAdapter = new ToppingAdapter(context, Common.toppingList);
        recycler_toppings.setAdapter(toppingAdapter);

        //See data
        Picasso.with(context)
                .load(drinkList.get(position).Link)
                .into(img_product_dialog);
        txt_product_dialog.setText(drinkList.get(position).Name);

        builder.setView(itemView);
        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(Common.sizeOfCup == -1)
                {
                    Toast.makeText(context, "Please Choose size of cup", Toast.LENGTH_SHORT).show();
                }
                if(Common.sugar == -1)
                {
                    Toast.makeText(context, "Please Choose amount of Suger", Toast.LENGTH_SHORT).show();
                }
                if(Common.ice == -1)
                {
                    Toast.makeText(context, "Please Choose amount of Ice", Toast.LENGTH_SHORT).show();
                }

                showConfirmDialog(position,txt_count.getNumber());
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showConfirmDialog(final int position, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_confirm_add_to_cart,null);


        //View
        ImageView img_product_dialog = itemView.findViewById(R.id.img_cart_product);
        final TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);
        TextView txt_product_price_dialog = itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_sugar = itemView.findViewById(R.id.txt_sugar);
        TextView txt_ice = itemView.findViewById(R.id.txt_ice);
        final TextView txt_topping_extra = itemView.findViewById(R.id.txt_topping_extra);

        //Set Data
        Picasso.with(context).load(drinkList.get(position).Link).into(img_product_dialog);
        txt_product_dialog.setText(new StringBuilder(drinkList.get(position).Name)
                .append(" x")
                .append(Common.sizeOfCup == 0 ? " Size M:":" Size L")
                .append(number).toString());

        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Suger: ").append(Common.sugar).append("%").toString());

        double price = (Double.parseDouble(drinkList.get(position).Price)* Double.parseDouble(number)) + Common.toppingPrice;

        if(Common.sizeOfCup ==1){
            price+=(3.0*Double.parseDouble(number));
        }

        StringBuilder topping_final_comment = new StringBuilder((""));

        for (String line:Common.toppingAdded){
            topping_final_comment.append(line).append("\n");
        }

        txt_topping_extra.setText(topping_final_comment);

        final double finalPrice = Math.round(price);

        txt_product_price_dialog.setText(new StringBuilder("$").append(finalPrice));


        builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //Add to SQLite
                //Create new Cart item

                try{
                    Cart cartItem = new Cart();
                    cartItem.name = drinkList.get(position).Name;
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.ice = Common.ice;
                    cartItem.sugar = Common.sugar;
                    cartItem.price = finalPrice;
                    cartItem.size = Common.sizeOfCup;
                    cartItem.toppingExtras = txt_topping_extra.getText().toString();
                    cartItem.link = drinkList.get(position).Link;

                    //Add to DB
                    Common.cartRepository.insertToCart(cartItem);

                    Log.d("DEBUG_FLAG", new Gson().toJson(cartItem));

                    Toast.makeText(context, "Save item to cart success", Toast.LENGTH_SHORT).show();

                }catch (Exception ex){
                    Toast.makeText(context, "Error: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        builder.setView(itemView);
        builder.show();
    }
}

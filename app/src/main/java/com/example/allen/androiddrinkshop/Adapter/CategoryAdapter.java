package com.example.allen.androiddrinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allen.androiddrinkshop.DrinkActivity;
import com.example.allen.androiddrinkshop.Interface.IItemClickListener;
import com.example.allen.androiddrinkshop.Model.Category;
import com.example.allen.androiddrinkshop.R;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.menu_item_layout,null);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, final int position) {
        //Load Image
        Picasso.with(context)
                .load(categories.get(position).Link)
                .into(categoryViewHolder.img_product);

        categoryViewHolder.txt_menu_name.setText(categories.get(position).Name);

        //Event
        categoryViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentCategory = categories.get(position);

                //Stat new Activity
                context.startActivity(new Intent(context,DrinkActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

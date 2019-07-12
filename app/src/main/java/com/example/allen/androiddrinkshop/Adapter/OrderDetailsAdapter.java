package com.example.allen.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.allen.androiddrinkshop.Database.ModelDB.Cart;
import com.example.allen.androiddrinkshop.R;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>{

    Context context;
    List<Cart> cartList;

    public OrderDetailsAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_details_item_layout,viewGroup,false);
        return new OrderDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailsViewHolder orderDetailsViewHolder, final int position) {
        //Load Image
        Picasso.with(context)
                .load(cartList.get(position).link)
                .into(orderDetailsViewHolder.img_product);

        orderDetailsViewHolder.txt_price.setText(new StringBuilder("$").append(cartList.get(position).price).toString());
        orderDetailsViewHolder.txt_product_name.setText(new StringBuilder(cartList.get(position).name)
                .append(" x")
                .append(cartList.get(position).amount)
                .append(" ")
                .append(cartList.get(position).size == 0 ? " Size M:":" Size L"));
        orderDetailsViewHolder.txt_sugar_ice.setText(new StringBuilder("")
                .append("Sugar: ").append(cartList.get(position).sugar).append("%").append("\n")
                .append("Ice: ").append(cartList.get(position).ice).append("%").toString());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class OrderDetailsViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img_product;
        TextView txt_product_name, txt_sugar_ice, txt_price;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public OrderDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            txt_product_name = itemView.findViewById(R.id.txt_cart_product_name);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_price = itemView.findViewById(R.id.txt_price);

            view_background = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);


        }
    }

    public void removeItem(int position){
        cartList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Cart item, int position){
        cartList.add(position,item);
        notifyItemInserted(position);
    }
}

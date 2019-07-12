package com.example.allen.androiddrinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.allen.androiddrinkshop.Interface.IItemClickListener;
import com.example.allen.androiddrinkshop.Model.Order;
import com.example.allen.androiddrinkshop.OrderDetailsActivity;
import com.example.allen.androiddrinkshop.R;
import com.example.allen.androiddrinkshop.Utils.Common;

import java.util.List;

public class OrderAdapter  extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    Context context;
    List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_item_layout,viewGroup,false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int position) {
        orderViewHolder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(position).getOrderId()).toString());
        orderViewHolder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(position).getOrderPrice()).toString());
        orderViewHolder.txt_order_address.setText(orderList.get(position).getOrderAddress());
        orderViewHolder.txt_order_comment.setText(orderList.get(position).getOrderComment());
        orderViewHolder.txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(orderList.get(position).getOrderStatus())));

        orderViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentOrder = orderList.get(position);
                context.startActivity(new Intent(context,OrderDetailsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txt_order_id, txt_order_price,txt_order_address,txt_order_comment,txt_order_status;

        IItemClickListener itemClickListener;

        public void setItemClickListener(IItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public OrderViewHolder(View itemView) {
            super(itemView);

            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_price = itemView.findViewById(R.id.txt_order_price);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_order_comment = itemView.findViewById(R.id.txt_order_comment);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }
}

package com.example.allen.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.R;
import com.example.allen.androiddrinkshop.Utils.Common;

import java.util.List;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder>{

    Context context;
    List<Drink> toppingList;

    public ToppingAdapter(Context context, List<Drink> toppingList) {
        this.context = context;
        this.toppingList = toppingList;
    }

    @NonNull
    @Override
    public ToppingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.topping_item_layout,null);
        return new ToppingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingViewHolder toppingViewHolder, final int position) {
        toppingViewHolder.checkBox.setText(toppingList.get(position).Name);
        toppingViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Common.toppingAdded.add(buttonView.getText().toString());
                    Common.toppingPrice+=Double.parseDouble(toppingList.get(position).Price);
                }
                else{
                    Common.toppingAdded.remove(buttonView.getText().toString());
                    Common.toppingPrice-=Double.parseDouble(toppingList.get(position).Price);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toppingList.size();
    }

    class ToppingViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public ToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.ckb_topping);
        }
    }
}

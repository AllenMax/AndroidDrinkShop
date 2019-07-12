package com.example.allen.androiddrinkshop.Utils;

import android.support.v7.widget.RecyclerView;

public interface RecylerItemTouchHelperListener {
    void onSwiped (RecyclerView.ViewHolder viewHolder, int direction, int position);
}

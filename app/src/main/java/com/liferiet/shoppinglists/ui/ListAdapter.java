package com.liferiet.shoppinglists.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;

import java.util.List;

/**
 * Created by liferiet on 15.11.2018.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<Product> mProductsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;

        public MyViewHolder(View v) {
            super(v);
            productName = v.findViewById(R.id.list_item_name);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
        }
    }

    public ListAdapter(List<Product> productList) {
        mProductsList = productList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View productView = inflater.inflate(R.layout.list_item, parent, false);

        MyViewHolder vh = new MyViewHolder(productView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Product product = mProductsList.get(position);

        TextView textView = holder.productName;
        textView.setText(product.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mProductsList.size();
    }

    public void removeItem(int position) {
        mProductsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Product product, int position) {
        mProductsList.add(position, product);
        // notify item added by position
        notifyItemInserted(position);
    }
}

package com.liferiet.shoppinglists.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.ListItemBinding;

import java.util.List;

/**
 * Created by liferiet on 15.11.2018.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListItemViewHolder> {

    private List<Product> mProductList;
    private OnListItemClickListener mOnListItemClickListener;

    public ListAdapter(List<Product> productList, OnListItemClickListener listener) {
        mProductList = productList;
        mOnListItemClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        ListItemBinding binding = DataBindingUtil.inflate(
                inflater, layoutIdForItem, parent, false);

        return new ListItemViewHolder(binding);
    }

    /**
     * Interface for class that will handle click events on view holders
     */
    interface OnListItemClickListener {
        void onListItemClick(Product product);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public void removeItem(int position) {
        mProductList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Product product, int position) {
        mProductList.add(position, product);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ListItemBinding binding;
/*      TODO delete this
        public TextView productName;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;*/

        public ListItemViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Product product = mProductList.get(position);
            binding.listItemName.setText(product.getName());
        }

        @Override
        public void onClick(View view) {
            mOnListItemClickListener.onListItemClick(mProductList.get(getAdapterPosition()));
        }
    }
}

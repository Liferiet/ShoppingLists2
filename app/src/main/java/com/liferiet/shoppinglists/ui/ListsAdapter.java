package com.liferiet.shoppinglists.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.ShoppingList;
import com.liferiet.shoppinglists.databinding.ListItemBinding;

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ItemViewHolder> {

    private static final String TAG = ListsAdapter.class.getSimpleName();
    private List<ShoppingList> mListShoppingList;
    private ListsAdapter.OnListItemClickListener mOnListItemClickListener;

    public ListsAdapter(ListsAdapter.OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    /**
     * Interface for class that will handle click events on view holders
     */
    interface OnListItemClickListener {
        void onListItemClick(ShoppingList shoppingList);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        ListItemBinding binding = DataBindingUtil.inflate(
                inflater, layoutIdForItem, parent, false);

        return new ListsAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return getListList().size();
    }

    public void setListList(List<ShoppingList> listShoppingList) {
        this.mListShoppingList = listShoppingList;
    }

    public List<ShoppingList> getListList() {
        return mListShoppingList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ListItemBinding binding;

        public ItemViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            binding.listName.setText(mListShoppingList.get(position).getName());
        }

        @Override
        public void onClick(View view) {
            try {
                mOnListItemClickListener.onListItemClick(mListShoppingList.get(getAdapterPosition()));
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.v(TAG, "onClick listener index out of bond");
            }
        }
    }
}

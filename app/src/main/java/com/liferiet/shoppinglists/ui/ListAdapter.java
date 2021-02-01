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
import com.liferiet.shoppinglists.databinding.ListItemBinding;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> {

    private static final String TAG = ListAdapter.class.getSimpleName();
    private List<String> mListList;
    private ListAdapter.OnListItemClickListener mOnListItemClickListener;

    public ListAdapter(ListAdapter.OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    /**
     * Interface for class that will handle click events on view holders
     */
    interface OnListItemClickListener {
        void onListItemClick(String string);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        ListItemBinding binding = DataBindingUtil.inflate(
                inflater, layoutIdForItem, parent, false);

        return new ListAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return getListList().size();
    }

    public void setListList(List<String> listList) {
        this.mListList = listList;
    }

    public List<String> getListList() {
        return mListList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ListItemBinding binding;

        public ItemViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            binding.listName.setText(mListList.get(position));
        }

        @Override
        public void onClick(View view) {
            try {
                mOnListItemClickListener.onListItemClick(mListList.get(getAdapterPosition()));
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.v(TAG, "onClick listener index out of bond");
            }
        }
    }
}

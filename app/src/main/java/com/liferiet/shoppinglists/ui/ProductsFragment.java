package com.liferiet.shoppinglists.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.databinding.FragmentProductsBinding;
import com.liferiet.shoppinglists.viewmodel.ProductsViewModel;
import com.liferiet.shoppinglists.viewmodel.ProductsViewModelFactory;

import java.util.List;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

/**
 * Created by liferiet on 26.01.2021.
 */

public class ProductsFragment extends Fragment
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        ProductsAdapter.OnListItemClickListener {

    private static final String TAG = ProductsFragment.class.getSimpleName();

    private FragmentProductsBinding mBinding;
    private ProductsViewModel mViewModel;

    private ProductsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_products, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null) {
            // co jesli dostane zly bundle?
            Log.d(TAG, "no arguments received");
        }

        ProductsFragmentArgs args =  ProductsFragmentArgs.fromBundle(getArguments());
        String listKey = args.getListKey();
        String listName = args.getListName();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ProductsViewModelFactory factory = new ProductsViewModelFactory(
                FirebaseDatabase.getInstance(), listKey, sharedPreferences);
        mViewModel = new ViewModelProvider(this, factory).get(ProductsViewModel.class);

        mViewModel.setListName(listName);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView mRecyclerView = mBinding.rvLists;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ProductsAdapter( this);

        mViewModel.getProductList().observe(getActivity(), products -> {
            mAdapter.setProductList(products);
            mRecyclerView.setAdapter(mAdapter);
        });

        getActivity().setTitle(mViewModel.getListName());

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        Log.d(TAG, "Repository list key: " + mViewModel.getListKey());

        setupFabOnClickListener();
    }

    private void setupFabOnClickListener() {
        Fragment fragment = this;
        FloatingActionButton fab = mBinding.fabAddNewProduct;
        fab.setOnClickListener(view -> {
            String listKey = mViewModel.getListKey();
            String userName = mViewModel.getUserName().getValue();
            Product product = new Product();
            product.setUser(userName);

            NavDirections action = ProductsFragmentDirections.actionProductsFragmentToDetailsFragment(listKey, product, userName);
            NavHostFragment.findNavController(fragment).navigate(action);
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof ProductsAdapter.ListItemViewHolder) {
            List<Product> productList = mViewModel.getProductList().getValue();

            if (productList == null) return;
            final int deletedIndex = viewHolder.getAdapterPosition();
            // get the removed item name to display it in snack bar
            String name = productList.get(deletedIndex).getName();

            // backup of removed item for undo purpose
            final Product deletedItem = productList.get(deletedIndex);

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mBinding.coordinatorLayout, name + " " + getString(R.string.info_deleted), LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo).toUpperCase(), view ->
                // undo is selected, restore the deleted item
                mAdapter.restoreItem(deletedItem, deletedIndex)
            );

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT ||
                            event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {

                        mViewModel.removeProduct(deletedItem);
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onListItemClick(Product product) {
        String listKey = mViewModel.getListKey();
        String userName = mViewModel.getUserName().getValue();

        NavDirections action = ProductsFragmentDirections.actionProductsFragmentToDetailsFragment(listKey, product, userName);
        NavHostFragment.findNavController(this).navigate(action);
    }
}

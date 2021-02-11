package com.liferiet.shoppinglists.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
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

/**
 * Created by liferiet on 26.01.2021.
 */

public class ProductsFragment extends Fragment
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        ProductsAdapter.OnListItemClickListener {

    private static final String TAG = ProductsFragment.class.getSimpleName();
    public static final String EXTRA_PRODUCT = "product";
    private static final String LIST_KEY = "list_key";
    private static final String LIST_NAME = "list_name";
    private static final String USER_NAME = "user_name";

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
        if (bundle == null || !bundle.containsKey(LIST_NAME) || !bundle.containsKey(LIST_KEY)) {
            // co jesli dostane zly bundle?
        }
        String listKey = bundle.getString(LIST_KEY);
        String listName = bundle.getString(LIST_NAME);

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
        FloatingActionButton fab = (FloatingActionButton) mBinding.fabAddNewProduct;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(LIST_KEY, mViewModel.getListKey());
                bundle.putString(USER_NAME, mViewModel.getUserName().getValue());

                Fragment productsFragment = new DetailsFragment();
                productsFragment.setArguments(bundle);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, productsFragment) // give your fragment container id in first parameter
                        .addToBackStack(null)  // if written, this transaction will be added to backstack
                        .commit();

                Toast.makeText(getActivity(), "Otworzy okienko nowego produktu: " + " klucz listy: " + mViewModel.getListKey(), Toast.LENGTH_LONG)
                        .show();
            }
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
                    .make(mBinding.coordinatorLayout, name + " " + getString(R.string.info_deleted), Snackbar
                            .LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo).toUpperCase(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });

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
        Bundle bundle = new Bundle();
        bundle.putString(LIST_KEY, mViewModel.getListKey());
        bundle.putParcelable(EXTRA_PRODUCT, (Parcelable) product);
        //bundle.putString(USER_NAME, mViewModel.getUserName().getValue());

        Fragment productsFragment = new DetailsFragment();
        productsFragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragment_container, productsFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();

        Toast.makeText(getActivity(), "Wyswietli informacje o " + product.getName(), Toast.LENGTH_SHORT)
                .show();
    }
}

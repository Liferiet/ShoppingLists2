package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Parcelable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.ActivityListOfProductsBinding;
import com.liferiet.shoppinglists.viewmodel.ListOfProductsViewModel;
import com.liferiet.shoppinglists.viewmodel.ListOfProductsViewModelFactory;

/**
 * Created by liferiet on 26.01.2021.
 */

public class ListOfProductsActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        ListAdapter.OnListItemClickListener {

    public static final String EXTRA_PRODUCT = "product";

    private ActivityListOfProductsBinding mBinding;
    private ListOfProductsViewModel mViewModel;

    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_products);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ListOfProductsViewModelFactory factory = new ListOfProductsViewModelFactory(
                FirebaseDatabase.getInstance(), getString(R.string.shoppingLists), sharedPreferences);
        mViewModel = new ViewModelProvider(this, factory).get(ListOfProductsViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_of_products);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView mRecyclerView = mBinding.rvLists;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListAdapter( this);

        mViewModel.getProductList().observe(this, products -> {
            mAdapter.setProductList(products);
            mRecyclerView.setAdapter(mAdapter);
        });

        mViewModel.getUserName().observe(this, name -> {
            setTitle("Hello " + name);
        });


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        FloatingActionButton fab = (FloatingActionButton) mBinding.fabAddNewProduct;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListOfProductsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("user", mViewModel.getUserName().getValue());
                ListOfProductsActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

/*        TODO do odkomentowania
            if (viewHolder instanceof ListAdapter.ListItemViewHolder) {
            // get the removed item name to display it in snack bar
            String name = productList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Product deletedItem = productList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

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
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(EXTRA_PRODUCT, (Parcelable) product);
        intent.putExtra("user", mViewModel.getUserName().getValue());
        startActivity(intent);
        Toast.makeText(this, "Wyswietli informacje o " + product.getName(), Toast.LENGTH_SHORT)
                .show();
    }
}

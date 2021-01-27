package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liferiet.shoppinglists.data.FirebaseRepository;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liferiet on 26.01.2021.
 */

public class MainActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Product> productList;
    private FirebaseRepository repository;

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View outerLayout;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        repository = new FirebaseRepository(FirebaseDatabase.getInstance(),
                getString(R.string.shoppingLists));
        productList = new ArrayList<>();
        outerLayout = findViewById(R.id.coordinator_layout);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new ListAdapter(productList);

        mRecyclerView = findViewById(R.id.rvLists);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_new_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(MainActivity.this,AddProductActivity.class);
                MainActivity.this.startActivity(newIntent);
            }
        });

        setTitle("Hello " + mUserName);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserName = sharedPreferences.getString(getString(R.string.user_name_key), getString(R.string.user_name_default));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        repository.getReference(getString(R.string.shoppingLists)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                Log.w("ShoppingListsApp", "getUser:onCancelled " + dataSnapshot.toString
                        ());
                Log.w("ShoppingListsApp", "count = " + dataSnapshot.getChildrenCount() + " values " + dataSnapshot.getKey());
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    productList.add(product);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ShoppingListsApp", "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof ListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = productList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Product deletedItem = productList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(outerLayout, name + " " + getString(R.string.info_deleted), Snackbar
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

                        repository.removeProductFromDatabase(deletedItem);
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

/*    private void removeProductFromDatabase(Product product) {
        DatabaseReference dbRef = getReference(getString(R.string.shoppingLists));
        Query query = dbRef.orderByChild("name").equalTo(product.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    postsnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

/*    private DatabaseReference getReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.user_name_key))) {
            Toast.makeText(this, "Ktos zmienil nazwe uzytkownika!", Toast.LENGTH_SHORT).show();
            mUserName = sharedPreferences.getString(key, "User");
            setTitle("Hello " + mUserName);
        } else {
            Toast.makeText(this, "To mamy jakies inne opcje?", Toast.LENGTH_SHORT).show();
        }
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

}

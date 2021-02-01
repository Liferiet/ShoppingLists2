package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.ActivityListOfListsBinding;
import com.liferiet.shoppinglists.viewmodel.ListOfListsViewModel;
import com.liferiet.shoppinglists.viewmodel.ListOfListsViewModelFactory;
import com.liferiet.shoppinglists.viewmodel.ListOfProductsViewModel;

public class ListOfListsActivity extends AppCompatActivity implements ListAdapter.OnListItemClickListener {

    private static final String TAG = ListOfListsActivity.class.getSimpleName();
    private static final String LIST_REFERENCE = "list_reference";
    private ListOfListsViewModel mViewModel;
    private ActivityListOfListsBinding mBinding;

    private ListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_lists);

        ListOfListsViewModelFactory factory = new ListOfListsViewModelFactory(
                FirebaseDatabase.getInstance());
        mViewModel = new ViewModelProvider(this, factory).get(ListOfListsViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_of_lists);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView mRecyclerView = mBinding.rvLists;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListAdapter( this);

        mAdapter.setListList(mViewModel.getLists());
        mRecyclerView.setAdapter(mAdapter);

/*        mViewModel.getLists().observe(this, list -> {
            mAdapter.setListList(list);
            mRecyclerView.setAdapter(mAdapter);
        });*/
        Log.d(TAG, "Adapter data: " + mAdapter.getListList());
    }

    @Override
    public void onListItemClick(String string) {
        Intent intent = new Intent(this, ListOfProductsActivity.class);
        intent.putExtra(LIST_REFERENCE, string);
        startActivity(intent);
        Toast.makeText(this, "Otworzy liste: " + string, Toast.LENGTH_SHORT)
                .show();
    }
}

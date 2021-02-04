package com.liferiet.shoppinglists.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.ShoppingList;
import com.liferiet.shoppinglists.databinding.ActivityListOfListsBinding;
import com.liferiet.shoppinglists.viewmodel.ListOfListsViewModel;
import com.liferiet.shoppinglists.viewmodel.ListOfListsViewModelFactory;

public class ListOfListsActivity extends AppCompatActivity
        implements ListAdapter.OnListItemClickListener {

    private static final String TAG = ListOfListsActivity.class.getSimpleName();
    private static final String LIST_KEY = "list_key";
    private static final String LIST_NAME = "list_name";

    private ListOfListsViewModel mViewModel;
    private ActivityListOfListsBinding mBinding;

    private ListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_lists);

        ListOfListsViewModelFactory factory = new ListOfListsViewModelFactory(
                FirebaseDatabase.getInstance(), getString(R.string.shoppingLists));
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

        setupFabOnClickListener();

    }

    @Override
    public void onListItemClick(ShoppingList list) {
        Intent intent = new Intent(this, ListOfProductsActivity.class);
        intent.putExtra(LIST_KEY, getString(R.string.shoppingLists) + "/" + list.getKey());
        intent.putExtra(LIST_NAME, list.getName());
        startActivity(intent);
        Toast.makeText(this, "Otworzy liste: " + list.getName(), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     *  Setup OnClickListener to fab, which after click event creates new AlertDialog,
     *  shows it, and change positiveClickButton onClickListener right away
     */
    private void setupFabOnClickListener() {
        mBinding.fabAddNewList.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title")
                    .setView(R.layout.dialog_create_list)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Do nothing here because we override this button later to change the close behaviour.
                        }
                    })
                    .create();
            final AlertDialog dialog = builder.create();

            dialog.show();
            TextInputLayout textInputLayout = dialog.findViewById(R.id.text_input_layout);
            TextInputEditText listNameEditText = dialog.findViewById(R.id.dialog_list_name);
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            if (textInputLayout == null || listNameEditText == null) {
                Log.d(TAG, "views are null; abort abort");
                return;
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mViewModel.getLastTimeClicked() < 3000){
                        return;
                    }
                    mViewModel.setLastTimeClicked(SystemClock.elapsedRealtime());

                    Log.d("FAB_CLICK", "yeeey correct On click Event");
                    String value = listNameEditText.getText().toString();
                    if (value.isEmpty()){
                        Log.d(TAG, "onClick, empty value");
                        textInputLayout.setError("Nazwa nie moze byc pusta");


                    } else {
                        Log.d(TAG, "onClick, value: " + value);
                        onDialogPositiveClick(value);
                        dialog.dismiss();
                    }
                }
            });

/*            Log.d(TAG, "fab: onClickL: before creating fragment");
            CreateListDialogFragment addListTitle = new CreateListDialogFragment();
            Log.d(TAG, "fab: onClickL: after creating fragment");
            addListTitle.show(getSupportFragmentManager(), TAG);
            Log.d(TAG, "fab: onClickL: after show fragment");
            //addListTitle.getDialog().setOnShowListener(addListTitle);*/
        });
    }

    public void onDialogPositiveClick(String listName) {
        mViewModel.createNewList(listName);
        /* // Uncomment this if list have to be opened immediately after creation
        ShoppingList list = mViewModel.getLists().get(mViewModel.getLists().size() - 1);
        onListItemClick(list);*/
    }
}

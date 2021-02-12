package com.liferiet.shoppinglists.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.ShoppingList;
import com.liferiet.shoppinglists.databinding.FragmentListsBinding;
import com.liferiet.shoppinglists.viewmodel.ListsViewModel;
import com.liferiet.shoppinglists.viewmodel.ListsViewModelFactory;

public class ListsFragment extends Fragment
        implements ListsAdapter.OnListItemClickListener {

    private static final String TAG = ListsFragment.class.getSimpleName();

    private ListsViewModel mViewModel;
    private FragmentListsBinding mBinding;

    private ListsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_lists, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListsViewModelFactory factory = new ListsViewModelFactory(
                FirebaseDatabase.getInstance(), getString(R.string.shoppingLists), getActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(ListsViewModel.class);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView mRecyclerView = mBinding.rvLists;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListsAdapter( this);

        mAdapter.setListList(mViewModel.getLists());
        mRecyclerView.setAdapter(mAdapter);

        setupFabOnClickListener(getActivity());
    }


    @Override
    public void onListItemClick(ShoppingList list) {
        String listKey = mViewModel.getDbPath() + "/" + list.getKey();
        String listName = list.getName();
        NavDirections action = ListsFragmentDirections.actionListsFragmentToProductsFragment(listKey, listName);
        NavHostFragment.findNavController(this).navigate(action);
    }

    /**
     *  Setup OnClickListener to fab, which after click event creates new AlertDialog,
     *  shows it, and change positiveClickButton onClickListener right away
     */
    private void setupFabOnClickListener(Context context) {
        mBinding.fabAddNewList.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

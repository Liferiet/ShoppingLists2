package com.liferiet.shoppinglists.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.ShoppingList;
import com.liferiet.shoppinglists.repository.ListOfListsRepository;

import java.util.ArrayList;
import java.util.List;

public class ListOfListsViewModel extends ViewModel {

    private static final String TAG = ListOfListsViewModel.class.getSimpleName();
    private List<ShoppingList> mShoppingLists;
    private ListOfListsRepository mRepository;
    private String mDbReference;
    private Long mLastTimeClicked;

    public ListOfListsViewModel(FirebaseDatabase db, String dbReference) {
        // TODO

        Log.d(TAG, "Preparing listOfLists viewModel");
        //mLists = new MutableLiveData<>();

        mShoppingLists = new ArrayList<>();
        mDbReference = dbReference;
        mRepository = ListOfListsRepository.getInstance(db, mDbReference);
        mLastTimeClicked = 0L;
    }

    public void createNewList(String name) {
        String key = mRepository.createListReference(name);
        ShoppingList list = new ShoppingList();
        list.setKey(key);
        list.setName(name);

        mShoppingLists.add(list);
    }

    public List<ShoppingList> getLists() {
        return mShoppingLists;
    }

    public Long getLastTimeClicked() {
        return mLastTimeClicked;
    }

    public void setLastTimeClicked(Long lastTimeClicked) {
        this.mLastTimeClicked = lastTimeClicked;
    }
}

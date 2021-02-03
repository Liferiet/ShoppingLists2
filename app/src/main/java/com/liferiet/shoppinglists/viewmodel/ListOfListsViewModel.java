package com.liferiet.shoppinglists.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ListOfListsViewModel extends ViewModel {

    private static final String TAG = ListOfListsViewModel.class.getSimpleName();
    private List<ShoppingList> mShoppingLists;
    private Long mLastTimeClicked;

    public ListOfListsViewModel(FirebaseDatabase db) {
        // TODO

        Log.d(TAG, "Preparing listOfLists viewModel");
        //mLists = new MutableLiveData<>();
        ArrayList<ShoppingList> tempShoppingList = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName("Domowa");
        shoppingList.setKey("ShoppingLists");
        tempShoppingList.add(shoppingList);

        mShoppingLists = tempShoppingList;
        mLastTimeClicked = 0L;
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

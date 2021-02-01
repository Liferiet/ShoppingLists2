package com.liferiet.shoppinglists.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.FirebaseRepository;

import java.util.ArrayList;
import java.util.List;

public class ListOfListsViewModel extends ViewModel {

    private static final String TAG = ListOfListsViewModel.class.getSimpleName();
    private List<String> mLists;

    public ListOfListsViewModel(FirebaseDatabase db) {
        // TODO

        Log.d(TAG, "Preparing listOfLists viewModel");
        //mLists = new MutableLiveData<>();

        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("ShoppingLists");
        mLists = tempList;
    }

    public List<String> getLists() {
        return mLists;
    }
}

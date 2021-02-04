package com.liferiet.shoppinglists.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class ListOfListsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseDatabase mDb;
    private final String mDbReference;

    public ListOfListsViewModelFactory(FirebaseDatabase db, String dbReference) {
        mDb = db;
        mDbReference = dbReference;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ListOfListsViewModel(mDb, mDbReference);
    }
}

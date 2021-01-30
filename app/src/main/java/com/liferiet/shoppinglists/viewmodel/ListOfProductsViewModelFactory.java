package com.liferiet.shoppinglists.viewmodel;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class ListOfProductsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseDatabase mDb;
    private final String mListReference;
    private final SharedPreferences mPreferences;

    public ListOfProductsViewModelFactory(FirebaseDatabase db, String listReference, SharedPreferences preferences) {
        mDb = db;
        mListReference = listReference;
        mPreferences = preferences;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ListOfProductsViewModel(mDb, mListReference, mPreferences);
    }
}

package com.liferiet.shoppinglists.viewmodel;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class ProductDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseDatabase mDb;
    private final String mListReference;

    public ProductDetailsViewModelFactory(FirebaseDatabase db, String listReference) {
        mDb = db;
        mListReference = listReference;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProductDetailsViewModel(mDb, mListReference);
    }
}

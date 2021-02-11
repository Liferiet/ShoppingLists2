package com.liferiet.shoppinglists.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class ListsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseDatabase mDb;
    private final String mDbReference;
    private final Application mApplication;

    public ListsViewModelFactory(FirebaseDatabase db, String dbReference, Application application) {
        mDb = db;
        mDbReference = dbReference;
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ListsViewModel(mDb, mDbReference, mApplication);
    }
}

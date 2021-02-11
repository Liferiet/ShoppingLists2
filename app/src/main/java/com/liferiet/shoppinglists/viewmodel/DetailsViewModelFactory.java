package com.liferiet.shoppinglists.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseDatabase mDb;
    private final String mListReference;

    public DetailsViewModelFactory(FirebaseDatabase db, String listReference) {
        mDb = db;
        mListReference = listReference;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(mDb, mListReference);
    }
}

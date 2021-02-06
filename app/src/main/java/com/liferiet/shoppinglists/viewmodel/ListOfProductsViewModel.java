package com.liferiet.shoppinglists.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liferiet.shoppinglists.repository.ListOfProductsRepository;
import com.liferiet.shoppinglists.data.Product;

import java.util.ArrayList;
import java.util.List;

public class ListOfProductsViewModel extends ViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = ListOfProductsViewModel.class.getSimpleName();

    private ListOfProductsRepository mRepository;
    private MutableLiveData<List<Product>> mProductList;
    private MutableLiveData<String> mUserName;
    private SharedPreferences mPreferences;
    private String mListName;

    public ListOfProductsViewModel(FirebaseDatabase db, String listKey, SharedPreferences preferences) {

        Log.d(TAG, "Preparing listOfProducts viewModel");
        mRepository = ListOfProductsRepository.getInstance(db, listKey);
        mPreferences = preferences;

        mUserName = new MutableLiveData<>();
        mUserName.postValue(preferences.getString("user name key", "default user name")); // TODO uzyc jakos stringow z resource'ow
        preferences.registerOnSharedPreferenceChangeListener(this);

        mProductList = new MutableLiveData<>();
        observeProductChanges();
    }

    public LiveData<List<Product>> getProductList() {
        return mProductList;
    }

    private void observeProductChanges() {
        mRepository.getReference(mRepository.getListKey()).child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG, "count = " + dataSnapshot.getChildrenCount() + " values " + dataSnapshot.getKey());
                ArrayList<Product> products = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    if (product != null )product.setId(data.getKey());
                    products.add(product);
                }
                mProductList.postValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    public LiveData<String> getUserName() {
        return mUserName;
    }

    public void removeProduct(Product product) {
        mRepository.removeProductFromDatabase(product);
    }

    public String getListKey() {
        return mRepository.getListKey();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String userNameKey = "user name key"; // TODO uzyc jakos stringa z resource'ow
        if (key.equals(userNameKey)) {
            mUserName.postValue(sharedPreferences.getString(key, "User"));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void setListName(String listName) {
        mListName = listName;
    }

    public String getListName() {
        return mListName;
    }
}

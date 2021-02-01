package com.liferiet.shoppinglists.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liferiet.shoppinglists.data.FirebaseRepository;
import com.liferiet.shoppinglists.data.Product;

import java.util.ArrayList;
import java.util.List;

public class ListOfProductsViewModel extends ViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = ListOfProductsViewModel.class.getSimpleName();

    private FirebaseRepository mRepository;
    private String mListReference;
    private MutableLiveData<List<Product>> mProductList;
    private MutableLiveData<String> mUserName;
    private SharedPreferences mPreferences;

    public ListOfProductsViewModel(FirebaseDatabase db, String listReference, SharedPreferences preferences) {

        Log.d(TAG, "Preparing listOfProducts viewModel");
        mListReference = listReference;
        mRepository = FirebaseRepository.getInstance(db, mListReference);
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

    public void setListReference(String listReference) {
        this.mListReference = listReference;
    }

    private void observeProductChanges() {
        getListReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG,  "getUser:onCancelled " + dataSnapshot.toString());
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

    public DatabaseReference getListReference() {
        return mRepository.getReference(mListReference);
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
}

package com.liferiet.shoppinglists.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.repository.DetailsRepository;
import com.liferiet.shoppinglists.data.Product;

public class DetailsViewModel extends ViewModel {

    private static final String TAG = DetailsViewModel.class.getSimpleName();
    private DetailsRepository mRepository;
    private Product mProduct;
    private Long mLastTimeClicked;

    public DetailsViewModel(FirebaseDatabase db, String listKey) {
        mRepository = DetailsRepository.getInstance(db, listKey);
        mLastTimeClicked = 0L;
    }

    public void saveProduct(Product product, DatabaseReference.CompletionListener listener) {
        mRepository.saveProduct(product, listener);
    }

    public Product getProduct() {
        return mProduct;
    }

    public void setProduct(Product product) {
        this.mProduct = product;
    }

    public Long getLastTimeClicked() {
        return mLastTimeClicked;
    }

    public void setLastTimeClicked(Long lastTimeClicked) {
        this.mLastTimeClicked = lastTimeClicked;
    }
}

package com.liferiet.shoppinglists.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.repository.ProductDetailsRepository;
import com.liferiet.shoppinglists.data.Product;

public class ProductDetailsViewModel extends ViewModel {

    private static final String TAG = ProductDetailsViewModel.class.getSimpleName();
    private ProductDetailsRepository mRepository;
    private Product mProduct;
    private Long mLastTimeClicked;

    public ProductDetailsViewModel(FirebaseDatabase db, String listKey) {
        mRepository = ProductDetailsRepository.getInstance(db, listKey);
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

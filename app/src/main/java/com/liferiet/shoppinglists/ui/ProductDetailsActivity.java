package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.databinding.ActivityProductDetailsBinding;
import com.liferiet.shoppinglists.viewmodel.ProductDetailsViewModel;
import com.liferiet.shoppinglists.viewmodel.ProductDetailsViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liferiet on 15.11.2018.
 */

public class ProductDetailsActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    private static final String TAG = ProductDetailsActivity.class.getSimpleName();
    public static final String EXTRA_PRODUCT = "product";
    public static final String USER = "user";

    private ProductDetailsViewModel mViewModel;
    private ActivityProductDetailsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);

        ProductDetailsViewModelFactory factory = new ProductDetailsViewModelFactory(
                FirebaseDatabase.getInstance(), getString(R.string.shoppingLists));
        mViewModel = new ViewModelProvider(this, factory).get(ProductDetailsViewModel.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PRODUCT)) {
            mViewModel.setProduct(intent.getParcelableExtra(EXTRA_PRODUCT));
        } else if (intent != null && intent.hasExtra(USER)) {
            Product product = new Product();
            product.setId("");
            product.setUser(intent.getStringExtra(USER));

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("en"));
            product.setDate(formatter.format(date));

            mViewModel.setProduct(product);
        } else {
            finish();
        }

        setupUI();

        FloatingActionButton fab = mBinding.fabUploadProduct;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mViewModel.getLastTimeClicked() < 3000){
                    return;
                }
                mViewModel.setLastTimeClicked(SystemClock.elapsedRealtime());
                Log.d(TAG, "Save button clicked");
                saveProduct();
            }
        });
    }

    private void setupUI() {
        Product product = mViewModel.getProduct();
        if (product == null) return;

        mBinding.nameEditText.setText(product.getName());
        mBinding.descriptionEditText.setText(product.getMessage());
        mBinding.dateTextView.setText(product.getDate());
        mBinding.addedByTextView.setText(product.getUser());
    }

    public void saveProduct() {
        String productName = mBinding.nameEditText.getText().toString();
        String productDescription = mBinding.descriptionEditText.getText().toString();

        Product product = mViewModel.getProduct();
        product.setName(productName);
        product.setMessage(productDescription);

        mViewModel.saveProduct(product, this);
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError == null) {
            finish();
        }
    }
}

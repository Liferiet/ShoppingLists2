package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.FirebaseRepository;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.ActivityProductDetailsBinding;

/**
 * Created by liferiet on 15.11.2018.
 */

public class ProductDetailsActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    public static final String EXTRA_PRODUCT = "product";
    private FirebaseRepository repository;
    private ActivityProductDetailsBinding mBinding;
    private Product product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PRODUCT)) {
            // mButton.setText(R.string.update_button);
            product = intent.getParcelableExtra(EXTRA_PRODUCT);
            mBinding.nameEditText.setText(product.getName());
            mBinding.descriptionEditText.setText(product.getMessage());

        }

        repository = new FirebaseRepository(FirebaseDatabase.getInstance(),
                getString(R.string.shoppingLists));

        FloatingActionButton fab = mBinding.fabUploadProduct;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });
    }

    public void saveProduct() {
        EditText productName = mBinding.nameEditText;
        EditText productDescription = mBinding.descriptionEditText;

        Product product = new Product();
        product.setName(productName.getText().toString());
        product.setMessage(productDescription.getText().toString());

        repository.saveProduct(product, this);
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError == null) {
            finish();
        }
    }
}

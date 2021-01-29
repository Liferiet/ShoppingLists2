package com.liferiet.shoppinglists.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.FirebaseRepository;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.ActivityProductDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liferiet on 15.11.2018.
 */

public class ProductDetailsActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    public static final String EXTRA_PRODUCT = "product";
    private FirebaseRepository repository;
    private ActivityProductDetailsBinding mBinding;
    private String productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        productId = "";

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PRODUCT)) {
            // mButton.setText(R.string.update_button);
            Product product;
            product = intent.getParcelableExtra(EXTRA_PRODUCT);
            productId = product.getId();
            mBinding.nameEditText.setText(product.getName());
            mBinding.descriptionEditText.setText(product.getMessage());
            mBinding.dateTextView.setText(product.getDate());
            mBinding.addedByTextView.setText(product.getUser());
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String user = sharedPreferences.getString(getString(R.string.user_name_key), getString(R.string.user_name_default));

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            mBinding.dateTextView.setText(formatter.format(date));
            mBinding.addedByTextView.setText(user);
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
        String productName = mBinding.nameEditText.getText().toString();
        String productDescription = mBinding.descriptionEditText.getText().toString();
        String date = mBinding.dateTextView.getText().toString();
        String user = mBinding.addedByTextView.getText().toString();

        Product product = new Product();
        product.setId(productId);
        product.setName(productName);
        product.setMessage(productDescription);
        product.setUser(user);
        product.setDate(date);

        repository.saveProduct(product, this);
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError == null) {
            finish();
        }
    }
}

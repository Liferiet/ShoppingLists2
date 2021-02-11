package com.liferiet.shoppinglists.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.databinding.FragmentDetailsBinding;
import com.liferiet.shoppinglists.viewmodel.ProductDetailsViewModel;
import com.liferiet.shoppinglists.viewmodel.ProductDetailsViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liferiet on 15.11.2018.
 */

public class DetailsFragment extends Fragment implements DatabaseReference.CompletionListener {

    private static final String TAG = DetailsFragment.class.getSimpleName();
    private static final String EXTRA_PRODUCT = "product";
    private static final String USER_NAME = "user_name";
    private static final String LIST_KEY = "list_key";

    private ProductDetailsViewModel mViewModel;
    private FragmentDetailsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle == null || !bundle.containsKey(LIST_KEY)) {
            Log.d(TAG, "Nie otrzymano klucza listy");
            return;
        }

        String listKey = bundle.getString(LIST_KEY);

        Product product = null;

        if (bundle.containsKey(EXTRA_PRODUCT)) {
            product = bundle.getParcelable(EXTRA_PRODUCT);
        } else if (bundle.containsKey(USER_NAME)) {
            product = new Product();
            product.setId("");
            product.setUser(bundle.getString(USER_NAME));

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("en"));
            product.setDate(formatter.format(date));

        } else {
            Log.d(TAG, "Bundle nie zawieral ani produktu ani usera");
            return;
        }

        ProductDetailsViewModelFactory factory = new ProductDetailsViewModelFactory(
                FirebaseDatabase.getInstance(), listKey);
        mViewModel = new ViewModelProvider(this, factory).get(ProductDetailsViewModel.class);

        mViewModel.setProduct(product);

        setupFields();
        setupFabOnClickListener();
    }

    private void setupFields() {
        Product product = mViewModel.getProduct();
        if (product == null) return;

        mBinding.nameEditText.setText(product.getName());
        mBinding.descriptionEditText.setText(product.getMessage());
        mBinding.dateTextView.setText(product.getDate());
        mBinding.addedByTextView.setText(product.getUser());
    }

    private void setupFabOnClickListener() {
        FloatingActionButton fab = mBinding.fabUploadProduct;
        fab.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mViewModel.getLastTimeClicked() < 3000){
                return;
            }
            mViewModel.setLastTimeClicked(SystemClock.elapsedRealtime());
            Log.d(TAG, "Save button clicked");
            saveProduct();
        });
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
            getParentFragmentManager().popBackStackImmediate();
        }
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }*/
}

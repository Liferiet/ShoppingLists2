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
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.data.Product;
import com.liferiet.shoppinglists.databinding.FragmentDetailsBinding;
import com.liferiet.shoppinglists.viewmodel.DetailsViewModel;
import com.liferiet.shoppinglists.viewmodel.DetailsViewModelFactory;

/**
 * Created by liferiet on 15.11.2018.
 */

public class DetailsFragment extends Fragment implements DatabaseReference.CompletionListener {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private DetailsViewModel mViewModel;
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
        if (bundle == null) {
            Log.d(TAG, "No arguments received");
            return;
        }

        DetailsFragmentArgs args = DetailsFragmentArgs.fromBundle(bundle);
        String listKey = args.getListKey();

        Product product = args.getProduct();

        DetailsViewModelFactory factory = new DetailsViewModelFactory(
                FirebaseDatabase.getInstance(), listKey);
        mViewModel = new ViewModelProvider(this, factory).get(DetailsViewModel.class);

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
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}

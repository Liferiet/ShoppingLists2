package com.liferiet.shoppinglists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liferiet on 15.11.2018.
 */

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        FloatingActionButton fab = findViewById(R.id.fab_upload_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });
    }

    public void saveProduct() {
        // get data to save in database
        EditText productName = findViewById(R.id.name_edit_text);
        EditText productDescription = findViewById(R.id.description_edit_text);

        // save it in database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String key = db.getReference(getString(R.string.shoppingLists)).push().getKey();

        Product product = new Product();
        product.setName(productName.getText().toString());
        product.setMessage(productDescription.getText().toString());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, product.toFirebaseObject());

        db.getReference(getString(R.string.shoppingLists)).updateChildren(childUpdates, new DatabaseReference
                .CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                }
            }
        });
    }
}

package com.liferiet.shoppinglists.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.Product;

import java.util.HashMap;
import java.util.Map;

public class DetailsRepository {

    private static final String TAG = DetailsRepository.class.getSimpleName();
    private static DetailsRepository sInstance;
    private FirebaseDatabase db;
    private String listKey;


    private DetailsRepository(FirebaseDatabase db, String listKey) {
        this.db = db;
        this.listKey = listKey;
    }

    public static synchronized DetailsRepository getInstance(final FirebaseDatabase database, String listKey) {
        if (sInstance == null) {
            sInstance = new DetailsRepository(database, listKey);
        }
        sInstance.listKey = listKey;
        return sInstance;
    }

    public DatabaseReference getReference(String path) {
        return db.getReference(path);
    }

    public void saveProduct(Product product, DatabaseReference.CompletionListener completionListener) {
        String path = "/products";
        DatabaseReference reference = getReference(listKey).child(path);

        if (product.getId().isEmpty()) {
            String key = reference.push().getKey();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, product.toFirebaseObject());
            reference.updateChildren(childUpdates, completionListener);

        } else {
            reference.child(product.getId()).setValue(product.toFirebaseObject(), completionListener);
        }
    }
}

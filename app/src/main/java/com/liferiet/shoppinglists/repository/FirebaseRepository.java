package com.liferiet.shoppinglists.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liferiet.shoppinglists.data.Product;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRepository {

    private static final String TAG = FirebaseRepository.class.getSimpleName();
    private static FirebaseRepository sInstance;
    private FirebaseDatabase db;
    private String listKey;


    private FirebaseRepository(FirebaseDatabase db, String listKey) {
        this.db = db;
        this.listKey = listKey;
    }

    public static FirebaseRepository getInstance(final FirebaseDatabase database, String listKey) {
        if (sInstance == null) {
            synchronized (FirebaseRepository.class) {
                if (sInstance == null) {
                    sInstance = new FirebaseRepository(database, listKey);
                }
            }
        }
        return sInstance;
    }

    public DatabaseReference getReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }

    public void removeProductFromDatabase(Product product) {
        DatabaseReference dbRef = getReference(listKey + "/products");
        Query query = dbRef.orderByChild("name").equalTo(product.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    postsnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            reference.setValue(product.toFirebaseObject(), completionListener);
        }
    }
}

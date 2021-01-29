package com.liferiet.shoppinglists.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRepository {

    private FirebaseDatabase db;
    private String listReference;

    public FirebaseRepository(FirebaseDatabase db, String listReference) {
        this.db = db;
        this.listReference = listReference;
    }

    public DatabaseReference getReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }

    public void removeProductFromDatabase(Product product) {
        DatabaseReference dbRef = getReference(listReference);
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
        DatabaseReference reference = getReference(listReference);
        if (product.getId().isEmpty()) {
            String key = reference.push().getKey();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, product.toFirebaseObject());
            db.getReference(listReference).updateChildren(childUpdates, completionListener);

        } else {
            reference.child(product.getId()).setValue(product.toFirebaseObject(), completionListener);
        }
    }
}

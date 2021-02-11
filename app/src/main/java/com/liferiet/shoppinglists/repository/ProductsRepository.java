package com.liferiet.shoppinglists.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liferiet.shoppinglists.data.Product;

public class ProductsRepository {

    private static final String TAG = DetailsRepository.class.getSimpleName();
    private static ProductsRepository sInstance;
    private FirebaseDatabase db;
    private String listKey;


    private ProductsRepository(FirebaseDatabase db, String listKey) {
        this.db = db;
        this.listKey = listKey;
    }

    public static ProductsRepository getInstance(final FirebaseDatabase database, String listKey) {
        if (sInstance == null) {
            synchronized (DetailsRepository.class) {
                if (sInstance == null) {
                    sInstance = new ProductsRepository(database, listKey);
                }
            }
        }
        sInstance.listKey = listKey;
        return sInstance;
    }

    public DatabaseReference getReference(String path) {
        return db.getReference(path);
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

    public String getListKey() {
        return listKey;
    }

}
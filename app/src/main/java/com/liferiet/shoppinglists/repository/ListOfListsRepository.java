package com.liferiet.shoppinglists.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListOfListsRepository {
    private static ListOfListsRepository sInstance;
    private FirebaseDatabase db;
    private String mDbPath;


    private ListOfListsRepository(FirebaseDatabase db, String reference) {
        this.db = db;
        this.mDbPath = reference;
    }

    public static ListOfListsRepository getInstance(final FirebaseDatabase database, String reference) {
        if (sInstance == null) {
            synchronized (ProductDetailsRepository.class) {
                if (sInstance == null) {
                    sInstance = new ListOfListsRepository(database, reference);
                }
            }
        }
        return sInstance;
    }

    private DatabaseReference getReference() {
        return db.getReference(mDbPath);
    }

    public String createListReference(String name) {
        String key = getReference().push().getKey();

        getReference().child(key + "/name").setValue(name);
        return key;
    }

    public String getDbPath() {
        return mDbPath;
    }
}

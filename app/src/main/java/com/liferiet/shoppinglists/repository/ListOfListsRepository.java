package com.liferiet.shoppinglists.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListOfListsRepository {
    private static ListOfListsRepository sInstance;
    private FirebaseDatabase db;
    private String mReference;


    private ListOfListsRepository(FirebaseDatabase db, String reference) {
        this.db = db;
        this.mReference = reference;
    }

    public static ListOfListsRepository getInstance(final FirebaseDatabase database, String reference) {
        if (sInstance == null) {
            synchronized (FirebaseRepository.class) {
                if (sInstance == null) {
                    sInstance = new ListOfListsRepository(database, reference);
                }
            }
        }
        return sInstance;
    }

    public DatabaseReference getReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }

    public String createListReference(String name) {
        DatabaseReference dbReference = getReference(mReference);
        String key = dbReference.push().getKey();

        dbReference.child(key + "/name").setValue(name);
        return key;
    }
}

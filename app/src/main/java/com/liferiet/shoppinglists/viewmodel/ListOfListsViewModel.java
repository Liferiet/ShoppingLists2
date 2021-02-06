package com.liferiet.shoppinglists.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.database.FirebaseDatabase;
import com.liferiet.shoppinglists.data.ShoppingList;
import com.liferiet.shoppinglists.repository.ListOfListsRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ListOfListsViewModel extends AndroidViewModel {

    private static final String TAG = ListOfListsViewModel.class.getSimpleName();
    private static final String FILENAME = "lists.txt";

    private List<ShoppingList> mShoppingLists;
    private ListOfListsRepository mRepository;
    private Long mLastTimeClicked;

    public ListOfListsViewModel(FirebaseDatabase db, String dbPath, Application application) {
        super(application);
        // TODO

        Log.d(TAG, "Preparing listOfLists viewModel");
        //mLists = new MutableLiveData<>();

        mShoppingLists = readListsFromFile();
        Log.d(TAG, "Lists: " + mShoppingLists.toString());
        mRepository = ListOfListsRepository.getInstance(db, dbPath);
        mLastTimeClicked = 0L;
    }

    public void createNewList(String name) {
        String key = mRepository.createListReference(name);
        ShoppingList list = new ShoppingList();
        list.setKey(key);
        list.setName(name);

        if (writeListToFile(list)){
            mShoppingLists.add(list);
        }
    }

    public List<ShoppingList> getLists() {
        return mShoppingLists;
    }

    public String getDbPath() {
        return mRepository.getDbPath();
    }

    public Long getLastTimeClicked() {
        return mLastTimeClicked;
    }

    public void setLastTimeClicked(Long lastTimeClicked) {
        this.mLastTimeClicked = lastTimeClicked;
    }

    private boolean writeListToFile(ShoppingList list) {
        try {
            FileOutputStream fileOutputStream = getApplication().openFileOutput(FILENAME, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            String data = list.getKey() + ":" + list.getName() + "\n";

            outputStreamWriter.write(data);
            outputStreamWriter.close();
            fileOutputStream.close();
            return true;
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }

    private List<ShoppingList> readListsFromFile() {
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        try {
            InputStream inputStream = getApplication().openFileInput(FILENAME);
            Log.d(TAG, "Reading lists from file");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                String[] oneListArray;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                     oneListArray = receiveString.split(":");
                     ShoppingList list = new ShoppingList();
                     list.setKey(oneListArray[0].trim());
                     list.setName(oneListArray[1].trim());
                     shoppingLists.add(list);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return shoppingLists;
    }
}

package com.liferiet.shoppinglists.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by liferiet on 15.11.2018.
 */

public class Product implements Serializable, Parcelable {

    private String id;
    private String name;
    private String message;
    private String date;
    private String user;

    public Product() {
    }

    public Product(String name, String message, String date, String user) {
        this.name = name;
        this.message = message;
        this.date = date;
        this.user = user;
    }

    public Product(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public HashMap<String, String> toFirebaseObject() {
        HashMap<String, String> product = new HashMap<>();
        product.put("name", name);
        product.put("message", message);
        product.put("date", date);
        product.put("user", user);
        return product;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Product(Parcel in) {
        setId(in.readString());
        setName(in.readString());
        setMessage(in.readString());
        setDate(in.readString());
        setUser(in.readString());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getId());
        parcel.writeString(getName());
        parcel.writeString(getMessage());
        parcel.writeString(getDate());
        parcel.writeString(getUser());
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}

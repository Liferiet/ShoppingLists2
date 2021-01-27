package com.liferiet.shoppinglists.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by liferiet on 15.11.2018.
 */

public class Product implements Serializable {

    private String name;
    private String message;
    private String date;
    private String user;

    public Product() {
    }

    public Product(String name) {
        this.name = name;
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
}

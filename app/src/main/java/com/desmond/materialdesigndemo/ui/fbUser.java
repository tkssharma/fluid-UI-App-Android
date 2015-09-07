package com.desmond.materialdesigndemo.ui;

import android.graphics.Bitmap;

/**
 * Created by tsharma3 on 9/5/2015.
 */
public class fbUser {

    public static String email;
    public static String name;
    public static String id;
    public static String imageUri;
    public static Bitmap bitmap;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setBitmap(Bitmap bitmap) {
        fbUser.bitmap = bitmap;
    }
}

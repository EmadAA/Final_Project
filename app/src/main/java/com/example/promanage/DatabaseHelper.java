package com.example.promanage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Test_DB";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_REGISTER = "register";
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_PRODUCT_NAME = "productName";
    public static final String COL_PRODUCT_PRICE = "productPrice";
    public static final String COL_PRODUCT_QUANTITY = "productQuantity";
    public static final String COL_PRODUCT_IMAGE_URI = "productImageUri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_MOBILE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_PRODUCT_QUANTITY + " INTEGER, " +
                COL_PRODUCT_IMAGE_URI + " BLOB)");
    }


    public boolean insertUser(String username, String email, String password, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_MOBILE, mobile);

        long result =  db.insert(TABLE_REGISTER, null, contentValues);
        //result value, if inserted, then "row number"
        //result value, if not inserted, then -1

        return result != -1;
    }

    public boolean checkUserByUsername(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REGISTER + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }



    public void insertProduct(String name, int quantity, byte[] imageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_QUANTITY, quantity);
        values.put(COL_PRODUCT_IMAGE_URI, imageByteArray);
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COL_PRODUCT_NAME));
                    int quantity = cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_QUANTITY));
                    byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex(COL_PRODUCT_IMAGE_URI));

                    Product product = new Product(id, name, quantity, imageByteArray);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return productList;
    }


    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public void updateProductNameAndQuantity(int productId, String newName, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, newName);
        values.put(COL_PRODUCT_QUANTITY, newQuantity);

        db.update(TABLE_PRODUCTS, values, COL_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }


}
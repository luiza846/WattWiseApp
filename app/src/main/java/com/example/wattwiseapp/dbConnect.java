package com.example.wattwiseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbConnect extends SQLiteOpenHelper {

    //db'name
    private static String dbName = "findFriendsManager";
    private static String dbTable = "users";
    private static int dbVersion = 1;

    private static  String ID = "id";
    private static  String fullname = "fullname";
    private static  String emailAddress = "emailAddress";
    private static  String password = "password";
    private static  String phoneNumber = "phoneNumber";
    private static  String dob = "dob";
    private static  String bio = "bio";


    public dbConnect(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query =
                "CREATE TABLE " + dbTable + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        fullname + " TEXT, " +
                        emailAddress + " TEXT, " +
                        password + " TEXT, " +
                        dob + " TEXT, " +
                        phoneNumber + " TEXT, " +
                        bio + " TEXT" +
                        ")";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + dbTable);
        onCreate(db);

    }

    public void addUser(Users user){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(fullname,user.getFullname());
        values.put(emailAddress,user.getEmailAddress());
        values.put(password,user.getPassword());
        values.put(phoneNumber,user.getPhoneNumber());
        values.put(dob,user.getDOB());
        values.put(bio,user.getBio());

        db.insert(dbTable, null, values);

    }

    public boolean checkLogin(String emailAddress, String password){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE emailAddress = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[] {emailAddress, password});

        boolean loginSuccess = cursor.moveToFirst();

        cursor.close();
        db.close();

        return loginSuccess;

    }

}

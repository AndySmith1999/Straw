package com.example.straw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 
public class DatabaseUtil extends SQLiteOpenHelper {
 
    private static final String DATABASE_NAME = "PhoneBook.db";  //The name of the database
    private static final int DATABASE_VERSION = 1;               //The version number of the database
 
    public DatabaseUtil(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
 
    /**
     * create a database
     * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }
 
    /**
     * build a database
     * */
    private void createTable(SQLiteDatabase db){
        db.execSQL("create table UserInfo(" +
                "id integer primary key autoincrement," +
                "userName text," +
                "userPhone text)");
    }
 
    /**
     * update the database
     * */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
 
    }
}
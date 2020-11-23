package com.example.straw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
 
import com.example.straw.User;
 
import java.util.ArrayList;
import java.util.List;

/**
 * This class is designed for the controlling of the database
 */
public class UtilDao {
    private DatabaseUtil du;
    private SQLiteDatabase db;

    /**
     * Instantiates a new Util dao.
     *
     * @param context the context
     */
    public UtilDao(Context context){
        du = new DatabaseUtil(context);
        db = du.getWritableDatabase();
    }


    /**
     * add some data
     *
     * @param tableName the table name
     * @param key       the key
     * @param values    the values
     */
    public void addData(String tableName,String[] key,String[] values){
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < key.length; i ++){
            contentValues.put(key[i],values[i]);
        }
        db.insert(tableName,null,contentValues);
        contentValues.clear();
    }

    /**
     * delete data
     *
     * @param where  the where
     * @param values the values
     * @return the int
     */
    public int delData(String where,String[] values){
        int del_data;
        del_data = db.delete("UserInfo",where,values);
        return del_data;
    }

    /**
     * edit data
     *
     * @param values the values
     */
    public void update(String[] values){
        db.execSQL("update UserInfo set userName=?,userPhone=? where userName=? ",values);
    }

    /**
     * query data
     *
     * @return the list
     */
    public List<User> inquireData(){
        List<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select userName,userPhone" +
                " from UserInfo",null);
            while(cursor.moveToNext()){
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
 
                User user = new User();
                user.setName(name);
                user.setPhone(phone);
 
                list.add(user);
            }
 
        return list;
    }

    /**
     * close the connection to the database
     */
    public void getClose(){
        if(db != null){
            db.close();
        }
    }
}
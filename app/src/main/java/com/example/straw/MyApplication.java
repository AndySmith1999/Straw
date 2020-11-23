package com.example.straw;

import android.app.Application;
 
import com.example.straw.UtilDao;

public class MyApplication extends Application {
    private UtilDao dao;
 
    /**
     * Call this when it is created
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        dao = new UtilDao(this);
    }
 
    /**
     * This method is called to free memory when the background process terminates and the foreground program needs memory
     * Used to close a database connection
     * */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dao.getClose();
    }
 
    public UtilDao getDao() {
        return dao;
    }
}
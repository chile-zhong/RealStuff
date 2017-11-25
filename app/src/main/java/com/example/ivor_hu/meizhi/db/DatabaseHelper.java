package com.example.ivor_hu.meizhi.db;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by ivor on 2017/11/25.
 */

public class DatabaseHelper {
    private static final String NAME = "real_stuff_db";
    private static DatabaseHelper sInstance;
    private AppDatabase mDatabase;

    private DatabaseHelper(Context context) {
        mDatabase = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, NAME).build();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (DatabaseHelper.class) {
            if (sInstance == null) {
                sInstance = new DatabaseHelper(context);
            }
        }
        return sInstance;
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }
}

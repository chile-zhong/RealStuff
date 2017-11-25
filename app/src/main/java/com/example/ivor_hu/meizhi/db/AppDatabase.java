package com.example.ivor_hu.meizhi.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.ivor_hu.meizhi.db.converter.DateConverter;
import com.example.ivor_hu.meizhi.db.dao.CollectionDao;
import com.example.ivor_hu.meizhi.db.entity.Stuff;

/**
 * Created by ivor on 2017/11/25.
 */

@Database(entities = {Stuff.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CollectionDao collectionDao();
}

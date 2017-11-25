package com.example.ivor_hu.meizhi.db.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by ivor on 2017/11/25.
 */

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}

package com.demo.alarm.Database;


import android.content.Context;

import androidx.room.Room;

public class InstanceDatabase {
    public static BaseRoomDatabase baseRoomDatabase;

    public static BaseRoomDatabase getInstance(Context context) {
        if (baseRoomDatabase == null) {
            baseRoomDatabase = Room.databaseBuilder(context, BaseRoomDatabase.class, "alarm_database.db").allowMainThreadQueries().build();
        }
        return baseRoomDatabase;
    }
}

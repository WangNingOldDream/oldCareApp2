package com.demo.alarm.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.demo.alarm.Dao.AlarmDao;
import com.demo.alarm.Entity.Alarm;

@Database(entities = { Alarm.class}, version = 1, exportSchema = false)
public abstract class BaseRoomDatabase extends RoomDatabase {
    public abstract AlarmDao getAlarmDao();
}

package com.taimoor.weather101.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Locations.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"WeatherLocations")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}

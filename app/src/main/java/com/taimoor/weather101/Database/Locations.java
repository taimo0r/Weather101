package com.taimoor.weather101.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Locations {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "location_name")
    public String LocationName;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;


}

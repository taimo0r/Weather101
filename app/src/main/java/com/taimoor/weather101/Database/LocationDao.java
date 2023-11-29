package com.taimoor.weather101.Database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Query(" Select * From Locations")
    List<Locations> getAllLocations();

    @Insert
    void saveLocation(Locations... locations);

    @Delete
    void delete(Locations locations);

}

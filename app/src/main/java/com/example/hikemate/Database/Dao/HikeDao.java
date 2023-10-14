package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Hike;

import java.util.List;

@Dao
public interface HikeDao {
    @Insert
    long insert(Hike hike);

    @Update
    void update(Hike hike);

    @Query("SELECT * FROM hike")
    List<Hike> getAllHikes();

    @Delete
    void deleteHike(Hike hike);

    @Query("SELECT * FROM hike WHERE hikeName LIKE :searchQuery")
    List<Hike> searchHikesByName(String searchQuery);
}

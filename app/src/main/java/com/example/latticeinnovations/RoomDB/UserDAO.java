package com.example.latticeinnovations.RoomDB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void saveUser(UserEntity userEntity);

    @Query("SELECT * FROM UserEntity")
    public List<UserEntity> getAllUser();
}

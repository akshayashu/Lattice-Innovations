package com.example.latticeinnovations.RoomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class}, version = 1)
public abstract class UserDB extends RoomDatabase {

    public abstract UserDAO userDAO();
}

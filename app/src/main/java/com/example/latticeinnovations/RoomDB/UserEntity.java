package com.example.latticeinnovations.RoomDB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "EMAIL")
    public String email ;

    @ColumnInfo(name = "NAME")
    public String name ;

    @ColumnInfo(name = "PASSWORD")
    public String password ;

    @ColumnInfo(name = "PHONE")
    public String phoneNo;

    @ColumnInfo(name = "ADDRESS")
    public String address;

    public UserEntity(@NonNull String email, String name, String password, String phoneNo, String address) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNo = phoneNo;
        this.address = address;
    }
}

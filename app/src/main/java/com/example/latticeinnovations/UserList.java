package com.example.latticeinnovations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.latticeinnovations.RoomDB.UserDB;
import com.example.latticeinnovations.RoomDB.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final UserDB db = Room.databaseBuilder(getApplicationContext(), UserDB.class, "userDB").build();

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<UserEntity> list = db.userDAO().getAllUser();
                if (list.isEmpty()){
                    Log.d("USERLIST - ", "EMPTY");
                }else {
                    userAdapter = new UserAdapter(getApplicationContext(), list);
                    recyclerView.setAdapter(userAdapter);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
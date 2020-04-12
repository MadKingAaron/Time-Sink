package com.example.projecttimesink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity
{
    TimeText timeText;

    Database database;
    private RecyclerView recyclerView;

    SwitchButton switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        this.database = new Database();

        this.switchButton = new SwitchButton(this, MainActivity.class, (Button) findViewById(R.id.restartButton));

//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();
//
//        if(bundle != null)
//        {
//            long time = (long) bundle.getSerializable("time");
//
//            this.timeText = new TimeText((TextView) findViewById(R.id.time), time, 1);
//
//            String name = (String) bundle.getSerializable("username");
//
//            TextView nameText = (TextView) findViewById(R.id.username);
//
//            nameText.setText(name);
//        }



//        this.database.readUser("k1");


        this.recyclerView = findViewById(R.id.recyclerView);
        this.database.readUsers(new Database.DataStatus()
        {
            @Override
            public void DataIsLoaded(ArrayList<User> users, ArrayList<String> keys)
            {
                new RecyclerView_Config().setConfig(recyclerView, LeaderboardActivity.this, users, keys);
            }

            @Override
            public void DataIsInserted()
            {

            }

            @Override
            public void DataIsUpdated()
            {

            }

            @Override
            public void DataIsDeleted()
            {

            }
        });
    }
}

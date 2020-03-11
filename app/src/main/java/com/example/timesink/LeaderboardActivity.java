package com.example.timesink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.Time;

public class LeaderboardActivity extends AppCompatActivity
{
    TimeText timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        long time = (long) bundle.getSerializable("time");

        this.timeText = new TimeText((TextView) findViewById(R.id.time), time, 1);
    }
}

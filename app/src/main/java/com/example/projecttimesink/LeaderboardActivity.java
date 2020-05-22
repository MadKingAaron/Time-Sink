package com.example.projecttimesink;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity
{
    TimeText timeText;

    Database database;
    private RecyclerView recyclerView;

    SwitchButton switchButton;

    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        this.database = new Database();

        this.switchButton = new SwitchButton(this, MainActivity.class, (Button) findViewById(R.id.restartButton));

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        this.firebaseAuth = FirebaseAuth.getInstance();

        if(this.firebaseAuth.getCurrentUser() != null)
            this.userID = firebaseAuth.getCurrentUser().getUid();
        else
            this.userID = null;

        this.recyclerView = findViewById(R.id.recyclerView);
        this.database.readLeaderboardIDs(new Database.UserIDDataStatus()
        {
            @Override
            public void DataIsLoaded(ArrayList<String> userIDs, ArrayList<String> keys)
            {
                database.readUsers(userIDs, new Database.UserDataStatus()
                {
                    @Override
//                    public void DataIsLoaded(ArrayList<User> users, ArrayList<String> keys)
                    public void DataIsLoaded(User[] users, String[] keys)
                    {
                        progressBar.setVisibility(View.GONE);
                        new RecyclerView_Config().setConfig(recyclerView, LeaderboardActivity.this, users, keys, userID);
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

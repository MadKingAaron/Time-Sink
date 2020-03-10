package com.example.timesink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000

    // ADD ALL OBJECTS HERE
    private void create()
    {
        // TIMER AND BUTTON
        SwitchTimer timer = new SwitchTimer(this, LeaderboardActivity.class,
                (TextView) findViewById(R.id.timer),
                (ImageView) findViewById(R.id.buttonImage),
                (Button) findViewById(R.id.theButton));

        this.actionableList.add(timer);
    }

    /*                                                      *\
        SHOULDN'T NEED TO CHANGE ANYTHING BELOW THIS POINT
    \*                                                      */

    final Handler handler = new Handler();
    final int delay = getDelay(); // Delay in milliseconds
    ActionableList actionableList;
    Actionable[] actionableObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        this.actionableList = new ActionableList();

        create();

        this.actionableObjects = this.actionableList.toArray();
    }

    // Runs once per frame when activity is active
    private void update()
    {
        for (int i = 0; i < this.actionableObjects.length; i++)
        {
            if (this.actionableObjects[i] != null)
                this.actionableObjects[i].update();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                update();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    // Runs upon pausing of activity
    private void pause()
    {
        for (int i = 0; i < this.actionableObjects.length; i++)
        {
            if (this.actionableObjects[i] != null)
                this.actionableObjects[i].pause();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        pause();
    }

    private int getDelay()
    {
        float fps = (this.FRAMES_PER_SECOND > 1000) ? 1000 : this.FRAMES_PER_SECOND;

        if (fps <= 0)
            fps = Float.MIN_NORMAL;

        int delay = (int) (1000 / fps);

        return delay;
    }
}

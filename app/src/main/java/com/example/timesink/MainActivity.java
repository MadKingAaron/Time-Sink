package com.example.timesink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000

    // ADD ALL OBJECTS HERE

    private void create()
    {
        // TIMER AND BUTTON
        this.actionableList.add(new Timer(
                (TextView) findViewById(R.id.timer),
                (ImageView) findViewById(R.id.buttonImage),
                (Button) findViewById(R.id.theButton)));
    }

    /*                                          *\
        DON'T CHANGE ANYTHING BELOW THIS POINT
    \*                                          */

    final Handler handler = new Handler();
    final int delay = 10;//getDelay(); // Delay in milliseconds
    ActionableList actionableList;
    Actionable[] actionableObjects;
    SensorManager sensorManager;
    Gyro gyroSensor=new Gyro();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        this.actionableList = new ActionableList();

        create();

        this.actionableObjects = this.actionableList.toArray();

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor.setSensorManager();
        sensorManager.registerListener(MainActivity.this,gyroSensor.gyroscope,SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        gyroSensor.getXAxis(event);
        gyroSensor.getYAxis(event);
        gyroSensor.getZAxis(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    // Runs once per frame when activity is active
    private void update()
    {
        for (int i = 0; i < this.actionableObjects.length; i++)
        {
            if(this.actionableObjects[i] != null)
                this.actionableObjects[i].update();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        handler.postDelayed(new Runnable() {
            public void run() {
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
            if(this.actionableObjects[i] != null)
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

        if(fps <= 0)
            fps = Float.MIN_NORMAL;

        int delay = (int) (1000 / fps);

        return delay;
    }
}

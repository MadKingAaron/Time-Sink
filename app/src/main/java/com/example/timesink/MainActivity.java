package com.example.timesink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    private static final String TAG = "MainActivity";//Use for logs involving MainActivity
    final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000
    final Handler handler = new Handler();
    final int delay = getDelay(); // Delay in milliseconds
    ActionableList actionableList;
    Actionable[] actionableObjects;

    SensorManager sensorManager;
    private Sensor accelerometer;

    private float[] axisValues;
    private float accelValue;
    private float prevAccelValue;
    private float currAccelValue;

    private TextView antiCheatText;

    // ADD ALL OBJECTS HERE

    private void create()
    {
        // TIMER AND BUTTON
        this.actionableList.add(new Timer(
                (TextView) findViewById(R.id.timer),
                (ImageView) findViewById(R.id.buttonImage),
                (Button) findViewById(R.id.theButton)));
    }

    private void createSensorManager() //creates sensor manager for gyro
    {
        //Sensor stuff
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelValue=0.00f;
        prevAccelValue = SensorManager.GRAVITY_EARTH;
        currAccelValue = SensorManager.GRAVITY_EARTH;
        antiCheatText=findViewById(R.id.antiCheatText);

    }

    /*                                          *\
        DON'T CHANGE ANYTHING BELOW THIS POINT
    \*                                          */



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        this.actionableList = new ActionableList();

        create();
        createSensorManager();
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        this.actionableObjects = this.actionableList.toArray();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            axisValues = event.values.clone();
            float xAxis = axisValues[0];
            float yAxis = axisValues[1];
            float zAxis = axisValues[2];
            prevAccelValue = currAccelValue;
            currAccelValue = (float)Math.sqrt((xAxis * xAxis) + (yAxis * yAxis) + (zAxis * zAxis));
            accelValue = accelValue * 0.9f + (currAccelValue - prevAccelValue);
            String TAG = "onSensorChanged";
            Log.d(TAG, "accelValue: " + accelValue);
            if(accelValue > 0.001 || accelValue < -0.001)
            {
                this.antiCheatText.setText("");
            }
            else
            {
                this.antiCheatText.setText("Are you still there?");
            }
        }
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

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(MainActivity.this,gyroSensor.gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
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
        sensorManager.unregisterListener(this);
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

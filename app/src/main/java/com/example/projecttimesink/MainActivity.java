package com.example.projecttimesink;

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
    private final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000
    private final Handler handler = new Handler();
    private final int delay = getDelay(); // Delay in milliseconds
    private ActionableList actionableList;
    private Actionable[] actionableObjects;

    SwitchTimer timer;

    //Sensor stuff
    private SensorManager sensorManager;
    private Sensor accelerometer;

    //Movement calculation values
    private float accelValue;
    private float prevAccelValue;
    private float currAccelValue;

    //Message displayed when movement isn't detected
    private TextView antiCheatText;

    //Counts number of iterations the phone is lying still
    private int waitTime;

    SarcasticComment comment;
    private TextView sarcasticCommentText;
    long currentTime;

    private void create()
    {
        // TIMER AND BUTTON
        /*SwitchTimer*/ this.timer = new SwitchTimer(this, LeaderboardPopUp.class,
                (TextView) findViewById(R.id.timer),
                (ImageView) findViewById(R.id.buttonImage),
                (Button) findViewById(R.id.theButton));

        this.actionableList.add(timer);
    }

    private void createSensorManager() //creates sensor manager
    {
        //Sensor stuff
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelValue = 0.00f;
        prevAccelValue = SensorManager.GRAVITY_EARTH;
        currAccelValue = SensorManager.GRAVITY_EARTH;
        antiCheatText = findViewById(R.id.antiCheatText);
    }

    private void createSarcasticComment()
    {
        this.comment = new SarcasticComment();
        this.sarcasticCommentText = findViewById(R.id.sarcasticCommentText);
        this.antiCheatText.setText("");
    }

    /*                                                      *\
        SHOULDN'T NEED TO CHANGE ANYTHING BELOW THIS POINT
    \*                                                      */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        this.actionableList = new ActionableList();

        create();
        createSensorManager();
        createSarcasticComment();
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        this.actionableObjects = this.actionableList.toArray();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float[] axisValues = event.values.clone();
            prevAccelValue = currAccelValue;
            Accelerometer accelMoveCheck = new Accelerometer(axisValues[0], axisValues[1], axisValues[2], prevAccelValue, currAccelValue, accelValue);
            accelValue = accelMoveCheck.calculateAccelValue();
            if (accelValue > 0.5)
            {
                this.waitTime = 0;
                this.antiCheatText.setText("");
            } else
            {
                this.waitTime++;
                if (waitTime > 100)
                {
                    this.antiCheatText.setText("Hold your device in your hand.");
                    Log.d("false", "accelValue: " + accelValue);
                }
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
            if (this.actionableObjects[i] != null)
                this.actionableObjects[i].update();
        }
        Log.d("currentTime","String of current time is: " + timer.getTotalTime());
        //6373 = 6s373 ms
        currentTime=timer.getTotalTime();
        comment.determineSarcasticComment(currentTime);
        String currentSarcasticComment=comment.sarcasticComment;
        this.sarcasticCommentText.setText(currentSarcasticComment);
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

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
        sensorManager.unregisterListener(this);
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

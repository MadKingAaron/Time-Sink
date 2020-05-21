package com.example.projecttimesink;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener
{
    private final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000
    private final Handler handler = new Handler();
    private final int delay = getDelay(); // Delay in milliseconds
    private ActionableList actionableList;
    private Actionable[] actionableObjects;

    SwitchTimer timer;
    boolean timerStarted;
    boolean timerStopped;

    //Sensor variables
    private SensorManager sensorManager;
    private Sensor accelerometer;

    //Movement calculation values
    private float accelValue;
    private float prevAccelValue;
    private float currAccelValue;

    //Message displayed when cheating is detected
    private TextView antiCheatText;

    //Counts number of iterations the phone is lying still
    private long waitTime;

    //Sarcastic comment variables
    SarcasticComment comment;
    private TextView sarcasticCommentText;
    long currentTime;

    ///Bluetooth////////////////////////////////////////////////////
    //TAG for logs
    private final String TAG = "MainActivity";


    private BluetoothAdapter mBluetoothAdapter;

    Button sendButton;


    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //CLick sounds
    private MediaPlayer clickMP;


    private void createBluetooth()
    {

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TODO: Use StringBluetoothPackage for debug and BluetoothIntegerPackage for emotes
        //this.bluetoothPackage = new StringBluetoothPackage();
        BluetoothSharedMemory.bluetoothPackage = new BluetoothIntegerPackage();


        BluetoothSharedMemory.bluetoothIsConnected = false;



    }

    ImageView settingsIcon;
    Button settingsButton;
    SwitchButton settingsSwitchButton;

    FirebaseAuth firebaseAuth;

    private void create()
    {
        // TIMER AND BUTTON
        this.timer = new SwitchTimer(this, Register.class,
                (TextView) findViewById(R.id.timer),
                (ImageView) findViewById(R.id.buttonImage),
                (Button) findViewById(R.id.theButton));

        this.actionableList.add(timer);

        this.settingsIcon = (ImageView) findViewById(R.id.settingsIcon);
        this.settingsButton = (Button) findViewById(R.id.settingsButton);

        this.firebaseAuth = FirebaseAuth.getInstance();

        if(this.firebaseAuth.getCurrentUser() == null)
        {
            this.settingsSwitchButton = new SwitchButton(this, Register.class, this.settingsButton);
        }
        else
        {
            this.settingsSwitchButton = new SwitchButton(this, Settings.class, this.settingsButton);
        }

        this.timerStarted = false;
        this.timerStopped = false;
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


        createBluetooth();
        configureAchievementButton();

        emoteSelection = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,emoteOptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emoteSelection.setAdapter(adapter);
        emoteSelection.setOnItemSelectedListener(this);
    }

    public void onItemSelected (AdapterView<?> parent, View v, int position, long id){

        switch (position) {
            case 0:
                Toast.makeText(MainActivity.this, "Dab on them fools", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, ":)", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this, "Taunt shown", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Good Luck!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void configureAchievementButton()
    {
        Button achievementButton=(Button) findViewById(R.id.achievementButton);
        achievementButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, AchievementActivity.class));
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && this.timer.isRunning())
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
                if (this.timerStarted && this.waitTime > 300)
                {
                    this.antiCheatText.setText("Hold your device in your hand.");
                    Log.d("false", "accelValue: " + accelValue);
                }
                if(waitTime > 500)
                {
                    this.waitTime=0;
                    this.antiCheatText.setText("");
                    timer.stopTimer();
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
//        Log.d("currentTime","String of current time is: " + timer.getTotalTime());
        //6373 = 6s373 ms
        this.currentTime = this.timer.getTotalTime();
        this.comment.determineSarcasticComment(this.currentTime);
        String currentSarcasticComment = this.comment.sarcasticComment;
        this.sarcasticCommentText.setText(currentSarcasticComment);

        if(!this.timerStarted && this.timer.isRunning())
        {
            this.timerStarted = true;
            this.timerStopped = false;

            // Anything that is run once upon timer starting

            this.settingsIcon.setVisibility(View.INVISIBLE);
            this.settingsButton.setEnabled(false);
        }

        if(!this.timerStopped && this.timerStarted && !this.timer.isRunning())
        {
            this.timerStopped = true;
            this.timerStarted = false;

            // Anything that is run once upon timer stopping

            this.settingsIcon.setVisibility(View.VISIBLE);
            this.settingsButton.setEnabled(true);
        }


        updateBluetoothMessage();
    }

    private void updateBluetoothMessage()
    {

        if(BluetoothSharedMemory.bluetoothPackage.checkIfDataUpdatedSinceLastCall())
        {
            //TODO: Debug
            //Toast.makeText(MainActivity.this, "Message: "+BluetoothSharedMemory.bluetoothPackage.getData(), Toast.LENGTH_SHORT).show();


            //TODO: Here is where to write code to display new emote
            displayEmote((Integer) BluetoothSharedMemory.bluetoothPackage.getData());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.handler.postDelayed(new Runnable()
        {
            public void run()
            {
                update();
                handler.postDelayed(this, delay);
            }
        }, delay);

        this.sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
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


    ////////////////////Bluetooth Methods///////////////////////////////////////////////


    //TODO Use when emotes are implemented to send via bluetooth
    public void sendEmoteViaBluetooth(int emote) throws EmoteNotSentException{

       if(BluetoothSharedMemory.bluetoothIsConnected)
       {
           try {
               //Convert to byte array
               String stringToSend = ""+emote;
               BluetoothSharedMemory.mBluetoothConnection.write(stringToSend.getBytes(), MainActivity.this);
           }catch (Exception e)
           {
               throw new EmoteNotSentException("Emote Unable to send ---- "+e.getMessage());
           }
       }
       else
       {
           Toast.makeText(MainActivity.this, "Bluetooth Devices Not Connected", Toast.LENGTH_SHORT);
       }
    }


    public void displayEmote(int emoteNumber)
    {

    }

    //TODO: Debug method for testing bluetooth emote message passing
    public void debugSendEmote()  {
        try {
            sendEmoteViaBluetooth(1);
        } catch (EmoteNotSentException e) {
            Log.d(TAG, "Failed to send emote "+e.getMessage());
        }
    }
}

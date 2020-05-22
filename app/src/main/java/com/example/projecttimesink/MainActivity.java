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
import com.google.firebase.database.DataSnapshot;

public class MainActivity extends AppCompatActivity implements SensorEventListener
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

    Database database;
    String userID;
    long longestTimeWasted;
    User user;

    private BluetoothAdapter mBluetoothAdapter;

    Button sendButton;

    private AchievementMessageVerification messageVerification;


    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //CLick sounds
    private MediaPlayer clickMP;

    private Spinner emoteSelection;
    private static final String[] emoteOptions = {"Dab", "Smile", "Taunt", "Good Luck"};


    private Button emoteButton;

    private ImageView emoteDisplayer;

    private boolean emoteTimerIsRunning = false;
    private final int MAX_EMOTE_FRAME_TIMER = 150;
    private int currentEmoteFrameTimer = 0;

    private TextView achievementUnlockText;

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
    ImageView achievementIcon;
    Button achievementButton;
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
        this.achievementIcon=(ImageView) findViewById(R.id.achievementIcon);
        this.achievementButton=(Button) findViewById(R.id.achievementButton);

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


    public void createEmoteSelector()
    {


        this.emoteButton = (Button) findViewById(R.id.emoteButton);

        this.emoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayEmoteSelector();
            }
        });
    }

    public void createEmoteDisplayer()
    {
        this.emoteDisplayer = (ImageView) findViewById(R.id.emoteDisplayedIcon);

    }

    public void displayEmoteSelector()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Emote To Send:");


        builder.setItems(emoteOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectEmoteAndSend(which);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void readUser()
    {
        if(this.userID==null)
        {
            return;
        }
        this.database.readUser(this.userID, new Database.OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                longestTimeWasted=user.longestTimeWasted;

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
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

        messageVerification=new AchievementMessageVerification();

        createBluetooth();
        configureAchievementButton();

        createEmoteSelector();
        createEmoteDisplayer();
        this.database=new Database();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            this.userID=firebaseAuth.getCurrentUser().getUid();
        }

        readUser();
        this.achievementUnlockText=(TextView)findViewById(R.id.achievementUnlockText);
    }



    public void selectEmoteAndSend(int position)
    {
        try {
            sendEmoteViaBluetooth(position);
        } catch (EmoteNotSentException e) {
            Log.d(TAG, "Main Activity - OnItemSelected: "+e.getMessage());
        }

        logEmoteSelected(position);
        displayEmote(position);
    }



    private void configureAchievementButton()
    {
        Button achievementButton=(Button) findViewById(R.id.achievementButton);
        if(firebaseAuth.getCurrentUser()!=null)
        {
            achievementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    startActivity(new Intent(MainActivity.this, AchievementActivity.class));
                }
            });
        }
        else
        {
            achievementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"Please log in/register in settings to view achievements",Toast.LENGTH_SHORT).show();
                }
            });
        }

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
            this.achievementButton.setEnabled(false);
            this.achievementIcon.setVisibility(View.INVISIBLE);
        }

        if(!this.timerStopped && this.timerStarted && !this.timer.isRunning())
        {
            this.timerStopped = true;
            this.timerStarted = false;

            // Anything that is run once upon timer stopping

            this.settingsIcon.setVisibility(View.VISIBLE);
            this.settingsButton.setEnabled(true);
            this.achievementButton.setEnabled(true);
            this.achievementIcon.setVisibility(View.VISIBLE);
        }


        updateBluetoothMessage();

        this.checkEmoteTimer();
        updateAchievementMessage();
    }

    private void updateAchievementMessage()
    {
        this.messageVerification.currentTime=this.currentTime;
        this.messageVerification.longestTime=this.longestTimeWasted;
        int numberOfAchievementsUnlocked=this.messageVerification.getNumberOfTimeBasedAchievementsUnlocked();
        if(numberOfAchievementsUnlocked==1&&this.currentTime<4000)
        {
            //Toast.makeText(MainActivity.this,"Achievement 1 unlocked",Toast.LENGTH_SHORT).show();
            this.achievementUnlockText.setText("Achievement 1 unlocked");
        }
        else if(numberOfAchievementsUnlocked==2&&this.currentTime<64000)
        {
            this.achievementUnlockText.setText("Achievement 2 unlocked");
        }
        else if(numberOfAchievementsUnlocked==3&&this.currentTime<(60000*5)+4000)
        {
            this.achievementUnlockText.setText("Achievement 3 unlocked");
        }
        else if(numberOfAchievementsUnlocked==4&&this.currentTime<(60000*10)+4000)
        {
            this.achievementUnlockText.setText("Achievement 4 unlocked");
        }
        else if(numberOfAchievementsUnlocked==5&&this.currentTime<(60000*30)+4000)
        {
            this.achievementUnlockText.setText("Achievement 5 unlocked");
        }
        else if(numberOfAchievementsUnlocked==6&&this.currentTime<(60000*60)+4000)
        {
            this.achievementUnlockText.setText("Achievement 6 unlocked");
        }
        else if(numberOfAchievementsUnlocked==7&&this.currentTime<((60000*60)*12)+4000)
        {
            this.achievementUnlockText.setText("Achievement 7 unlocked");
        }
        else if(numberOfAchievementsUnlocked==8&&this.currentTime<((60000*60)*24)+4000)
        {
            this.achievementUnlockText.setText("Achievement 8 unlocked");
        }
        else
        {
            this.achievementUnlockText.setText("");
        }

    }

    private void checkEmoteTimer()
    {
        if(this.emoteTimerIsRunning)
        {
            if(this.currentEmoteFrameTimer >= this.MAX_EMOTE_FRAME_TIMER)
            {
                this.setEmoteImageToBlank();

                this.emoteTimerIsRunning = false;

                this.currentEmoteFrameTimer = 0;
            }
            else
            {
                this.currentEmoteFrameTimer++;
            }
        }
    }

    private void setEmoteImageToBlank()
    {
        this.emoteDisplayer.setVisibility(View.INVISIBLE);
    }

    private void updateBluetoothMessage()
    {

        if(BluetoothSharedMemory.bluetoothPackage.checkIfDataUpdatedSinceLastCall())
        
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

                displayEmoteSent(emote);
            }catch (Exception e)
            {
                throw new EmoteNotSentException("Emote Unable to send ---- "+e.getMessage());
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Bluetooth Devices Not Connected", Toast.LENGTH_SHORT);

            throw new EmoteNotSentException("Bluetooth Devices Not Connected");
        }
    }


    public void displayEmote(int emoteNumber)
    {
        this.emoteTimerIsRunning = true;
        switch (emoteNumber) {
            case 0:
                dab();
                break;
            case 1:
                smile();
                break;
            case 2:
                taunt();
                break;
            case 3:
                goodLuck();
                break;
        }
    }

    private void dab()
    {
//        Toast.makeText(MainActivity.this, "Dab on them fools", Toast.LENGTH_SHORT).show();

        double rand = Math.random();

        if(rand <= 0.15)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.sans_foreground);
        }
        else if(rand <= 0.3)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.squidward_foreground);
        }
        else if(rand <= 0.45)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.roblox_foreground);
        }
        else if(rand <= 0.60)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.luigi_foreground);
        }
        else if(rand <= 0.75)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.panda_foreground);
        }
        else if(rand <= 0.9)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.grandma_foreground);
        }
        else
        {
            this.emoteDisplayer.setImageResource(R.mipmap.sanic_foreground);
        }

        updateEmoteData();
    }

    private void smile()
    {
//        Toast.makeText(MainActivity.this, ":)", Toast.LENGTH_SHORT).show();
        double rand = Math.random();

        if(rand <= 0.25)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.smiley_foreground);
        }
        else if(rand <= 0.5)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.harold_foreground);
        }
        else if(rand <= 0.75)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.nyan_cat_foreground);
        }
        else
        {
            this.emoteDisplayer.setImageResource(R.mipmap.doge_foreground);
        }

        updateEmoteData();
    }

    private void taunt()
    {
//        Toast.makeText(MainActivity.this, "Taunt shown", Toast.LENGTH_SHORT).show();

        double rand = Math.random();

        if(rand <= 0.15)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.shrek_foreground);
        }
        else if(rand <= 0.3)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.troll_foreground);
        }
        else if(rand <= 0.4)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.tauntface_foreground);
        }
        else if(rand <= 0.5)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.roll_safe_foreground);
        }
        else if(rand <= 0.6)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.jake_foreground);
        }
        else if(rand <= 0.7)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.dat_boi_foreground);
        }
        else if(rand <= 0.8)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.deal_with_it_foreground);
        }
        else if(rand <= 0.9)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.u_mad_bro_foreground);
        }
        else
        {
            this.emoteDisplayer.setImageResource(R.mipmap.laughing_foreground);
        }

        updateEmoteData();
    }

    private void goodLuck()
    {
        Toast.makeText(MainActivity.this, "Good Luck!", Toast.LENGTH_SHORT).show();

        double rand = Math.random();

        if(rand <= 0.3)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.handshake_foreground);
        }
        else if(rand <= 0.55)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.good_luck_have_fun_foreground);
        }
        else if(rand <= 0.8)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.good_luck3_foreground);
        }
        else if(rand <= 0.95)
        {
            this.emoteDisplayer.setImageResource(R.mipmap.good_luck1_foreground);
        }
        else
        {
            this.emoteDisplayer.setImageResource(R.mipmap.good_luck2_foreground);
        }

        updateEmoteData();
    }

    private void updateEmoteData()
    {
        this.currentEmoteFrameTimer = 0;
        this.emoteDisplayer.setVisibility(View.VISIBLE);
    }

    public void displayEmoteSent(int emoteNumber)
    {

        String sent = "Sent: ";
        switch (emoteNumber) {
            case 0:
                Toast.makeText(MainActivity.this, sent+"Dab on them fools", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, sent+":)", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this, sent+"Taunt shown", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this, sent+"Good Luck!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void logEmoteSelected(int emoteNumber)
    {
        String logEmote = "MainActivity: logEmoteSelected: ";
        switch (emoteNumber) {
            case 0:
                Log.d(TAG,logEmote+"Dab on them fools");
                break;
            case 1:
                Log.d(TAG, logEmote+":)");
                break;
            case 2:
                Log.d(TAG, logEmote+"Taunt shown");
                break;
            case 3:
                Log.d(TAG, logEmote+"Good Luck!");
                break;
        }
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

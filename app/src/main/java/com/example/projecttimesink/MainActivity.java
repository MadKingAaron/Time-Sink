package com.example.projecttimesink;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;

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

    Button bluetoothButton;

    private boolean bluetoothDevicePaired;

    private BluetoothAdapter mBluetoothAdapter;

    //List of found BT devices
    private ArrayList<BluetoothDevice> mBTDevices;

    //BluetoothDevice that are connected to
    private BluetoothDevice mBTDevice;

    private BluetoothConnectionService mBluetoothConnection;

    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //////////Broadcast Recievers/////////////////////////
    //Create BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReciever1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //When discovery finds a device

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onRecieve: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onRecieve: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onRecieve: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onRecieve: STATE TURNING ON");
                        break;
                }
            }
            //if(BluetoothDevice.ACTION_FOUND.equals(action))
            //{
            //Get the BluetoothDevice object from the Intent
            //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //Add the name and address to an array adapter to show in a ListView
            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            //}
        }
    };

    private final BroadcastReceiver mBroadcastReciever2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in disco mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReciever2: Disco Enabled!");
                        break;
                    //Device not in disco mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReciever2: Disco Enabled. Able to recieve connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReciever2: Disco Enabled. No able to recieve connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReciever2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReciever2: Connected.");
                        break;
                }
            }
        }
    };


    private BroadcastReceiver mBroadcastReciever3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra((BluetoothDevice.EXTRA_DEVICE));
                mBTDevices.add(device);
                Log.d(TAG, "onRecieve: " + device.getName() + ": " + device.getAddress());
                //mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                // lvNewDevices.setAdapter(mDeviceListAdapter);
                //lvNewDevices.setAdapter(mDeviceListAdapter);

            }
        }
    };
    private BroadcastReceiver mBroadcastReciever4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Log.d(TAG, "");

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //3 cases
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "mBroadcastReciever4: BOND_BONDED.");
                    //inside BroadcastReciever4
                    //assign mBTDevice to device its paired with
                    mBTDevice = mDevice;
                }
                //case2: creating a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "mBroadcastReciever4: BOND_BONDING");
                }
                //case3: breaking a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "mBroadcastReciever4: BOND_NONE");
                }
            }
        }
    };

    //TODO finish broadcast reciever
    private BroadcastReceiver messageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("message");

            //TODO finish emote reciever
            //messages.delete(0, messages.length() - 1);

            //messages.append(text);
        }
    };

    private void createBluetooth()
    {
        //Create broadCast Reciever for passing message to mainActivity
        //LocalBroadcastManager.getInstance(this).registerReceiver(this.messageReciever, new IntentFilter("incomingMessage"));

        //Create arraylist of all discoverable devices
        this.mBTDevices = new ArrayList<>();

        //Broadcasts when bond state changes (ie: pairing)
        IntentFilter pairingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReciever4, pairingFilter);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.bluetoothButton = (Button) findViewById(R.id.bluetoothButton);

        this.bluetoothDevicePaired = false;

        this.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                BTHandler(view);
            }
        });


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
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void BTHandler(View view)
    {
        Log.d(TAG, "Starting Bluetooth handler");

        if(!bluetoothDevicePaired)
        {
            try {
                this.enableBT();

                this.enableDisable_Discoverable(view);

                this.discoverDevices(view);


            }catch(NoBluetoothAdapterException nbae)
            {
                Log.d(TAG, nbae.getMessage());
                //TODO Add message alert
            }
        }
        else //Start connection
        {
            Log.d(TAG, "onClick: starting connection");
            startConnection();
        }
    }

    //create method for starting connection
    //*** TODO remember the connection will fail and app will crash if you haven't pair first
    //*** Take this into account when using
    public void startConnection() {
        startBTConnection(mBTDevice, UUID_INSECURE);
    }

    //Start message service
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");

        this.mBluetoothConnection.startClient(device, uuid);


    }

    public void enableBT() throws NoBluetoothAdapterException {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDiableBT: DEVICE DOES NOT HAVE ONBOARD BLUETOOTH ADAPTER");
            throw new NoBluetoothAdapterException("DEVICE DOES NOT HAVE ONBOARD BLUETOOTH ADAPTER");
        }

        else if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            //Intent filter
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciever1, BTIntent);
        }
    }


    public void enableDisable_Discoverable(View view) {

        Log.d(TAG, "btnEnableDiable_Discoverable: Making device discoverable for 300 seconds");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        registerReceiver(mBroadcastReciever2, intentFilter);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void discoverDevices(View view) {
        Log.d(TAG, "btnDisocver: Looking for unpaired devices");

        if (mBluetoothAdapter.isDiscovering()) {//Restart discovering
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling Discovery.");

            //For devices running Lollipop or greater
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReciever3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {
            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReciever3, discoverDevicesIntent);
        }

        final ProgressDialog bluetoothDeviceSearchProgress = this.initalizeBluetoothDeviceSearchProgress();
        //Handler to delay by 5 seconds
        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                //what ever you do here will be done after 5 seconds delay.
                bluetoothDeviceSearchProgress.dismiss();
                createBTDeviceListDialog();
            }
        };
        handler.postDelayed(r, 10000);



    }

    private ProgressDialog initalizeBluetoothDeviceSearchProgress()
    {
        return this.initializeProgressDialog(this, "Searching for BT Devices", "Please Wait...");
    }

    private ProgressDialog initializeProgressDialog(Context context, String title, String message)
    {
        return ProgressDialog.show(context, title, message, true);
    }

    private void createBTDeviceListDialog()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Device to Pair to");

        // add a list
        String[] deviceArray = adapterListToStringArray(mBTDevices);
        //animals = (String[])stringArrayList.toArray();

        builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPairedBTDeviceAndChangeBTButton(which);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Only execute on Android Lollipop or above
    //Required to enable bluetooth on those devices
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private String[] adapterListToStringArray(ArrayList<BluetoothDevice> devList)
    {
        String[] deviceStringArray = new String[devList.size()];
        for(int i = 0; i < deviceStringArray.length; ++i)
        {
            deviceStringArray[i] = ("Name: "+devList.get(i).getName() + "\nAddress: "+devList.get(i).getAddress());
        }

        return deviceStringArray;
    }



    private void setPairedBTDeviceAndChangeBTButton(int deviceIndex)
    {
        changeBTButton();

        setPairedBTDevice(deviceIndex);
    }

    private void changeBTButton()
    {
        bluetoothDevicePaired = true;

        this.bluetoothButton.setText("Start Connection");
    }

    private void setPairedBTDevice(int deviceIndex)
    {
        //first cancel discovery because it's very memory intensive
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You clicked on a device!");

        String deviceName = mBTDevices.get(deviceIndex).getName();
        String deviceAddress = mBTDevices.get(deviceIndex).getName();

        Log.d(TAG, "onItemClick: Clicked on Device Name: " + deviceName + " Address: " + deviceAddress);

        //create the bond
        //NOTE: Requires API 17+ (Jellybean MR2 ?)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(deviceIndex).createBond();


            //Start connection service
            mBTDevice = mBTDevices.get(deviceIndex);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }

    //TODO Use when emotes are implemented to send via bluetooth
    public void sendEmoteViaBluetooth(EmoteInterface emote) throws EmoteNotSentException{

        try {
            //Convert to byte array
            byte[] toSend= SerializeServiceClass.serializeObject(emote);

            this.mBluetoothConnection.write(toSend);
        }catch (IOException ioe)
        {
            throw new EmoteNotSentException("Emote Unable to send ---- "+ioe.getMessage());
        }
    }
}

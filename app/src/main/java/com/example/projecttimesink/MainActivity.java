package com.example.projecttimesink;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private final float FRAMES_PER_SECOND = 100; // MAX 1000 (INCLUSIVE), MIN 0 (EXCLUSIVE) FRAMES_PER_SECOND // 0 < FRAMES_PER_SECOND <= 1000
    private final Handler handler = new Handler();
    private final int delay = getDelay(); // Delay in milliseconds
    private ActionableList actionableList;
    private Actionable[] actionableObjects;

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

    //TAG for logs
    private final String TAG = "MainActivity";


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

    private BroadcastReceiver messageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("message");

            //TODO finish emote reciever
            //messages.delete(0, messages.length() - 1);

            //messages.append(text);
        }
    };

    private void create()
    {
        // TIMER AND BUTTON
        SwitchTimer timer = new SwitchTimer(this, LeaderboardPopUp.class,
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
                if (waitTime > 300)
                {
                    this.antiCheatText.setText("Are you still there?");
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
        //sensorManager.registerListener(MainActivity.this,gyroSensor.gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
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

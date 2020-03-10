package com.example.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;

    EditText editText;

    TextView incomingMessages;
    StringBuilder messages;

    Button btnStartConnection;
    Button btnSend;


    //For List Devices
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

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
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

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

            messages.delete(0, messages.length() - 1);

            messages.append(text);
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        unregisterReceiver(mBroadcastReciever1);
        unregisterReceiver(mBroadcastReciever2);
        unregisterReceiver(mBroadcastReciever3);
        unregisterReceiver(mBroadcastReciever4);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //Added for Bluetooth

        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);

        //Create broadCast Reciever for passing message to mainActivity
        LocalBroadcastManager.getInstance(this).registerReceiver(this.messageReciever, new IntentFilter("incomingMessage"));

        //Create textView for incomingMessages
        this.incomingMessages = (TextView) findViewById(R.id.incomingMessage);


        //Section for bluetooth disco
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);

        //Create listview for listing
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        //Create arraylist of all discoverable devices
        mBTDevices = new ArrayList<>();


        //Create btnStartConnection
        this.btnStartConnection = (Button) findViewById(R.id.btnStartConnection);
        //Create btnSend
        this.btnSend = (Button) findViewById(R.id.btnSend);
        //Create editText
        this.editText = (EditText) findViewById(R.id.editText);


        //Create StringBuilder for message string
        this.messages = new StringBuilder();

        //Broadcasts when bond state changes (ie: pairing)
        IntentFilter pairingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReciever4, pairingFilter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: enable/disable BT");
                enableDisableBT();
            }
        });


        btnEnableDisable_Discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: enable/disbaling bluetooth disco.");
                //TODO create button on click

                btnEnableDisable_Discoverable(v);
            }
        });


        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create bytes
                byte[] bytes = editText.getText().toString().getBytes(Charset.defaultCharset());
                Log.d(TAG, "btnSend: Sent: Message "+ editText.getText().toString());

                mBluetoothConnection.write(bytes);

                editText.setText("");
            }
        });


    }


    //create method for starting connection
    //*** TODO remember the connection will fail and app will crash if you haven't pair first
    public void startConnection() {
        startBTConnection(mBTDevice, UUID_INSECURE);
    }

    //Start message service
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");

        this.mBluetoothConnection.startClient(device, uuid);


    }


    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDiableBT: DEVICE DOES NOT HAVE ONBOARD BLUETOOTH ADAPTER");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciever1, BTIntent);
        } else { //bluetooth adapter already enabled

            Log.d(TAG, "enableDisableBT: disabling BT");

            mBluetoothAdapter.disable();

            //IntentFilter to catch state change
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciever1, BTIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnEnableDisable_Discoverable(View view) {

        Log.d(TAG, "btnEnableDiable_Discoverable: Making device discoverable for 300 seconds");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        registerReceiver(mBroadcastReciever2, intentFilter);


    }

    public void btnDiscover(View view) {
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


    }

    //Only execute on Android Lollipop or above
    //Required to enable bluetooth on those devices
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because it's very memory intensive
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You clicked on a device!");

        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getName();

        Log.d(TAG, "onItemClick: Clicked on Device Name: " + deviceName + " Address: " + deviceAddress);

        //create the bond
        //NOTE: Requires API 17+ (Jellybean MR2 ?)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();


            //Start connection service
            this.mBTDevice = mBTDevices.get(i);
            this.mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }
}
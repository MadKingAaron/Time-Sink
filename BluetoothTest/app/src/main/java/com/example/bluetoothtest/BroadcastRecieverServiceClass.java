package com.example.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;

public class BroadcastRecieverServiceClass {
    public static final String TAG = "BcastReciever";

    public static BluetoothAdapter mBluetoothAdapter;

    public static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public static BluetoothDevice mBTDevice;

    //For List Devices
    public static ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public static DeviceListAdapter mDeviceListAdapter;
    public static ListView lvNewDevices;


    public static BluetoothConnectionService mBluetoothConnection;

    public static final BroadcastReceiver mBroadcastReciever1 = new BroadcastReceiver() {
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

    public static final BroadcastReceiver mBroadcastReciever2 = new BroadcastReceiver() {
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

    public static BroadcastReceiver mBroadcastReciever3 = new BroadcastReceiver() {
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
                lvNewDevices.setAdapter(mDeviceListAdapter);

            }
        }
    };
    public static BroadcastReceiver mBroadcastReciever4 = new BroadcastReceiver() {
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

}

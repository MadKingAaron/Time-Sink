package com.example.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothMainActivityService {

    private static final String TAG = "BTMainService";

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

    public ArrayList<BluetoothDevice> mBTDevices;
}

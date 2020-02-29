package com.example.bluetoothmessenging;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionService {

    public static final String TAG = "BluetoothConnectServe";
    public static final String appname = "Aaron's Funky Fresh Chatter";

    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter;
    Context context;

    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    ProgressDialog progressDialog;

    public BluetoothConnectionService(Context context)
    {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    //Thread for accepting connection to other device

    /**
     * Thread runs while listening for incoming connection. Behaves like a server-side client. Runs
     * until connection is accepted (or until cancelled)
     */
    private class AcceptThread extends Thread{
        //Local server socket
        private final BluetoothServerSocket serverSocket;


        public AcceptThread()
        {
            BluetoothServerSocket tmp = null;

            //Create a new listening server socket
            try{
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up server using: "+ UUID_INSECURE);
            }catch (IOException e)
            {
                Log.d(TAG, "AcceptThread: IOException: "+ e.getMessage());
            }

            serverSocket = tmp;

        }

        public void run(){
            Log.d(TAG, "run AcceptThread Running.");

            BluetoothSocket socket = null;



            try {
                //This a blocking call and will only return on a
                //successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start...");

                socket = this.serverSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection");
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(TAG, "AccepThread: IOException: "+e.getMessage());
            }


            if(socket != null)
            {
                connected(socket, mmDevice);
            }


            Log.d(TAG, "END Accept thread");
        }


        public void cancel()
        {
            Log.d(TAG, "cancel: Canceling AcceptThread");

            try
            {
                serverSocket.close();
            }catch(IOException e)
            {
                Log.d(TAG, "Cancel: Close of AcceptThread ServerSocket failed. "+ e.getMessage());
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a device. It runs straight through;
     * the connection either succeeds or fails.
     */

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;

        private ConnectThread(BluetoothDevice device, UUID uuid)
        {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run()
        {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN: connectThread");

            //Get a BluetoothSocket for a connection with the BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID "
                        +UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);

            } catch (IOException e) {
                Log.d(TAG, "ConnectThread: Could not create InsecureRfcommSocket "+ e.getMessage());
            }

            socket = tmp;

            //Cancel discovery when connection made
            //Disco memory intesive, will slow down connection
            bluetoothAdapter.cancelDiscovery();


            //Make connection to BluetoothSocket

            //This is a blocking call and will only return on a successful connection
            //or exception
            try {

                Log.d(TAG, "run: connectThread: Attempting to connect");

                socket.connect();

                Log.d(TAG, "run: connectThread: Connection Successful");
            }catch (IOException e)
            {
                //Close socket

                try
                {
                    socket.close();
                    Log.d(TAG, "run: connectThread: Closed Socket");
                }catch (IOException ioe)
                {
                    Log.e(TAG, "run: connectThread: Unable to close connection in socket "+ ioe.getMessage());
                }

                Log.d(TAG, "run: connectThread: Could not connect to UUID: "+UUID_INSECURE);
            }

            //Talk about in part 3
            connected(socket, mmDevice);
        }

        public void cancel(){
            try {
                Log.d(TAG, "cancel: CLosing Client Socket.");
                socket.close();
            }catch (IOException e)
            {
                Log.e(TAG, "cancel: close() of socket in connectThread failed "+ e.getMessage());
            }
        }

    }

    /**
     * Start the messaging service. Specifically start AcceptThread to begin a session in listening (server) mode.
     * Called by the Activity onResume()
     */

    public synchronized void start()
    {
        Log.d(TAG, "start");

        //Cancel any thread to make a connection
        if(this.connectThread != null)
        {
            this.connectThread.cancel();
            this.connectThread = null;
        }

        if(this.insecureAcceptThread == null)
        {
            this.insecureAcceptThread = new AcceptThread();
            this.insecureAcceptThread.start();
        }
    }

    /**
     * AcceptThread starts and sits waiting for a connection.
     * Then ConnectThread starts and attempts to make a connection with the other device's AcceptThread.
     */
    public synchronized void startClient(BluetoothDevice device, UUID uuid)
    {
        Log.d(TAG, "startClient");

        //initprogress dialog box
        this.progressDialog = ProgressDialog.show(this.context, "Connecting Bluetooth"
            ,"Please Wait...", true);

        //Create connectThread
        this.connectThread = new ConnectThread(device, uuid);
        this.connectThread.start();

    }

}

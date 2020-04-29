package com.example.projecttimesink;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {

    public static final String TAG = "BluetoothConnectServe";
    public static final String appname = "TimeSink";
    public static final String BROADCAST_FILTER = "BluetoothConnectionService_broadcast_receiver_intent_filter";

    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter;
    Context context;

    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;


    private BluetoothMessageReceive messagePackage;

    ProgressDialog progressDialog;

    public BluetoothConnectionService(Context context, BluetoothMessageReceive bluetoothPackage)
    {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.messagePackage = bluetoothPackage;

        start();
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



            while(true)
            {
                try {
                    //This a blocking call and will only return on a
                    //successful connection or an exception
                    Log.d(TAG, "run: RFCOM server socket start...");

                    socket = this.serverSocket.accept();

                    Log.d(TAG, "run: RFCOM server socket accepted connection");
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.d(TAG, "AcceptThread: IOException: "+e.getMessage());
                }


                if(socket != null)
                {
                    connected(socket, mmDevice);

                    break;
                }

            }

            Log.i(TAG, "END Accept thread");
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
                Log.d(TAG, "cancel: Closing Client Socket.");
                socket.close();
            }catch (IOException e)
            {
                Log.e(TAG, "cancel: close() of socket in connectThread failed "+ e.getMessage());
            }
        }

    }


    private class ConnectedThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final OutputStream outputStream;
        private final InputStream inputStream;
        private final int BUFFER_SIZE = 2048;


        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d(TAG, "ConnectedThread: Starting.");

            this.bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            //dismiss the process dialog when connection is established
            try {
                progressDialog.dismiss();
            }catch (NullPointerException npe)
            {
                Log.d(TAG, "ConnectedThread: Tried to close progress dialog box that doesnt exits "+npe.getMessage());
            }


            try{
                tempIn = this.bluetoothSocket.getInputStream();
                tempOut = this.bluetoothSocket.getOutputStream();
            }catch (IOException e)
            {
                Log.d(TAG, "ConnectedThread: Input/Output Stream Unable to Create" + e.getMessage());
            }


            this.outputStream = tempOut;
            this.inputStream = tempIn;

            Log.d(TAG, "ConnectedThread: Construct: Initialized");
        }

        public void run()
        {
            byte[] buffer = new byte[BUFFER_SIZE]; //buffer store for the stream

            int bytes; //bytes returned from read()

            //Keep listening to the InputStream until an exception occurs
            while(true)
            {
                //Read from the InputStream
                try {
                    bytes = this.bluetoothSocket.getInputStream().read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);

                    Log.d(TAG, "ConnectedThread: InputStream: "+incomingMessage);

                    //Pass to main activity through intent named "incomingMessage"
                    Intent incomingMessageIntent = new Intent(BROADCAST_FILTER);
                    incomingMessageIntent.putExtra("message", incomingMessage);

                    //Send the broadcast
                    //TODO Send intent
                    //LocalBroadcastManager.getInstance(context).sendBroadcast(incomingMessageIntent);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(incomingMessageIntent);

                    messagePackage.updateData(incomingMessage);

                } catch (IOException e) {
                    Log.d(TAG, "ConnectedThread: Issue reading with InputStream "+e.getMessage());
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes)
        {

            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "ConnectedThread: write: Writing to outputstream: "+text);

            bytes = "Hello".getBytes();

            Log.d(TAG, "ConnectedThread: write: Byte Arr Length "+ bytes.length);
            try
            {
                this.bluetoothSocket.getOutputStream().write(bytes);
                Log.d(TAG, "ConnectedThread: write: message sent");
            } catch(IOException e)
            {
                Log.d(TAG, "ConnectedThread: write: ERROR SENDING MESSAGE: "+ e.getMessage());
            }

        }

        //Call this from the main activity to shutdown the connection
        public void cancel()
        {
            try{
                this.bluetoothSocket.close();
                Log.d(TAG, "ConnectedThread: Cancel: Closed BT Socket");
            }catch (IOException e)
            {
                Log.d(TAG, "ConnectedThread: Cancel: ERROR CLOSING BT SOCKET" + e.getMessage());
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
        Log.d(TAG, "startClient: Started");

        //initprogress dialog box
        this.progressDialog = ProgressDialog.show(this.context, "Connecting Bluetooth"
                ,"Please Wait...", true);

        //Create connectThread
        this.connectThread = new ConnectThread(device, uuid);
        this.connectThread.start();

    }

    private void connected(BluetoothSocket socket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        //Start the thread to manage the connection and perform transmissions
        this.connectedThread = new ConnectedThread(socket);
        this.connectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsyncronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out)
    {
        //Create temporary object
        //ConnectedThread r;

        //Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write called.");
        //perform the write
        this.connectedThread.write(out);
    }



}


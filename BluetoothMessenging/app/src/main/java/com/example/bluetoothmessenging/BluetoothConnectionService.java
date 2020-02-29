package com.example.bluetoothmessenging;

import android.bluetooth.BluetoothAdapter;
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




}

package com.example.bluetoothmessenging;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializeServiceClass {
    private static final String TAG = "SerializeService";
    public static byte[] serialize(Object toSerialize)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(toSerialize);

            return byteArrayOutputStream.toByteArray();

        }catch (IOException e)
        {
            Log.d(TAG, "SerializeServiceClass: serialize: ERROR MAKING ObjectOutputStream "+ e.getMessage());
            return null;
        }

    }
}

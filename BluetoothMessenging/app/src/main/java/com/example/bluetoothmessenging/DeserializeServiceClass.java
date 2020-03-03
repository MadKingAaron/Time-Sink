package com.example.bluetoothmessenging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class DeserializeServiceClass {
    private static final String TAG = "DeserializeService";

    public static Object deserialize(byte[] bytes)
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

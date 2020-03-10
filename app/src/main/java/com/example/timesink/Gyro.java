package com.example.timesink;

import android.hardware.Sensor;
import android.hardware.SensorEvent;


public class Gyro
{
    float xAxis;
    float yAxis;
    float zAxis;

    Sensor gyroscope;

    boolean isMoving;



    public float getXAxis(SensorEvent event)
    {
        return this.xAxis=event.values[0];
    }

    public float getYAxis(SensorEvent event)
    {
        return this.yAxis=event.values[1];
    }

    public float getZAxis(SensorEvent event)
    {
        return this.zAxis=event.values[2];
    }
}

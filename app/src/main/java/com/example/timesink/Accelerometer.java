package com.example.timesink;

public class Accelerometer
{
    private float xAxis, yAxis, zAxis;
    private float prevAccelValue, currAccelValue, accelValue;

    public Accelerometer(float xAxis, float yAxis, float zAxis, float prevAccelValue, float currAccelValue, float accelValue)
    {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
        this.prevAccelValue = prevAccelValue;
        this.currAccelValue = currAccelValue;
        this.accelValue = accelValue;
    }

    public float calculateAccelValue()
    {
        currAccelValue = (float) Math.sqrt((xAxis * xAxis) + (yAxis * yAxis) + (zAxis * zAxis));
        accelValue = accelValue * 0.9f + (currAccelValue - prevAccelValue);
        return accelValue;
    }
}

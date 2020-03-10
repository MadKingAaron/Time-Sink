package com.example.timesink;

public class GyroAntiCheat
{
    float xAxis;
    float yAxis;
    float zAxis;
    boolean isMoving;

    public GyroAntiCheat(float xAxis, float yAxis, float zAxis)
    {
        this.xAxis=xAxis;
        this.yAxis=yAxis;
        this.zAxis=zAxis;
    }

    public boolean checkMovement()
    {
        if(this.xAxis==0 && this.yAxis==0 && this.zAxis==0)
        {
            isMoving=false;
        }
        return isMoving;
    }
}

package com.example.projecttimesink;

public class BasicDelay implements Delay
{
    private long waitTime;

    public BasicDelay()
    {
        this(0);
    }

    public BasicDelay(long waitTime)
    {
        this.waitTime = waitTime;
    }

    @Override
    public void delay()
    {
        try
        {
            Thread.sleep(this.waitTime);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

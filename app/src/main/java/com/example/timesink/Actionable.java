package com.example.timesink;

public interface Actionable
{
    // Method that runs once per frame when activity is active
    public void update();

    // Method that runs upon pause of activity
    public void pause();

}
package com.example.projecttimesink;

public interface Actionable
{
    // Method that runs once per frame when activity is active
    void update();

    // Method that runs upon pause of activity
    void pause();

}
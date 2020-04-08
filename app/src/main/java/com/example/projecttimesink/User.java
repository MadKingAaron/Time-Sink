package com.example.projecttimesink;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User
{
    public String username;
    public Long timeWasted; // Total Time Wasted

    public User()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, Long timeWasted)
    {
        this.username = username;
        this.timeWasted = timeWasted;
    }

}
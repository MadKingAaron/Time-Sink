package com.example.projecttimesink;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User
{
    public String username;
    public Long totalTimeWasted;
    public Long longestTimeWasted;
    public Long placement;

    public User()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, Long timeWasted, Long placement)
    {
        this.username = username;
        this.totalTimeWasted = timeWasted;
        this.longestTimeWasted = new Long(Long.valueOf(timeWasted));
        this.placement = placement;
    }

    public void updateUsername(String username)
    {
        if(username != null)
            this.username = username;
    }

    // returns true if new time is greater than previous
    public boolean updateTimeWasted(Long timeWasted)
    {
        this.totalTimeWasted += timeWasted;

        if(timeWasted > this.longestTimeWasted)
        {
            this.longestTimeWasted = timeWasted;
            return true;
        }

        return false;
    }
}
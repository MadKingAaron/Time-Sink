package com.example.projecttimesink;

public class AchievementMessageVerification
{

    public long currentTime;

    private int numberOfTimeBasedAchievementsUnlocked;

    private long oneSecond=1000;
    private long oneMinute=60000;

    private void determineNumberOfTimeBasedAchievementsUnlocked()
    {

            checkFirst();
            checkSecond();
            checkThird();

    }

    private void checkFirst()
    {
        if(this.currentTime>=oneSecond)
        {
            numberOfTimeBasedAchievementsUnlocked=1;
        }
    }

    private void checkSecond()
    {
        if(this.currentTime>=oneMinute&&this.currentTime<(oneMinute*5))
        {
            numberOfTimeBasedAchievementsUnlocked=2;
        }
    }

    private void checkThird()
    {
        if(this.currentTime>=(oneMinute*5))
        {
            numberOfTimeBasedAchievementsUnlocked=3;
        }
    }

    public int getNumberOfTimeBasedAchievementsUnlocked()
    {
        determineNumberOfTimeBasedAchievementsUnlocked();
        return numberOfTimeBasedAchievementsUnlocked;
    }
}

package com.example.projecttimesink;

public class AchievementMessageVerification
{
    public long currentTime;
    public long longestTime;
    private int numberOfTimeBasedAchievementsUnlocked;

    private long oneSecond=1000;
    private long oneMinute=60000;

    private void determineNumberOfTimeBasedAchievementsUnlocked()
    {
        if(this.currentTime>this.longestTime)
        {
            checkFirst();
            checkSecond();
            checkThird();
            checkFourth();
            checkFifth();
            checkSixth();
            checkSeventh();
            checkEight();
        }
        else
        {
            this.numberOfTimeBasedAchievementsUnlocked=0;
        }
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
        if(this.currentTime>=oneMinute)
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

    private void checkFourth()
    {
        if(this.currentTime>=(oneMinute*10))
        {
            numberOfTimeBasedAchievementsUnlocked=4;
        }
    }

    private void checkFifth()
    {
        if(this.currentTime>=(oneMinute*30))
        {
            numberOfTimeBasedAchievementsUnlocked=5;
        }
    }

    private void checkSixth()
    {
        if(this.currentTime>=(oneMinute*60))
        {
            numberOfTimeBasedAchievementsUnlocked=6;
        }
    }

    private void checkSeventh()
    {
        if(this.currentTime>=(oneMinute*(60*12)))
        {
            numberOfTimeBasedAchievementsUnlocked=7;
        }
    }

    private void checkEight()
    {
        if(this.currentTime>=(oneMinute*(60*24)))
        {
            numberOfTimeBasedAchievementsUnlocked=8;
        }
    }

    public int getNumberOfTimeBasedAchievementsUnlocked()
    {
        determineNumberOfTimeBasedAchievementsUnlocked();
        return numberOfTimeBasedAchievementsUnlocked;
    }
}

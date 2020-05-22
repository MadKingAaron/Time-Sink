package com.example.projecttimesink;

public class Achievement
{
    private long longestTimeWasted;

    public Achievement(long longestTimeWasted)
    {
        this.longestTimeWasted=longestTimeWasted;
    }

    public int checkLongestTimeWastedAchievements()
    {
        int numberOfAchievementsUnlocked=0;

        if(this.longestTimeWasted>0&&this.longestTimeWasted<60000)
        {
            numberOfAchievementsUnlocked=1;
        }
        else if(this.longestTimeWasted>60000&&this.longestTimeWasted<(60000)*5)
        {
            numberOfAchievementsUnlocked=2;
        }
        else if(this.longestTimeWasted>(60000)*5&&this.longestTimeWasted<(60000)*10)
        {
            numberOfAchievementsUnlocked=3;
        }
        else if(this.longestTimeWasted>(60000)*10&&this.longestTimeWasted<(60000)*30)
        {
            numberOfAchievementsUnlocked=4;
        }
        else if(this.longestTimeWasted>(60000)*30&&this.longestTimeWasted<(60000)*60)
        {
            numberOfAchievementsUnlocked=5;
        }
        else if(this.longestTimeWasted>(60000)*60&&this.longestTimeWasted<(60000)*(60*12))
        {
            numberOfAchievementsUnlocked=6;
        }
        else if(this.longestTimeWasted>(60000)*(60*12)&&this.longestTimeWasted<(60000)*(60*24))
        {
            numberOfAchievementsUnlocked=7;
        }
        else if(this.longestTimeWasted>(60000)*(60*24))
        {
            numberOfAchievementsUnlocked=8;
        }

        return numberOfAchievementsUnlocked;
    }
}

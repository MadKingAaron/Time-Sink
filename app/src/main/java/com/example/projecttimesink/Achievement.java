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
        else if(this.longestTimeWasted>(60000)*5)
        {
            numberOfAchievementsUnlocked=3;
        }
        return numberOfAchievementsUnlocked;
    }
}

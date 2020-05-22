package com.example.projecttimesink;

public class SarcasticComment
{
    public String sarcasticComment;

    public void determineSarcasticComment(long currentTime)
    {
        if(currentTime>10000&&currentTime<17000)//10,000 = 10 seconds
        {
            emailComment();
        }
        else if(currentTime>30000&&currentTime<37000)
        {
            googleComment();
        }
        else if(currentTime>60000&&currentTime<67000)//60000=1 minute
        {
            wholeMinuteComment();
        }
        else if(currentTime>(60000)*10&&currentTime<((60000)*10)+7000)//(60000)*10 -> 10 minutes
        {
            laundryComment();
        }
        else if(currentTime>(60000)*20&&currentTime<((60000)*20)+7000)
        {
            powerNapComment();
        }
        else if(currentTime>(60000)*30&&currentTime<((60000)*30)+7000)
        {
            exerciseComment();
        }
        else
        {
            this.sarcasticComment="";
        }
    }

    private void emailComment()
    {
        this.sarcasticComment="You could've checked an email";
    }

    private void googleComment()
    {
        this.sarcasticComment="You could’ve googled something useful";
    }

    private void wholeMinuteComment()
    {
        this.sarcasticComment="Wow. You've already wasted a minute of your life on this button";
    }

    private void laundryComment()
    {
        this.sarcasticComment="You could’ve folded your laundry";
    }

    private void powerNapComment()
    {
        this.sarcasticComment="You could’ve taken a quick power nap";
    }

    private void exerciseComment()
    {
        this.sarcasticComment="You could’ve done your recommended daily amount of exercise";
    }
}

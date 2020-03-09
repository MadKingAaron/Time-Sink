package com.example.timesink;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SwitchTimer extends Timer
{
    Context currentActivity;
    Class nextActivity;
    Delay delay;

    public SwitchTimer(Context currentActivity, Class nextActivity, TextView timerText, ImageView buttonImage, Button theButton)
    {
        super(timerText, buttonImage, theButton);

        this.currentActivity = currentActivity;
        this.nextActivity = nextActivity;

        this.delay = new BasicDelay();
    }

    public void addDelay(Delay delay) { this.delay = delay; }

    public void setNextActivity(Class nextActivity) { this.nextActivity = nextActivity; }

    @Override
    protected void stopTimer()
    {
        super.stopTimer();

        this.delay.delay();

        switchToNextActivity();
    }

    private void switchToNextActivity()
    {
        Intent intent = new Intent(this.currentActivity, nextActivity);
        this.currentActivity.startActivity(intent);
    }
}

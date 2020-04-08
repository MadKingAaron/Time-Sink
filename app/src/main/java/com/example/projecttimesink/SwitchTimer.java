package com.example.projecttimesink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SwitchTimer extends Timer
{
    private Context currentActivity;
    private Class nextActivity;
    private Delay delay;
    private Bundle bundle;

    public SwitchTimer(Context currentActivity, Class nextActivity, TextView timerText, ImageView buttonImage, Button theButton)
    {
        super(timerText, buttonImage, theButton);

        this.currentActivity = currentActivity;
        this.nextActivity = nextActivity;

        this.delay = new BasicDelay();
    }

    public void setNextActivity(Class nextActivity)
    {
        this.nextActivity = nextActivity;
    }

    public void addDelay(Delay delay)
    {
        this.delay = delay;
    }

    public void addBundle(Bundle bundle)
    {
        this.bundle = bundle;
    }

    @Override
    protected void stopTimer()
    {
        super.stopTimer();

        this.delay.delay();

        if(this.bundle == null)
            this.bundle = new Bundle();

        this.bundle.putSerializable("time", super.getTotalTime());

        switchToNextActivity();
    }

    private void switchToNextActivity()
    {
        Intent intent = new Intent(this.currentActivity, this.nextActivity);

        if (this.bundle != null)
            intent.putExtras(bundle);

        this.currentActivity.startActivity(intent);
    }
}

package com.example.projecttimesink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SwitchButton
{
    private Context currentActivity;
    private Class nextActivity;
    private Switchable switchCondition;
    private Delay delay;
    private Finish finish;
    private Bundle bundle;

    private Button theButton;

    public SwitchButton(Context currentActivity, Class nextActivity, Button theButton)
    {
        this.currentActivity = currentActivity;
        this.nextActivity = nextActivity;
        this.theButton = theButton;

        this.switchCondition = null;
        this.delay = new BasicDelay();
        this.finish = null;

        this.theButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                // release of button
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    if(switchCondition == null || switchCondition.canSwitch())
                    {
                        delay.delay();

                        switchToNextActivity();
                    }
                }

                return true;
            }
        });
    }

    public void setNextActivity(Class nextActivity)
    {
        this.nextActivity = nextActivity;
    }

    public void addSwitchCondition(Switchable switchCondition) { this.switchCondition = switchCondition; }

    public void addDelay(Delay delay)
    {
        this.delay = delay;
    }

    public void addBundle(Bundle bundle)
    {
        this.bundle = bundle;
    }

    public void addFinish(Finish finish) { this.finish = finish; }

    private void switchToNextActivity()
    {
        Intent intent = new Intent(this.currentActivity, this.nextActivity);

        if(this.bundle != null)
            intent.putExtras(this.bundle);

        this.currentActivity.startActivity(intent);

        if(this.finish != null)
            this.finish.finish();
    }
}

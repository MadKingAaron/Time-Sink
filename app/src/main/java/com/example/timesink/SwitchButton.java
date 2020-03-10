package com.example.timesink;

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
    private Delay delay;
    private Bundle bundle;

    private ImageView buttonImage;
    private Button theButton;

    public SwitchButton(Context currentActivity, Class nextActivity, ImageView buttonImage, Button theButton)
    {
        this.currentActivity = currentActivity;
        this.nextActivity = nextActivity;
        this.buttonImage = buttonImage;
        this.theButton = theButton;

        this.delay = new BasicDelay();

        this.theButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                // release of button
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    delay.delay();

                    switchToNextActivity();
                }

                return true;
            }
        });
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

    private void switchToNextActivity()
    {
        Intent intent = new Intent(this.currentActivity, this.nextActivity);

        if (this.bundle != null)
            intent.putExtras(bundle);

        this.currentActivity.startActivity(intent);
    }
}

package com.example.timesink;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SwitchButton
{
    Context currentActivity;
    Class nextActivity;

    ImageView buttonImage;
    Button theButton;

    public SwitchButton(Context currentActivity, Class nextActivity, ImageView buttonImage, Button theButton)
    {
        this.currentActivity = currentActivity;
        this.nextActivity = nextActivity;
        this.buttonImage = buttonImage;
        this.theButton = theButton;

        this.theButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                // release of button
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    switchToNextActivity();

                return true;
            }
        });
    }

    public void setNextActivity(Class nextActivity)
    {
        this.nextActivity = nextActivity;
    }

    private void switchToNextActivity()
    {
        Intent intent = new Intent(this.currentActivity, this.nextActivity);
        this.currentActivity.startActivity(intent);
    }
}

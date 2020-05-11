package com.example.projecttimesink;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Timer implements Actionable
{
    private long startTime;
    private boolean timerIsRunning;
    private long totalTime;

    private TimeText timeText;
    private ImageView buttonImage;
    private Button theButton;

    private MediaPlayer musicMP;

    public Timer(TextView timerText, ImageView buttonImage, Button theButton)
    {
        this.timeText = new TimeText(timerText, (this.totalTime = 0));
        this.buttonImage = buttonImage;
        this.theButton = theButton;

        this.theButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                // start
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startTimer();
                    createMP();
                    musicMP.start();
                }
                    // stop
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    stopTimer();
                    musicMP.release();
                }


                return true;
            }
        });

        resetTimer();
    }

    @Override
    public void update()
    {
        updateTimer();
    }

    @Override
    public void pause()
    {
        if (this.timerIsRunning)
            stopTimer();
    }

    public boolean isRunning()
    {
        return this.timerIsRunning;
    }

    private long getCurrentTime()
    {
        return SystemClock.elapsedRealtime();
    }

    protected void startTimer()
    {
        resetTimer();
        this.startTime = getCurrentTime();
        this.timerIsRunning = true;
        this.buttonImage.setImageResource(R.drawable.button_pressed_foreground);
    }

    protected void updateTimer()
    {
        // sets the time to the current time in milliseconds
        if (this.timerIsRunning)
            setTimer(getCurrentTime() - this.startTime);
    }

    protected void stopTimer()
    {
        this.totalTime = getCurrentTime() - this.startTime;
        this.timerIsRunning = false;
        this.buttonImage.setImageResource(R.drawable.button_unpressed_foreground);
    }

    public long getTotalTime()
    {
        return this.totalTime;
    }

    private void setTimer(long millis)
    {
        this.totalTime=millis;
        this.timeText.updateTime(millis);
    }

    private void resetTimer()
    {
        setTimer(this.totalTime = 0);
    }

    public void createMP ()
    {
        musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.bennytheme);
    }
}

package com.example.timesink;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Timer implements Actionable {
    long startTime;
    boolean timerRunning;
    long totalTime;

    TextView timerText;
    ImageView buttonImage;
    Button theButton;

    public Timer(TextView timerText, ImageView buttonImage, Button theButton) {
        this.timerText = timerText;
        this.buttonImage = buttonImage;
        this.theButton = theButton;

        this.theButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // start
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    startTimer();

                    // stop
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    stopTimer();

                return true;
            }
        });

        resetTimer();
    }

    @Override
    public void update() {
        updateTimer();
    }

    @Override
    public void pause() {
        stopTimer();
    }

    private long getCurrentTime() {
        return SystemClock.elapsedRealtime();
    }

    private void startTimer() {
        resetTimer();
        this.startTime = getCurrentTime();
        this.timerRunning = true;
        this.buttonImage.setImageResource(R.drawable.button_pressed_background);
    }

    private void updateTimer() {
        // sets the time to the current time in milliseconds
        if (this.timerRunning)
            setTimer(getCurrentTime() - this.startTime);
    }

    private void stopTimer() {
        this.totalTime = getCurrentTime() - this.startTime;
        this.timerRunning = false;
        this.buttonImage.setImageResource(R.drawable.button_unpressed_background);
    }

    private void setTimer(long millis) {
        int seconds = (int) (millis / 1000);
        int milliseconds = (int) (millis % 1000);

        int minutes = seconds / 60;
        seconds %= 60;

        int hours = minutes / 60;
        minutes %= 60;

        int days = hours / 24;
        hours %= 24;

        String timeString = formatTime(days, hours, minutes, seconds, milliseconds);

        this.timerText.setText(timeString);
    }

    private String formatTime(int days, int hours, int minutes, int seconds, int milliseconds) {
        StringBuilder timeBuilder = new StringBuilder();

        if (minutes > 0) {
            if (hours > 0) {
                if (days > 0) {
                    timeBuilder.append(days);
                    timeBuilder.append(":");
                    if (hours < 10)
                        timeBuilder.append("0");
                }

                timeBuilder.append(hours);
                timeBuilder.append(":");
                if (minutes < 10)
                    timeBuilder.append("0");
            }

            timeBuilder.append(minutes);
            timeBuilder.append(":");
            if (seconds < 10)
                timeBuilder.append("0");
        }

        timeBuilder.append(seconds);
        timeBuilder.append(".");

        int hundredths = (int) (milliseconds / 10);
        if (hundredths < 10)
            timeBuilder.append("0");
        timeBuilder.append(hundredths);

        return timeBuilder.toString();
    }

    private void resetTimer() {
        this.timerText.setText("0.00");
    }
}
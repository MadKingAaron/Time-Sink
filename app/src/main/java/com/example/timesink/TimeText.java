package com.example.timesink;

import android.widget.TextView;

public class TimeText
{
    private TextView text;
    private long time;
    private int format;

    public TimeText(TextView text, long time)
    {
        this(text, time, 0);
    }

    public TimeText(TextView text, long time, int format)
    {
        this.text = text;
        this.time = time;
        this.format = format;

        updateTime(this.time);
    }

    public void setFormat(int format) { this.format = format; }

    public void updateTime(long time)
    {
        this.time = time;
        this.text.setText(getTimeString(time, this.format));
    }

    public static String getTimeString(long millis) { return getTimeString(millis, 0); }

    public static String getTimeString(long millis, int format)
    {
        int seconds = (int) (millis / 1000);
        int milliseconds = (int) (millis % 1000);

        int minutes = seconds / 60;
        seconds %= 60;

        int hours = minutes / 60;
        minutes %= 60;

        int days = hours / 24;
        hours %= 24;

        switch (format)
        {
            case 0:
                return formatTime0(days, hours, minutes, seconds, milliseconds);
            case 1:
                return formatTime1(days, hours, minutes, seconds, milliseconds);
            default:
                return formatTime0(days, hours, minutes, seconds, milliseconds);
        }
    }

    private static String formatTime0(int days, int hours, int minutes, int seconds, int milliseconds)
    {
        StringBuilder timeBuilder = new StringBuilder();

        if (minutes > 0)
        {
            if (hours > 0)
            {
                if (days > 0)
                {
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

    private static String formatTime1(int days, int hours, int minutes, int seconds, int milliseconds)
    {
        StringBuilder timeBuilder = new StringBuilder();

        if (minutes > 0)
        {
            if (hours > 0)
            {
                if (days > 0)
                {
                    timeBuilder.append(days);
                    timeBuilder.append("d");
                    if (hours < 10)
                        timeBuilder.append("0");
                }

                timeBuilder.append(hours);
                timeBuilder.append("h");
                if (minutes < 10)
                    timeBuilder.append("0");
            }

            timeBuilder.append(minutes);
            timeBuilder.append("m");
            if (seconds < 10)
                timeBuilder.append("0");
        }

        timeBuilder.append(seconds);
        timeBuilder.append("s");
        timeBuilder.append(milliseconds);
        timeBuilder.append("ms");

        return timeBuilder.toString();
    }
}

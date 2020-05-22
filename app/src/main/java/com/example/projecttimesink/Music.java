package com.example.projecttimesink;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.Random;

import static android.nfc.NdefRecord.createUri;

public class Music
{
    private MediaPlayer musicMP;
    private boolean started;

    public Music()
    {
        createMP();
        this.started = false;
    }

    public void start()
    {
        if(!started)
        {
            this.musicMP.start();
            this.started = true;
        }
    }

    public void stop()
    {
        if(started)
            this.musicMP.release();
    }

    public void createMP()
    {
        Random r = new Random();
        int cap = 8;
        int result = r.nextInt(cap);
        switch (result) {
            case 0:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.bennytheme);
                break;
            case 1:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.kahoot);
                break;
            case 2:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.elevator);
                break;
            case 3:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.guile);
                break;
            case 4:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.rocky);
                break;
            case 5:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.rickroll);
                break;
            case 6:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.eye_of_the_tiger);
                break;
            case 7:
                musicMP = MediaPlayer.create(MyApplication.getAppContext(), R.raw.megalovator);
                break;
        }

        musicMP.setAudioStreamType(AudioManager.STREAM_MUSIC);

        musicMP.setLooping(true);
    }
}

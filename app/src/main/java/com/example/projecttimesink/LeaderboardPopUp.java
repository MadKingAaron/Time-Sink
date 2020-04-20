package com.example.projecttimesink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderboardPopUp extends AppCompatActivity
{
    TextView errorText;
    TimeText timeText;
    EditText nameInput;

    String name;
    long time;

    SwitchButton switchButton;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_pop_up);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null)
        {
            this.time = (long) bundle.getSerializable("time");
        }
        else
        {
            this.time = 0;
        }

        this.errorText = findViewById(R.id.errorText);

        this.timeText = new TimeText((TextView) findViewById(R.id.time), this.time, 1);

        this.nameInput = findViewById(R.id.nameText);

        this.switchButton = new SwitchButton(this, LeaderboardActivity.class, (Button) findViewById(R.id.enterButton));

        this.database = new Database();

        this.switchButton.addSwitchCondition(new Switchable()
        {
            @Override
            public boolean canSwitch()
            {
                name = nameInput.getText().toString();

                if(name == null || name.equals(""))
                {
                    errorText.setText("Enter Name To Continue");
                    return false;

                }

                // check if name is in database

                return true;
            }
        });

        this.switchButton.addDelay(new Delay()
        {
            @Override
            public void delay()
            {
                String name = nameInput.getText().toString();

//                Bundle bundle = new Bundle();
//
//                bundle.putSerializable("time", time);
//                bundle.putSerializable("username", name);

//                database.writeUser(name, time);

                switchButton.addFinish(new Finish()
                {
                    @Override
                    public void finish()
                    {
                        LeaderboardPopUp.this.finish();
                    }
                });
            }
        });
    }
}

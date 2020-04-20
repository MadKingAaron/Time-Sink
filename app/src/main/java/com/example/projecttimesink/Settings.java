package com.example.projecttimesink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class Settings extends AppCompatActivity
{
    private final String preUsername = "Username: ";

    String userID;
    TextView usernameText;
    EditText usernameEditText;
    Button usernameButton;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    Database database;

    SwitchButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.firebaseAuth = FirebaseAuth.getInstance();

        if(this.firebaseAuth.getCurrentUser() == null)
        {
            Intent nextIntent = new Intent(this, Register.class);
            startActivity(nextIntent);
            finish();
        }

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        this.userID = this.firebaseAuth.getCurrentUser().getUid();

        this.database = new Database();

        this.usernameText = (TextView) findViewById(R.id.usernameText);

        this.database.readUserContinuous(this.userID, new Database.OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    progressBar.setVisibility(View.GONE);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    usernameText.setVisibility(View.VISIBLE);
                    usernameText.setText(preUsername + username);
                }
            }

            @Override
            public void onStart()
            {

            }

            @Override
            public void onFailure()
            {

            }
        });

        this.usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        this.usernameButton = (Button) findViewById(R.id.usernameButton);

        this.usernameButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // update username
                String newUsername = usernameEditText.getText().toString();

                if(newUsername != null)
                {
                    database.updateUsername(userID, newUsername);

                    Toast.makeText(Settings.this, "Username Update Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.logoutButton = new SwitchButton(this, MainActivity.class, (Button) findViewById(R.id.logoutButton));

        this.logoutButton.addDelay(new Delay()
        {
            @Override
            public void delay()
            {
                logout();
            }
        });
    }

    private void logout()
    {
        FirebaseAuth.getInstance().signOut();
    }
}

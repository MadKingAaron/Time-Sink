package com.example.projecttimesink;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Online2 extends AppCompatActivity
{
    private Button firebaseButton;

    private DatabaseReference mDatabase;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online2);

        this.firebaseButton = findViewById(R.id.firebaseButton);

        // gets root of firebase database
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        this.firebaseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                somefunction();
            }
        });
    }

    private void somefunction()
    {
        writeNewUser("Joe", (long) 5);
    }

    private void writeNewUser(String userId, Long timeWasted)
    {
        User user = new User(userId, timeWasted);

        this.mDatabase.child("users").child(userId).setValue(user);
    }
}

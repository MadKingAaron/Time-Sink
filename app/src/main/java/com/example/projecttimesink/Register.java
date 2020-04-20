package com.example.projecttimesink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class Register extends AppCompatActivity
{
    TimeText timeText;
    TextView registerText;

    EditText usernameInput;
    EditText emailInput;
    EditText passwordInput;

    long time;
    String username;
    String email;
    String password;

    Button enterButton;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    private Database database;

    Long numOfUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        this.database = new Database();

        if(bundle != null)
            this.time = (long) bundle.getSerializable("time");
        else
            this.time = 0;

        if(this.time > 0)
        {
            this.registerText = (TextView) findViewById(R.id.time);
            this.timeText = new TimeText(this.registerText, this.time, 1);
            this.registerText.setVisibility(View.VISIBLE);
        }
        else
        {
            this.registerText = (TextView) findViewById(R.id.signIn);
            this.registerText.setVisibility(View.VISIBLE);
        }

        this.usernameInput = (EditText) findViewById(R.id.usernameText);
        this.emailInput = (EditText) findViewById(R.id.emailText);
        this.passwordInput = (EditText) findViewById(R.id.passwordText);

        this.enterButton = (Button) findViewById(R.id.enterButton);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        this.firebaseAuth = FirebaseAuth.getInstance();

        if(this.firebaseAuth.getCurrentUser() != null)
        {
            // switch activity
            String userID = firebaseAuth.getCurrentUser().getUid();
            database.updateUserPlacement(userID, time);

            Intent nextIntent = new Intent(this, LeaderboardActivity.class);
            startActivity(nextIntent);
            finish();
        }

        this.database.readUserData(new Database.OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                    numOfUsers = dataSnapshot.getChildrenCount();
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

        this.enterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                username = usernameInput.getText().toString();

//                if(TextUtils.isEmpty(username))
//                {
//                    usernameInput.setError("Enter Username To Continue");
//                    return;
//                }
//                else
//                {
//                    // TODO check if username is taken
//                }

                email = emailInput.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    emailInput.setError("Enter Email To Continue");
                    return;
                }

                password = passwordInput.getText().toString();

                if(password == null || password.length() < 6)
                {
                    passwordInput.setError("Password Must Be At Least 6 Letters");
                    return;
                }

                registerAccount();
            }
        });
    }

    private void registerAccount()
    {
        this.progressBar.setVisibility(View.VISIBLE);

        this.firebaseAuth.signInWithEmailAndPassword(this.email, this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Register.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    database.updateUserPlacement(userID, time);
                    if(!username.isEmpty())
                        database.updateUsername(userID, username);
                    startActivity(new Intent(Register.this, LeaderboardActivity.class));
                    Register.this.finish();
                }
                else
                {
                    String exceptionMessage = task.getException().getMessage();
                    String lowerCase = exceptionMessage.toLowerCase();

                    if(lowerCase.contains("password is invalid"))
                    {
                        Toast.makeText(Register.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    else if(lowerCase.contains("blocked all requests"))
                    {
                        Toast.makeText(Register.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        this.firebaseAuth.createUserWithEmailAndPassword(this.email, this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Register.this, "Account Creation Successful!", Toast.LENGTH_SHORT).show();
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    if(username.isEmpty())
                        username = getUsernameFromEmail(email);
                    database.createNewUser(userID, username, time, numOfUsers);
                    startActivity(new Intent(Register.this, LeaderboardActivity.class));
                    Register.this.finish();
                }
                else
                {
                    String exceptionMessage = task.getException().getMessage();
                    String lowerCase = exceptionMessage.toLowerCase();

                    if(!lowerCase.contains("email address is already in use"))
                    {
                        Toast.makeText(Register.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private String getUsernameFromEmail(String email)
    {
        return email.substring(0, email.indexOf("@"));
    }
}

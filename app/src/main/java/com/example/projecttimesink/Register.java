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
            this.usernameInput.setVisibility(View.GONE);
            this.emailInput.setVisibility(View.GONE);
            this.passwordInput.setVisibility(View.GONE);
            this.enterButton.setVisibility(View.GONE);
            this.progressBar.setVisibility(View.VISIBLE);

            String userID = firebaseAuth.getCurrentUser().getUid();
            database.updateUserPlacement(userID, time, new Database.CompleteStatus()
            {
                @Override
                public void onComplete()
                {
                    startActivity(new Intent(Register.this, LeaderboardActivity.class));
                    Register.this.finish();
                }
            });
        }

        this.enterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                username = usernameInput.getText().toString();

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
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    if(!username.isEmpty())
                        database.updateUsername(userID, username);
                    database.updateUserPlacement(userID, time, new Database.CompleteStatus()
                    {
                        @Override
                        public void onComplete()
                        {
                            Toast.makeText(Register.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, LeaderboardActivity.class));
                            Register.this.finish();
                        }
                    });
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
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    if(username.isEmpty())
                        username = getUsernameFromEmail(email);
                    database.createNewUser(userID, username, time, new Database.CompleteStatus()
                    {
                        @Override
                        public void onComplete()
                        {
                            Toast.makeText(Register.this, "Account Creation Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, LeaderboardActivity.class));
                            Register.this.finish();
                        }
                    });
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

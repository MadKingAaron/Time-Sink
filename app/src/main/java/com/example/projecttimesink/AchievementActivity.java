package com.example.projecttimesink;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;


public class AchievementActivity extends AppCompatActivity
{
    Database database;
    FirebaseAuth firebaseAuth;
    String userID;
    long time;

    ImageView achievementOne;
    ImageView achievementTwo;
    ImageView achievementThree;

    User user;

    //Instead of scrolling, maybe start with a list that doesn't move?

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        configureBackButton();

        this.time=0;
        this.database=new Database();
        this.firebaseAuth=FirebaseAuth.getInstance();
        this.userID=firebaseAuth.getCurrentUser().getUid();
        achievementOne=(ImageView) findViewById(R.id.greenOne);
        achievementTwo=(ImageView) findViewById(R.id.greenTwo);
        achievementThree=(ImageView) findViewById(R.id.greenThree);
        this.database.readUser(this.userID, new Database.OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                time=user.longestTimeWasted;
                Achievement achievement=new Achievement(time);
                int numberOfAchievementsUnlocked=achievement.checkLongestTimeWastedAchievements();
                setGreen(numberOfAchievementsUnlocked);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    private void configureBackButton()
    {
        Button backButton=(Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private void setGreen(int numberOfAchievementsUnlocked)
    {
        if(numberOfAchievementsUnlocked==1)
        {
            achievementOne.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==2)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==3)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
        }
    }
}

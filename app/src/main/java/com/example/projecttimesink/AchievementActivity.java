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
    ImageView achievementFour;
    ImageView achievementFive;
    ImageView achievementSix;
    ImageView achievementSeven;
    ImageView achievementEight;

    User user;

    //Instead of scrolling, maybe start with a list that doesn't move?

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        this.time=0;
        this.database=new Database();
        this.firebaseAuth=FirebaseAuth.getInstance();
        this.userID=firebaseAuth.getCurrentUser().getUid();
        achievementOne=(ImageView) findViewById(R.id.greenOne);
        achievementTwo=(ImageView) findViewById(R.id.greenTwo);
        achievementThree=(ImageView) findViewById(R.id.greenThree);
        achievementFour=(ImageView) findViewById(R.id.greenFour);
        achievementFive=(ImageView) findViewById(R.id.greenFive);
        achievementSix=(ImageView) findViewById(R.id.greenSix);
        achievementSeven=(ImageView) findViewById(R.id.greenSeven);
        achievementEight=(ImageView) findViewById(R.id.greenEight);
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
        else if(numberOfAchievementsUnlocked==4)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
            achievementFour.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==5)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
            achievementFour.setVisibility(View.VISIBLE);
            achievementFive.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==6)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
            achievementFour.setVisibility(View.VISIBLE);
            achievementFive.setVisibility(View.VISIBLE);
            achievementSix.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==7)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
            achievementFour.setVisibility(View.VISIBLE);
            achievementFive.setVisibility(View.VISIBLE);
            achievementSix.setVisibility(View.VISIBLE);
            achievementSeven.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==8)
        {
            achievementOne.setVisibility(View.VISIBLE);
            achievementTwo.setVisibility(View.VISIBLE);
            achievementThree.setVisibility(View.VISIBLE);
            achievementFour.setVisibility(View.VISIBLE);
            achievementFive.setVisibility(View.VISIBLE);
            achievementSix.setVisibility(View.VISIBLE);
            achievementSeven.setVisibility(View.VISIBLE);
            achievementEight.setVisibility(View.VISIBLE);
        }
    }
}

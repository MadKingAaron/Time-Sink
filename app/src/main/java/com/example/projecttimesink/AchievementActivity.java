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
    long longestTimeWasted=0;

    ImageView achievementOne;
    ImageView achievementTwo;
    ImageView achievementThree;

    //Instead of scrolling, maybe start with a list that doesn't move?

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        configureBackButton();

        this.database=new Database();
        this.userID=firebaseAuth.getCurrentUser().getUid();
        this.database.readUser(userID, new Database.OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                longestTimeWasted=user.longestTimeWasted;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
        Achievement achievement=new Achievement(longestTimeWasted);
        int numberOfAchievementsUnlocked=achievement.checkLongestTimeWastedAchievements();

        this.achievementOne=(ImageView) findViewById(R.id.greenOne);
        this.achievementTwo=(ImageView) findViewById(R.id.greenTwo);
        this.achievementThree=(ImageView) findViewById(R.id.greenThree);

        if(numberOfAchievementsUnlocked==1)
        {
            this.achievementOne.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==2)
        {
            this.achievementOne.setVisibility(View.VISIBLE);
            this.achievementTwo.setVisibility(View.VISIBLE);
        }
        else if(numberOfAchievementsUnlocked==3)
        {
            this.achievementOne.setVisibility(View.VISIBLE);
            this.achievementTwo.setVisibility(View.VISIBLE);
            this.achievementThree.setVisibility(View.VISIBLE);
        }
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

}

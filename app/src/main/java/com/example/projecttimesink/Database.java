package com.example.projecttimesink;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database
{
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<User> users = new ArrayList<>();

    public Database() { this(null); }

    public Database(String path)
    {
        this.database = FirebaseDatabase.getInstance();

        if(path != null)
            this.reference = this.database.getReference(path);
        else
            this.reference = this.database.getReference();
    }

    public interface DataStatus
    {
        void DataIsLoaded(ArrayList<User> users, ArrayList<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public void writeUser(String userId, Long timeWasted)
    {
//        readUsers(new DataStatus()
//        {
//            @Override
//            public void DataIsLoaded(ArrayList<User> users, ArrayList<String> keys) {}
//
//            @Override
//            public void DataIsInserted() {}
//
//            @Override
//            public void DataIsUpdated() { }
//
//            @Override
//            public void DataIsDeleted() { }
//        });

        User user = new User(userId, timeWasted);

        // make it use placement

        this.reference.child("users").child(userId).setValue(user);
    }

    private void updatePlacement()
    {

    }

    public void readUser(String userId)
    {
        DatabaseReference reference = this.reference.child("users").child(userId);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);

                System.out.println(user.username + " wasted " + user.timeWasted + " milliseconds");
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
            }
        };

        reference.addValueEventListener(postListener);
    }

    public void readUsers(final DataStatus dataStatus)
    {
        DatabaseReference reference = this.reference.child("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                users.clear();

                ArrayList<String> keys = new ArrayList<>();

                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }

                dataStatus.DataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
            }
        };

        reference.addValueEventListener(postListener);
    }
}

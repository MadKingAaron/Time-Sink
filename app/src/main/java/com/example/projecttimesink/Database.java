package com.example.projecttimesink;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database
{
    FirebaseDatabase database;
    DatabaseReference baseReference;
    DatabaseReference usersReference;
    DatabaseReference leaderboardReference;

    private static final int MAX_LEADERBOARD_USERS = 100;
    ArrayList<String> leaderboardUserIDs = new ArrayList<>();

    public Database()
    {
        this.database = FirebaseDatabase.getInstance();

        this.baseReference = this.database.getReference();
        this.usersReference = this.baseReference.child("users");
        this.leaderboardReference = this.baseReference.child("leaderboard");
    }

    public interface UserIDDataStatus
    {
        void DataIsLoaded(ArrayList<String> userIDs, ArrayList<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public interface UserDataStatus
    {
        void DataIsLoaded(User[] users, String[] keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public void createNewUser(final String userId, final String username, final Long timeWasted)
    {
        readNumOfUsers(new OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                Long placement = dataSnapshot.getChildrenCount() + 1;

                User newUser = new User(username, timeWasted);

                addUserToLeaderboard(userId, newUser, placement);

                usersReference.child(userId).setValue(newUser);
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
    }

    public void updateUserPlacement(final String userID, final Long timeWasted)
    {
        readUser(userID, new OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                final User user = dataSnapshot.getValue(User.class);

                if(user.updateTimeWasted(timeWasted))
                {
                    readPlacement(new OnGetDataListener()
                    {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot)
                        {
                            Long placement = null;

                            for(DataSnapshot keyNode : dataSnapshot.getChildren())
                            {
                                String currentUserID = keyNode.getValue(String.class);

                                if(currentUserID.equals(userID))
                                {
                                    placement = Long.parseLong(keyNode.getKey());
                                    break;
                                }
                            }

                            if(placement == null)
                                placement = new Long(0);

                            updatePlacement(userID, user, placement);
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
                }

                usersReference.child(userID).setValue(user);
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
    }

    public void updateUsername(final String userId, final String username)
    {
        this.usersReference.child(userId).child("username").setValue(username);
    }

    private void addUserToLeaderboard(String userID, User currentUser, Long placement)
    {
        this.leaderboardReference.child(placement.toString()).setValue(userID);

        updatePlacement(userID, currentUser, placement);
    }

    private void readPlacement(final OnGetDataListener listener)
    {
        listener.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                listener.onFailure();
            }
        };

        this.leaderboardReference.addListenerForSingleValueEvent(postListener);
    }

    private void updatePlacement(final String userID, final User currentUser, final long placement)
    {
        final Long priorPlacement = placement - 1;

        if(priorPlacement > 0)
        {
            readUserAtPlace(priorPlacement, new OnGetDataListener()
            {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot)
                {
                    final String priorUserID = dataSnapshot.getValue(String.class);

                    if(priorUserID != null)
                    {
                        // read user
                        readUser(priorUserID, new OnGetDataListener()
                        {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot)
                            {
                                User priorUser = dataSnapshot.getValue(User.class);

                                if(priorUser == null || priorUser.longestTimeWasted == null)
                                {
                                    leaderboardReference.child(priorPlacement.toString()).setValue(userID);
                                }
                                else if(priorUser.longestTimeWasted < currentUser.longestTimeWasted)
                                {
                                    Long placement = priorPlacement;

                                    leaderboardReference.child(priorPlacement.toString()).setValue(userID);

                                    placement++;

                                    leaderboardReference.child(placement.toString()).setValue(priorUserID);
                                }

                                updatePlacement(userID, currentUser, priorPlacement);
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
        }
    }

    private void readNumOfUsers(final OnGetDataListener listener)
    {
        listener.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                listener.onFailure();
            }
        };

        this.leaderboardReference.addListenerForSingleValueEvent(postListener);
    }

    public void readUserData(final OnGetDataListener listener)
    {
        listener.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
                listener.onFailure();
            }
        };

        this.usersReference.addValueEventListener(postListener);
    }

    public interface OnGetDataListener
    {
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    // only reads user data once initially

    public void readUser(String userID, final OnGetDataListener listener)
    {
        listener.onStart();

        DatabaseReference reference = this.usersReference.child(userID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                listener.onFailure();
            }
        };

        reference.addListenerForSingleValueEvent(postListener);
    }

    public void readUserContinuous(String userID, final OnGetDataListener listener)
    {
        listener.onStart();

        DatabaseReference reference = this.usersReference.child(userID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                listener.onFailure();
            }
        };

        reference.addValueEventListener(postListener);
    }

    private void readUserAtPlace(Long place, final OnGetDataListener listener)
    {
        listener.onStart();

        String placement = place.toString();
        DatabaseReference reference = this.leaderboardReference.child(placement);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
                listener.onFailure();
            }
        };

        reference.addListenerForSingleValueEvent(postListener);
    }

    public void readLeaderboardIDs(final UserIDDataStatus dataStatus)
    {
        ValueEventListener leaderboardPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                leaderboardUserIDs.clear();

                int numOfUsers = 0;

                ArrayList<String> keys = new ArrayList<>();

                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    if(numOfUsers >= MAX_LEADERBOARD_USERS)
                        break;

                    keys.add(keyNode.getKey());

                    String userID = keyNode.getValue(String.class);

                    leaderboardUserIDs.add(userID);

                    numOfUsers++;
                }

                dataStatus.DataIsLoaded(leaderboardUserIDs, keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
            }
        };

        this.leaderboardReference.addValueEventListener(leaderboardPostListener);
    }

    public void readUsers(final ArrayList<String> userIDs, final UserDataStatus dataStatus)
    {
        ValueEventListener leaderboardPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int numOfUsers = userIDs.size();

                User[] users = new User[numOfUsers];
                String[] keys = new String[numOfUsers];

                for(DataSnapshot keyNode : dataSnapshot.getChildren())
                {
                    String key = keyNode.getKey();

                    for(int i = 0; i < userIDs.size(); i++)
                    {
                        if(key.equals(userIDs.get(i)))
                        {
                            User user = keyNode.getValue(User.class);
                            users[i] = user;
                            keys[i] = keyNode.getKey();
                        }
                    }
                }

                dataStatus.DataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w("CANCELLED", "loadPost:onCancelled", databaseError.toException());
            }
        };

        this.usersReference.addValueEventListener(leaderboardPostListener);
    }
}

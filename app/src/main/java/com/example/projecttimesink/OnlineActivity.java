package com.example.projecttimesink;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.analytics.FirebaseAnalytics;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class OnlineActivity extends AppCompatActivity
{
//    ListView loginUsers;
//    ArrayList<String> loginUserList = new ArrayList<>();
//    ArrayAdapter adapter;
//
//    ListView requestedUsers;
//    ArrayList<String> requestedUserList = new ArrayList<>();
//    ArrayAdapter requestAdapter;
//
//    TextView userID, sendRequest, acceptRequest;
//    String loginUserID, userName, loginUID;
//
//    private FirebaseAnalytics mFirebaseAnalytics;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference reference = database.getReference();
//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_online);
//
//        sendRequest = (TextView) findViewById(R.id.send_request);
//        acceptRequest = (TextView) findViewById(R.id.accept_request);
//
//        sendRequest.setText("Please wait...");
//        acceptRequest.setText("Please wait...");
//
//        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        loginUsers = (ListView) findViewById(R.id.login_users);
//        adapter = new ArrayAdapter(this, R.layout.activity_online, loginUserList);
//        loginUsers.setAdapter(adapter);
//
//        requestedUsers = (ListView) findViewById(R.id.requested_users);
//        requestAdapter = new ArrayAdapter(this, R.layout.activity_online, requestedUserList);
//        requestedUsers.setAdapter(requestAdapter);
//
////        userID = (TextView) findViewById(R.id.login_user);
//
////        mAuthListener = new FirebaseAuth.AuthStateListener() {
////            @Override
////            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////                FirebaseUser user = firebaseAuth.getCurrentUser();
////                if(user != null) {
////                    // User is signed in
////                    loginUID = user.getUid();
////                    Log.d("AUTH", "onAuthStateChanged:signed_in:" + loginUID);
////                    loginUserID = user.getEmail();
//////                    userID.setText();
//////                    userName
////                } else {
////                    // User is signed out
////                    Log.d("AUTH", "onAuthStateChanged:signed_out");
////                }
////            }
////        }
//    }
//
//    private void createAccount(String email, String password)
//    {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("Auth", "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
////                        updateUI(user);
//                        } else
//                        {
//                            // If sign in fails, display a message to the user.
//                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                        updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//
//    private void signIn(String email, String password)
//    {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("Auth", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
////                        updateUI(user);
//                        } else
//                        {
//                            // If sign in fails, display a message to the user.
//                            Log.w("Auth", "signInWithEmail:failure", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                        updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//
//    private String convertEmailToString(String email)
//    {
//        String value = email.substring(0, email.indexOf('@'));
//        value = value.replace(".", "");
//        return value;
//    }
//
//    private void acceptIncommingRequests()
//    {
//        reference.child("users").child(userName).child("request")
//            .addValueEventListener(new ValueEventListener()
//            {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                {
//                    try
//                    {
//                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                        if (map != null)
//                        {
//                            String value = "";
//                            for (String key : map.keySet())
//                            {
//                                value = (String) map.get(key);
//                                requestAdapter.add(convertEmailToString(value));
//                                requestAdapter.notifyDataSetChanged();
//                                reference.child("users").child(userName).child("request").setValue(loginUID);
//                            }
//                        }
//                    } catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError)
//                {
//
//                }
//            });
//    }
}

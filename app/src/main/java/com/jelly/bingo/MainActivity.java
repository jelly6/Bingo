package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        super.onStop();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();
        if(auth.getCurrentUser() != null){
            Log.d(TAG, "onAuthStateChanged: "+auth.getCurrentUser());
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("displayName")
                    .setValue(user.getDisplayName());
        }else{
            Log.d(TAG, "onAuthStateChanged: ");
            signUp();

        }
    }

    private void signUp() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
        ))
        //.setIsSmartLockEnabled(false)
        .build(),RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        return super.onOptionsItemSelected(item);
    }
}
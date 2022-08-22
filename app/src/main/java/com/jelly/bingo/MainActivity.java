package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

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
        if(auth.getCurrentUser() != null){
            Log.d(TAG, "onAuthStateChanged: "+auth.getCurrentUser());

        }else{
            Log.d(TAG, "onAuthStateChanged: ");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.AnonymousBuilder().build()
            ))
            //.setIsSmartLockEnabled(false)
            .build(),RC_SIGN_IN);

        }
    }
}
package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private TextView tv_nickname;
    private ImageView iv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        findViews();

    }

    private void findViews() {
        tv_nickname = findViewById(R.id.nickname);
        iv_avatar = findViewById(R.id.avatar);
    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        auth.removeAuthStateListener(this);
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

            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Member member = snapshot.getValue(Member.class);
                            if(member !=null){
                                if(member.getNickName()!=null){
                                    tv_nickname.setText(member.getNickName());
                                }else{
                                    showNicknameDialog(auth.getCurrentUser().getDisplayName());

                                }
                            }else{
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            /*FirebaseDatabase.getInstance().getReference("user")
                    .child(user.getUid())
                    .child("nickName")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()!=null){
                                Log.d(TAG, "onDataChange: "+snapshot.getValue().toString());

                            }else{
                                showNicknameDialog(user.getDisplayName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/
        }else{
            Log.d(TAG, "onAuthStateChanged: ");
            signUp();

        }
    }

    private void showNicknameDialog(String displayName) {
        EditText editNickname = new EditText(this);
        editNickname.setText(displayName);
        new AlertDialog.Builder(this)
                .setTitle("Nickname")
                .setMessage("Please enter your nickname:")
                .setView(editNickname)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(auth.getUid())
                                .child("nickName")
                                .setValue(editNickname.getText().toString());
                    }
                })
                .show();

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
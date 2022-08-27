package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private TextView tv_nickname;
    private ImageView iv_avatar;
    private Member member;
    int[] groupIds = new int[]{R.id.avatar_0,R.id.avatar_1,R.id.avatar_2,R.id.avatar_3,R.id.avatar_4,R.id.avatar_5,R.id.avatar_6};
    int[] avatarsIds = new int[]{R.drawable.avatar_0,R.drawable.avatar_1,R.drawable.avatar_2,R.drawable.avatar_3,R.drawable.avatar_4,R.drawable.avatar_5,R.drawable.avatar_6};
    private Group group_avarars;
    private ImageView avatar;

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
        tv_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNicknameDialog(member.getNickName());
            }
        });
        group_avarars = findViewById(R.id.group_avatars);
        avatar = findViewById(R.id.avatar);
        group_avarars.setVisibility(View.GONE);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group_avarars.getVisibility() == View.GONE){
                    group_avarars.setVisibility(View.VISIBLE);
                }else{
                    group_avarars.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.avatar_0).setOnClickListener(this);
        findViewById(R.id.avatar_1).setOnClickListener(this);
        findViewById(R.id.avatar_2).setOnClickListener(this);
        findViewById(R.id.avatar_3).setOnClickListener(this);
        findViewById(R.id.avatar_4).setOnClickListener(this);
        findViewById(R.id.avatar_5).setOnClickListener(this);
        findViewById(R.id.avatar_6).setOnClickListener(this);

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
            /*FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("displayName")
                    .setValue(user.getDisplayName());
            */
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            member = snapshot.getValue(Member.class);
                            if(member !=null){
                                if(member.getNickName()!=null){
                                    tv_nickname.setText(member.getNickName());
                                }else{
                                    showNicknameDialog(auth.getCurrentUser().getDisplayName());

                                }
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(auth.getUid())
                                        .child("uid")
                                        .setValue(auth.getUid());
                                avatar.setImageResource(avatarsIds[member.getAvatarId()]);

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

    @Override
    public void onClick(View view) {
        int selected = 0;
        switch(view.getId()){
            case R.id.avatar_0:
                selected = 0;
                break;
            case R.id.avatar_1:
                selected = 1;
                break;
            case R.id.avatar_2:
                selected = 2;
                break;
            case R.id.avatar_3:
                selected = 3;
                break;
            case R.id.avatar_4:
                selected = 4;
                break;
            case R.id.avatar_5:
                selected = 5;
                break;
            case R.id.avatar_6:
                selected = 6;
                break;
        }
        member.setAvatarId(selected);
        FirebaseDatabase.getInstance().getReference("users")
                .child(auth.getUid())
                .child("avatarId")
                .setValue(selected);
        group_avarars.setVisibility(View.GONE);
    }
}
package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private TextView nickText;
    private ImageView avatarImg;
    private Member member;
    int[] avatarsIds = new int[]{R.drawable.avatar_0,R.drawable.avatar_1,R.drawable.avatar_2,R.drawable.avatar_3,R.drawable.avatar_4,R.drawable.avatar_5,R.drawable.avatar_6};
    private Group avararGroup;
    private ImageView avatar;
    private FloatingActionButton fab;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        findViews();

    }

    private void findViews() {
        nickText = findViewById(R.id.nickname);
        avatarImg = findViewById(R.id.avatar);
        nickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNicknameDialog(member.getNickName());
            }
        });
        avararGroup = findViewById(R.id.group_avatars);
        avararGroup.setVisibility(View.GONE);
        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(avararGroup.getVisibility() == View.GONE){
                    avararGroup.setVisibility(View.VISIBLE);
                }else{
                    avararGroup.setVisibility(View.GONE);
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

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showＲoomDialog();
            }
        });
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference("rooms")
                .limitToLast(20);
        FirebaseRecyclerOptions<GameRoom> options = new FirebaseRecyclerOptions.Builder<GameRoom>()
                .setQuery(query,GameRoom.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<GameRoom, RoomHolder>(options) {
            @NonNull
            @Override
            public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.room_layout,parent,false);
                return new RoomHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RoomHolder holder, int position, @NonNull GameRoom model) {
                holder.roomTitleText.setText(model.getTitle());
                holder.roomAvatarImg.setImageResource(avatarsIds[model.getInit().getAvatarId()]);
            }
        };
        recycler.setAdapter(adapter);



    }

    public class RoomHolder extends RecyclerView.ViewHolder{
        ImageView roomAvatarImg;
        TextView roomTitleText;
        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            roomAvatarImg = itemView.findViewById(R.id.room_avatar);
            roomTitleText = itemView.findViewById(R.id.room_title);
        }
    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(this);
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        auth.removeAuthStateListener(this);
        super.onStop();
        adapter.stopListening();
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
                                    nickText.setText(member.getNickName());
                                }else{
                                    showNicknameDialog(auth.getCurrentUser().getDisplayName());

                                }
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(auth.getUid())
                                        .child("uid")
                                        .setValue(auth.getUid());
                                avatarImg.setImageResource(avatarsIds[member.getAvatarId()]);

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
    private void showＲoomDialog() {
        EditText editRoomTitle = new EditText(this);
        editRoomTitle.setText("Welcome");
        new AlertDialog.Builder(this)
                .setTitle("Game Room")
                .setMessage("Please enter your room title:")
                .setView(editRoomTitle)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GameRoom room = new GameRoom(editRoomTitle.getText().toString(),member);
                        FirebaseDatabase.getInstance().getReference("rooms")
                                .push()
                                .setValue(room, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Log.d(TAG, "onComplete: "+error+'/'+ref.getKey());
                                        if(error==null){
                                            Intent intent = new Intent(MainActivity.this, BingoActivity.class);
                                            intent.putExtra("ROOM_ID",ref.getKey());
                                            intent.putExtra("IS_CREATOR",true);
                                            startActivity(intent);
                                        }
                                    }
                                });

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
        avararGroup.setVisibility(View.GONE);
    }
}
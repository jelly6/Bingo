package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_JOINED = 2;
    public static final int STATUS_CREATED_TURN = 3;
    public static final int STATUS_JOINED_TURN = 4;
    public static final int STATUS_CREATED_DONE = 5;
    public static final int STATUS_JOIN_DONE = 6;
    int BINGO_TARGET = 4;
    boolean isMyTurn = false;
    public String TAG = BingoActivity.class.getSimpleName();
    private FirebaseRecyclerAdapter<Boolean, BallHolder> adapter;
    private TextView info;
    private RecyclerView recycler;
    private String roomId;
    private boolean isCreator;
    private List<NumberButton> buttons;
    private TextView lineInfo;

    ValueEventListener stateListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            long status = (long) snapshot.getValue();
            switch ((int) status){
                case STATUS_CREATED:
                    info.setText("Wait for the join..");
                    break;
                case STATUS_JOINED:

                    break;
                case STATUS_CREATED_TURN:
                    isMyTurn = isCreator? true:false;
                    info.setText(isCreator? "Please select a number..":"Wait for other's selection..");
                    break;
                case STATUS_JOINED_TURN:
                    isMyTurn = !isCreator? true:false;
                    info.setText(!isCreator? "Please select a number..":"Wait for other's selection..");

                    break;
                case STATUS_CREATED_DONE:
                    isMyTurn = false;
                    if(!isCreator){
                        isBingo = check_bingo(BINGO_TARGET);

                    }
                    new AlertDialog.Builder(BingoActivity.this)
                            .setTitle("Bingo~!")
                            .setMessage((isCreator || isBingo)? "You win the game..":"You lose the game..")
                            .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    endGame();
                                }
                            }).show();
                    break;
                case STATUS_JOIN_DONE:
                    isMyTurn = false;
                    if(isCreator){
                        isBingo = check_bingo(BINGO_TARGET);

                    }
                    new AlertDialog.Builder(BingoActivity.this)
                            .setTitle("Bingo~!")
                            .setMessage((!isCreator || isBingo) ? "You win the game..":"You lose the game..")
                            .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    endGame();
                                }
                            }).show();
                break;
            }

//            if (isMyTurn) {
//                info.setText("Please select a Number..");
//            } else {
//                info.setText("Wait for other's selection");
//            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private boolean isBingo;

    private void endGame() {
        //TODO:
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        roomId = getIntent().getStringExtra("ROOM_ID");
        isCreator = getIntent().getBooleanExtra("IS_CREATOR", false);
        Log.d(TAG, "onCreate: " + roomId + "/" + isCreator);
        findViews();

        if (isCreator) {
            for (int i = 0; i < 25; i++) {
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomId)
                        .child("numbers")
                        .child(String.valueOf(i + 1))
                        .setValue(false);
            }
            //isMyTurn = true;
            info.setText("Waiting for the join..");
        }else{
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("status")
                    .setValue(STATUS_JOINED);
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("status")
                    .setValue(STATUS_CREATED_TURN);
            info.setText("Waiting for other's selection..");

        }



        // find pos by number
        Map<Integer, Integer> numberMap = new HashMap<Integer, Integer>();
        buttons = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            NumberButton button = new NumberButton(this);
            button.setNumber(i+1);
            buttons.add(button);
        }
        Collections.shuffle(buttons);
        for (int i = 0; i < 25; i++) {
            numberMap.put(buttons.get(i).getNumber(),i);
            buttons.get(i).setPos(i);
        }

        // recyclerView for numbers

        Query query = FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("numbers")
                .orderByKey();
        FirebaseRecyclerOptions<Boolean> options = new FirebaseRecyclerOptions.Builder<Boolean>()
                .setQuery(query, Boolean.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Boolean, BallHolder>(options) {
            @NonNull
            @Override
            public BallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(BingoActivity.this).inflate(R.layout.single_button,parent,false);
                return new BallHolder(view);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                //Log.d(TAG, "onChildChanged: "+type+"/"+snapshot.getKey());
                if(type== ChangeEventType.CHANGED){
                    int number = Integer.parseInt(snapshot.getKey());
                    boolean isPicked = snapshot.getValue(Boolean.class);
                    int pos = numberMap.get(number);
                    Log.d(TAG, "onChildChanged: "+pos);
                    buttons.get(pos).setPicked(isPicked);
                    BallHolder holder= (BallHolder) recycler.findViewHolderForAdapterPosition(pos);
                    holder.button.setEnabled(!isPicked);

                }
                boolean isBingo = check_bingo(BINGO_TARGET);

                if(isBingo){
                    FirebaseDatabase.getInstance().getReference("rooms")
                            .child(roomId)
                            .child("status")
                            .setValue(isCreator? STATUS_CREATED_DONE:STATUS_JOIN_DONE);

                }





            }

            @Override//
            protected void onBindViewHolder(@NonNull BallHolder holder, int position, @NonNull Boolean model) {
                Log.d(TAG, "onBindViewHolder: "+position);
                //holder.button.setEnabled(!model);
                holder.button.setText(String.valueOf(buttons.get(position).getNumber()));
                holder.button.setNumber(buttons.get(position).getNumber());
                holder.button.setEnabled(!buttons.get(position).isPicked());
                holder.button.setOnClickListener(BingoActivity.this);

            }
        };
        recycler.setAdapter(adapter);

    }

    private boolean check_bingo(int target) {
        int count = 0;
        int[] bingo = new int[25];
        for (int i = 0; i < 25; i++) {
            bingo[i] = buttons.get(i).isPicked() ? 1 : 0 ;
        }
        for (int i = 0; i < 5; i++) {
            int sum = 0;
            for (int j = 0; j < 5; j++) {
                sum += bingo[j+5*i];
            }
            if(sum>=5){
                count +=1;
            }
            sum = 0;
            for (int j = 0; j < 5; j++) {
                sum += bingo[i+5*j];
            }
            if(sum>=5){
                count +=1;
            }
        }
        lineInfo.setText(String.valueOf(count));
        if(count>=target){
            return true;
        }else{
            return false;
        }
    }

    private void findViews() {
        info = findViewById(R.id.info);
        recycler = findViewById(R.id.game_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(this, 5));
        lineInfo = findViewById(R.id.lineInfo);
        lineInfo.setText(String.valueOf(0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("status")
                .addValueEventListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View view) {
        NumberButton button = (NumberButton) view;
        Log.d(TAG, "onClick: button "+button.getNumber()+"/ "+button.getPos());
        //button.setEnabled(button.isPicked());
        if(isMyTurn){
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("numbers")
                    .child(String.valueOf(button.getNumber()))
                    .setValue(true);
            int status = isCreator? STATUS_JOINED_TURN:STATUS_CREATED_TURN;
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("status")
                    .setValue(status);


        }

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    class BallHolder extends RecyclerView.ViewHolder {

        NumberButton button;

        public BallHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }


}

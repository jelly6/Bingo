package com.jelly.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoActivity extends AppCompatActivity {
    public String TAG = BingoActivity.class.getSimpleName();
    private FirebaseRecyclerAdapter<Boolean, BallHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        String roomId = getIntent().getStringExtra("ROOM_ID");
        boolean isCreator = getIntent().getBooleanExtra("IS_CREATOR", false);
        Log.d(TAG, "onCreate: " + roomId + "/" + isCreator);

        if (isCreator) {
            for (int i = 0; i < 25; i++) {
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomId)
                        .child("numbers")
                        .child(String.valueOf(i + 1))
                        .setValue(false);
            }
        }
        List<NumberButton> buttons = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            NumberButton button = new NumberButton(this);
            button.setNumber(i+1);
            buttons.add(button);
        }
        Collections.shuffle(buttons);

        RecyclerView recycler = findViewById(R.id.game_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(this, 5));

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
            protected void onBindViewHolder(@NonNull BallHolder holder, int position, @NonNull Boolean model) {
                Log.d(TAG, "onBindViewHolder: "+position);
                holder.button.setEnabled(!model);
                holder.button.setText(String.valueOf(buttons.get(position).getNumber()));
            }
        };
        recycler.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    class BallHolder extends RecyclerView.ViewHolder {

        NumberButton button;

        public BallHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }
}

package com.jelly.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class BingoActivity extends AppCompatActivity {
    public String TAG = BingoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        String roomId = getIntent().getStringExtra("ROOM_ID");
        boolean isCreator = getIntent().getBooleanExtra("IS_CREATOR",false);
        Log.d(TAG, "onCreate: "+roomId+"/"+isCreator);
    }
}
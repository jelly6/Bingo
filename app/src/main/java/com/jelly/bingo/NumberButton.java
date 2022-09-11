package com.jelly.bingo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton {
    int number;
    boolean picked;
    int pos;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public NumberButton(@NonNull Context context) {
        super(context);
    }

    public NumberButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}

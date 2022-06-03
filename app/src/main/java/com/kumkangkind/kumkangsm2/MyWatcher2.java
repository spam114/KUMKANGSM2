package com.kumkangkind.kumkangsm2;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;

public class MyWatcher2 implements TextWatcher {
    //할인율 변경
    private android.widget.Button btnNext;

    public MyWatcher2(android.widget.Button btnNext) {
        this.btnNext = btnNext;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnNext.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
    }


    @Override
    public void afterTextChanged(Editable s) {
    }
}
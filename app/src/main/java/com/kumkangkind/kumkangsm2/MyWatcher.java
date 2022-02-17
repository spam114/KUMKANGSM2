package com.kumkangkind.kumkangsm2;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class MyWatcher implements TextWatcher {
    //수량 변경
    private TextInputEditText edit;
    private Dong item;

    public MyWatcher(TextInputEditText edit) {
        this.edit = edit;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Log.d("TAG", "onTextChanged: " + s);
        this.item = (Dong) edit.getTag();
        if (item != null) {
            if (!s.toString().equals(item.ProgressFloor)) {
                //row.setBackgroundColor(Color.YELLOW);
                item.isChanged = true;
            } else {
                item.isChanged = false;
            }
        }
    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}
package com.kumkangkind.kumkangsm2;

import android.support.v7.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {


    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

}
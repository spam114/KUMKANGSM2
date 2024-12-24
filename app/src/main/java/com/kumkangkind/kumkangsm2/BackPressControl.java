package com.kumkangkind.kumkangsm2;

//Activity 추가

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BackPressControl {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;


    public BackPressControl(Activity context) {
        this.activity = context;
    }


    public void onBackPressed() {

        long num=System.currentTimeMillis();

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        } else {
            ActivityCompat.finishAffinity(activity);
            System.exit(0);
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, Users.Language == 0 ? "뒤로가기 버튼을 한 번 더 누르면 종료됩니다." : "Press the Back button one more time to exit.", Toast.LENGTH_SHORT);
        toast.show();
    }
}

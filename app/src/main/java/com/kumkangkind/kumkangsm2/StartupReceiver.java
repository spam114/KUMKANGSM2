package com.kumkangkind.kumkangsm2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StartupReceiver extends BroadcastReceiver {

    public StartupReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SharedPreferences noticePref;
        noticePref=context.getSharedPreferences("NoticePref",0);

        SharedPreferences.Editor editor = noticePref.edit();
        editor.putBoolean("viewNotice", true);
        editor.commit();
        //앱 변경 시, 무조건 공지를 보게 설정

        //context.startActivity(i); //자동 실행 중지
    }
}
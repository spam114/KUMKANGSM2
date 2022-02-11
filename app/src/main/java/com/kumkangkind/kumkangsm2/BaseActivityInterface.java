package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.os.Handler;

public interface BaseActivityInterface{
    void progressON();

    void progressON(String message);

    void progressON(String message, Handler handler);

    void progressOFF(String className);

    void progressOFF2(String className);

    void progressOFF();

    public void HideKeyBoard(Context context);
}

package com.kumkangkind.kumkangsm2.fcm;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;


public class MyInstanceIDListenerService extends FirebaseMessagingService {//토큰(Instance Id)를 가 변할때 갱신

         private static final String TAG = "MyInstanceIDLS";
                     /**
           * Called if InstanceID token is updated. This may occur if the security of
           * the previous token had been compromised. This call is initiated by the
           * InstanceID provider.
           */
                 // [START refresh_token]
                /* @Override
         public void onTokenRefresh() {
                 // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
                 Intent intent = new Intent(this, RegistrationIntentService.class);
                 startService(intent);
             }*/

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}

package com.kumkangkind.kumkangsm2.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kumkangkind.kumkangsm2.MainActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.sqlite.DbOpenHelper;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static PowerManager.WakeLock sCpuWakeLock;

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){


       SaveDB(remoteMessage);
       sendNotification(remoteMessage);

    }

    private void sendNotification(RemoteMessage remoteMessage){

        String str= remoteMessage.getMessageId();
        String str2= remoteMessage.getTo();
        String channelId = getString(R.string.default_notification_channel_id);
        Object cNo =remoteMessage.getData().get("certificateNo");
        String certificateNo="";
        if(cNo!=null)
            certificateNo=cNo.toString();

       /* Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("certificateNo",certificateNo);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.ic_stat_, 1)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setNumber(BadgeControl.badgeCnt + 1)
                .setVibrate(new long[]{0,2000});


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(55, notificationBuilder.build());
        BadgeControl.addBadgeCount(this);
    }

    /**
     * DB 저장
     * @param
     */
    private void SaveDB(RemoteMessage remoteMessage)
    {
        String certificateNo="";
        String msg1 = remoteMessage.getData().get("message");
        String msg2 = remoteMessage.getData().get("time");
        String msg3 = remoteMessage.getData().get("title");

        Object cNo = remoteMessage.getData().get("certificateNo");
        if(cNo!=null)
            certificateNo=cNo.toString();

        if(msg1 == "" ||msg2 =="")
            return;

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(msg3, msg1, msg2, certificateNo);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();

        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hi");

        sCpuWakeLock.acquire();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

}

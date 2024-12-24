package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


/**
 * App을 실행할 경우 처음 실행되는 액티비티이다.
 */
public class MainActivity extends BaseActivity {

    SharedPreferences _pref;
    Boolean isShortcut = false;//아이콘의 생성
    String certificateNo = "";

    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 10000);
        progressON("Loading...", handler);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startProgress();

        Object cNo = getIntent().getStringExtra("certificateNo");
        certificateNo = "";
        if (cNo != null)
            certificateNo = cNo.toString();

        _pref = getSharedPreferences("kumkang", MODE_PRIVATE);//sharedPreferences 이름: "kumkang"에 저장
        isShortcut = _pref.getBoolean("isShortcut", false);//"isShortcut"에 들어있는값을 가져온다.
        //Log.e("test1", "isShortcut: " + isShortcut);

        if (!isShortcut)//App을 처음 깔고 시작했을때 이전에 깐적이 있는지없는지 검사하고, 이름과 아이콘을 설정한다.
        {
            addShortcut(this);
        }
        setContentView(R.layout.activity_main);
        viewRegion();
        //progressOFF();
    }

    public void viewRegion() {
        boolean isFirst = PreferenceManager.getBoolean(this, "isFirst");
        if (isFirst) {//최초 실행 회사 & 언어 셋팅
            CharSequence[] companyCharSequences = new CharSequence[3];
            companyCharSequences[0] = "대한민국(음성,진천,창녕)";
            companyCharSequences[1] = "KKM";
            companyCharSequences[2] = "KKV";
            final int[] clickedButton = {0};
            //다이얼로그 출력
            new MaterialAlertDialogBuilder(MainActivity.this).setTitle((CharSequence) "지역(Region)").setCancelable(false)
                    .setSingleChoiceItems(companyCharSequences, 0, new DialogInterface.OnClickListener() {
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            clickedButton[0] = i3;
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialogInterface) {
                        }
                    }).setPositiveButton((CharSequence) "확인(OK)", new DialogInterface.OnClickListener() {
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PreferenceManager.setInt(MainActivity.this, "company", clickedButton[0]);
                            PreferenceManager.setBoolean(MainActivity.this, "isFirst", false);
                            Users.ServiceType = PreferenceManager.getInt(MainActivity.this, "company");//0:금강공업(음성,진천),1:KKM,2:KKV,-1:TEST
                            if (Users.ServiceType == 0) {//한국이면 한국어로 초기셋팅
                                PreferenceManager.setInt(MainActivity.this, "language", 0);
                                Users.Language = 0;
                            } else {//그 외는 영어
                                PreferenceManager.setInt(MainActivity.this, "language", 1);
                                Users.Language = 1;
                            }
                            setServiceAddress();
                            VersionCheckActivity();
                        }
                    }).show();
        } else {
            Users.ServiceType = PreferenceManager.getInt(MainActivity.this, "company");//0:금강공업(음성,진천),1:KKM,2:KKV,-1:TEST
            setServiceAddress();
            VersionCheckActivity();
        }
    }

    private void setServiceAddress() {
        if (Users.ServiceType == 0) {//금강
            Users.ServiceAddress = getString(R.string.service_address);
        } else if (Users.ServiceType == 1) {//KKM
            Users.ServiceAddress = getString(R.string.service_address_kkm);
        } else if (Users.ServiceType == 2) {//KKV
            Users.ServiceAddress = getString(R.string.service_address_kkv);
        }
        Users.Language = PreferenceManager.getInt(MainActivity.this, "language");

        /*else{//TEST
            Users.ServiceAddress = ApplicationClass.getResourses().getString(R.string.service_address_test);
            Users.ServiceType = -1;//0:금강공업(음성,진천),1:KKM,2:KKV,-1:TEST
        }*/
    }

    /**
     * 업데이트 체크
     */
    private void VersionCheckActivity() {

        Intent i = new Intent(this, ActivityDownload.class);
        startActivityForResult(i, 1);//startActivityForResult() 메서드는 단순히 액티비티를 시작하는 것만으로 끝내는 것이 아닌, 시작한 액티비티를 통해 어떠한 결과값을 받기 위해 사용
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {//초기 화면 불러온다.
            // Intent i = new Intent(this, SearchActivity.class);
            Intent i = new Intent(this, ActivityMenuTest3.class);
            i.putExtra("certificateNo", certificateNo);
            startActivityForResult(i, 0);
            finish();
        }
        if (requestCode == 0 && resultCode == RESULT_CANCELED && null != data) {
            finish();
        }

        if (requestCode == 1) {//버전 체크한다음에 실행, 이게우선
            AppendSearchActivity();
        }
    }

    /**
     * 유저 체크
     */
    private void AppendSearchActivity() {

        Intent i = new Intent(this, SplashView.class);//처음에 금강공업 로고 화면 부르면서
        startActivityForResult(i, 0);
    }

    private void addShortcut(Context context) {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //FLAG_ACTIVITY_NEW_TASK: 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED: 사용자가 홈스크린이나 "최근 실행 액티비티목록"에서 태스크를 시작할 경우 시스템이 설정하는 플래그, 이플래그는 새로 태스크를
        //시작하거나 백그라운드 태스크를 포그라운드로 가지고 오는 경우가 아니라면 영향을 주지 않는다, "최근 실행 액티비티 목록":  홈 키를 오랫동안 눌렀을 떄 보여지는 액티비티 목록

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);//putExtra(이름, 실제값)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "KUMKANG");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.img_kumkang));
        //Intent.ShortcutIconResource.fromContext(context, R.drawable.img_kumkang);
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        sendBroadcast(intent);
        SharedPreferences.Editor editor = _pref.edit();
        editor.putBoolean("isShortcut", true);

        editor.commit();
    }
}
package com.kumkangkind.kumkangsm2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.material.navigation.NavigationView;
import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;
import com.kumkangkind.kumkangsm2.fcm.BadgeControl;
import com.kumkangkind.kumkangsm2.fcm.QuickstartPreferences;
import com.kumkangkind.kumkangsm2.fcm.RegistrationIntentService;
import com.kumkangkind.kumkangsm2.sale.ActivitySales;
import com.kumkangkind.kumkangsm2.sqlite.ActivityMessageHistory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ActivityMenuTest3 extends BaseActivity {

    TextView txtDate;
    //TextView tvVersion;
    WoImage image;

    private static final String TAG = "SearchActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    MenuItem item1;
    MenuItem item2;
    MenuItem item3;
    MenuItem item4;
    MenuItem item5;
    MenuItem item6;
    MenuItem item7;
    MenuItem item8;
    MenuItem item9;
    MenuItem item10;
    MenuItem item11;
    MenuItem item12;
    MenuItem item13;
    MenuItem item14;
    MenuItem item15;
    MenuItem item16;


    //여기리스트들은 어플 최초 메인 버튼 셋팅을 의미한다.
    ArrayList<String> eumSungTeam = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "나의 작업보기", "일보확인", "진행기준정보 관리", "진행층수 등록",
            "경비등록", "현장 불만사례", "현장 지원요청", "알림 메시지", "생산내역 조회", "진행층수 등록(회수)","현장 진행현황",""));
    ArrayList<String> eumSungSale = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "나의 작업보기", "일보확인", "진행기준정보 관리", "진행층수 등록",
            "경비등록", "현장 불만사례", "현장 지원요청", "알림 메시지", "생산내역 조회", ""));
    ArrayList<String> eumSungSuper = new ArrayList<>(Arrays.asList("담당자 배정", "나의 작업보기", "진행기준정보 관리", "진행층수 등록", "경비등록", "현장 불만사례",
            "현장 지원요청", "생산내역 조회", ""));
    ArrayList<String> ChangTeam = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "작업요청내역 조회", "작업요청 관리", "일보확인", "경비등록", "알림 메시지", "진행층수 등록(회수)","현장 진행현황", ""));
    ArrayList<String> ChangSale = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "작업요청내역 조회", "작업요청 관리", "일보확인", "경비등록", "알림 메시지", ""));
    ArrayList<String> ChangSuper = new ArrayList<>(Arrays.asList("작업요청내역 조회", "경비등록", "알림 메시지", ""));

    ArrayList<String> returnUser = new ArrayList<>(Arrays.asList("진행층수 등록(회수)", "알림 메시지","현장 진행현황", "나의 작업보기", "반출송장 등록"));
    ArrayList<String> myDefaultButtonList = new ArrayList<>();

    int fromYear = 0;
    int fromMonth = 0;
    int fromDay = 0;

    int toYear = 0;
    int toMonth = 0;
    int toDay = 0;


    BackPressControl backpressed;
    ArrayList<SuWorder> suWorders;

    String restURL;
    String searchFromDate = "";
    String searchToDate = "";
    String searchSupervisor = "";
    String searchDateType = "요청일";
    String searchDateNum = "1";
    Spinner searchTypeSpinner;

    String searchStatusFlag1 = "";
    String searchStatusFlag2 = "";

    String searchtype4 = "0";
    String activityType = "배정";

    String supervisorCode;
    String type1;
    String programType = "";

    String noticeData;

    DatePicker upDatePicker;
    DatePicker downDatePicker;

    Button btnFromDate;
    Button btnToDate;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    SharedPreferences pref;// 커스텀 버튼 내용을 저장
    SharedPreferences noticePref;//공지 유무를 저장
    private FloatingNavigationView mFloatingNavigationView;
    Menu menu;
    ImageView userImage;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        backpressed = new BackPressControl(this);
        //tvVersion = findViewById(R.id.tvVersion);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        menu = mFloatingNavigationView.getMenu();

        View view = mFloatingNavigationView.getHeaderView(0);
        userImage = view.findViewById(R.id.userImage);
        getUserImage();

        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView txtAuthorityName = view.findViewById(R.id.txtAuthorityName);
        txtUserName.setText(Users.UserName);
        if (Users.LeaderType.equals("0")) {
            txtAuthorityName.setText("슈퍼바이저");
        } else if (Users.LeaderType.equals("1")) {
            txtAuthorityName.setText("팀장");
        } else if (Users.LeaderType.equals("2")) {
            txtAuthorityName.setText("영업");
        }
        else{
            txtAuthorityName.setText("회수");
        }

        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //네비게이션 메뉴 하나선택시
                //Snackbar.make((View) mFloatingNavigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                mFloatingNavigationView.close();

                if (item.getTitle().equals(getString(R.string.work_request_management))) {
                    GoWorkRequestManagement();
                } else if (item.getTitle().equals(getString(R.string.work_request_search))) {
                    GoWorkRequestSearch();
                } else if (item.getTitle().equals(getString(R.string.my_work))) {
                    GoWorkRequestSearch();
                } else if (item.getTitle().equals(getString(R.string.daily_report))) {
                    GoDailyReport();
                } else if (item.getTitle().equals(getString(R.string.assignment_status))) {
                    GoAssignmentStatus();
                } else if (item.getTitle().equals(getString(R.string.register_expense))) {
                    GoRegisterExpense();
                } else if (item.getTitle().equals(getString(R.string.assignment))) {
                    GoAssignment();
                } else if (item.getTitle().equals(getString(R.string.message))) {
                    GoMessage();
                } else if (item.getTitle().equals(getString(R.string.problem))) {
                    GoProblem();
                } else if (item.getTitle().equals(getString(R.string.progress_information))) {
                    GoProgressInformation();
                } else if (item.getTitle().equals(getString(R.string.progress_floor))) {
                    GoProgressFloor();
                } else if (item.getTitle().equals(getString(R.string.progress_floor_return))) {
                    GoProgressFloorReturn();
                } else if (item.getTitle().equals(getString(R.string.product))) {
                    GoProduct();
                } else if (item.getTitle().equals(getString(R.string.location_progress))) {
                    GoLocationProgress();
                }
                else if (item.getTitle().equals(getString(R.string.support))) {
                    GoSupport();
                } else if (item.getTitle().equals(getString(R.string.edit_menu))) {
                    ArrayList<String> btnList = new ArrayList<>();
                    CardView btn3 = findViewById(R.id.btn3);
                    ImageView imv3 = findViewById(R.id.imv3);
                    TextView txt3 = findViewById(R.id.txt3);
                    CardView btn4 = findViewById(R.id.btn4);
                    ImageView imv4 = findViewById(R.id.imv4);
                    TextView txt4 = findViewById(R.id.txt4);
                    CardView btn5 = findViewById(R.id.btn5);
                    ImageView imv5 = findViewById(R.id.imv5);
                    TextView txt5 = findViewById(R.id.txt5);
                    CardView btn6 = findViewById(R.id.btn6);
                    ImageView imv6 = findViewById(R.id.imv6);
                    TextView txt6 = findViewById(R.id.txt6);
                    CardView btn7 = findViewById(R.id.btn7);
                    ImageView imv7 = findViewById(R.id.imv7);
                    TextView txt7 = findViewById(R.id.txt7);

                    btnList.add(btn3.getTag().toString());
                    btnList.add(btn4.getTag().toString());
                    try {
                        btnList.add(btn5.getTag().toString());
                        btnList.add(btn6.getTag().toString());
                        btnList.add(btn7.getTag().toString());
                    } catch (Exception E) {

                    }
                    myDefaultButtonList.remove("");
                    final AlertDialog.Builder build = new AlertDialog.Builder(ActivityMenuTest3.this);
                    build.create();
                    build.setTitle(getString(R.string.edit_menu));

                    final String[] items = new String[myDefaultButtonList.size()];
                    for (int i = 0; i < myDefaultButtonList.size(); i++)
                        items[i] = myDefaultButtonList.get(i).toString();

                    final boolean[] checkedItems = new boolean[items.length];

                    for (int i = 0; i < items.length; i++) {
                        if (btnList.contains(items[i]))
                            checkedItems[i] = true;
                        else
                            checkedItems[i] = false;
                    }


                    build.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedItems[which] = isChecked;
                            int cnt = 0;
                            for (int i = 0; i < checkedItems.length; i++) {
                                if (checkedItems[i])
                                    cnt++;
                            }
                            if (cnt > 5) {
                                Toast.makeText(build.getContext(), "최대 5개까지 선택 가능합니다.", Toast.LENGTH_SHORT);
                                checkedItems[which] = false;
                                ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            }
                        }
                    });

                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> stringArrayList = new ArrayList<>();

                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                int selectCount = 0;
                                for (int i = 0; i < items.length; i++) {
                                    if (checkedItems[i]) {
                                        selectCount++;
                                        if (selectCount <= 5) {
                                            stringArrayList.add(items[i]);
                                        }
                                    }
                                }

                                String[] str = new String[5];
                                for (int i = 0; i < str.length; i++) {
                                    if (i + 1 > stringArrayList.size()) {
                                        str[i] = "";
                                        continue;
                                    }

                                    str[i] = stringArrayList.get(i).toString();
                                }

                                refreshCustomButton(str[0], str[1], str[2], str[3], str[4]);
                            } else {
                                Toast.makeText(ActivityMenuTest3.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    build.setPositiveButton("선택", listener);
                    build.setNegativeButton("취소", listener);
                    build.create().show();
                    progressOFF();
                }
                return true;
            }
        });


        txtDate = findViewById(R.id.txtDate);

        btnFromDate = findViewById(R.id.btnFromDate);
        btnToDate = findViewById(R.id.btnToDate);

        SetDate();
        SetButton();

        getInstanceIdToken();
        BadgeControl.clearBadgeCount(this);
        noticePref = getSharedPreferences("NoticePref", MODE_PRIVATE);

        //음성만 공지사항 사용 -> 전부 사용
        boolean viewNotice = true;
        viewNotice = noticePref.getBoolean("viewNotice", true);

        if (viewNotice == true) {
            getNoticeData();
        }

    }

    private void getNoticeData() {
        String url = getString(R.string.service_address) + "getNoticeData";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        GetNoticeData gsod = new GetNoticeData(url, values);
        gsod.execute();
    }

    public class GetNoticeData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetNoticeData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //startProgress();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                //WoImage image;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //stockArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    noticeData = child.getString("AppRemark");
                }
                viewNotice();
            } catch (Exception e) {

            } finally {
                //progressOFF2(this.getClass().getName());
            }
        }
    }

    private void getUserImage() {
        String url = getString(R.string.service_address) + "getUserImage";
        ContentValues values = new ContentValues();
        values.put("EmployeeNo", Users.EmployeeNo);
        GetUserImage gsod = new GetUserImage(url, values);
        gsod.execute();
    }

    public class GetUserImage extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetUserImage(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                //WoImage image;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //stockArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(SearchAvailablePartActivity.this, ErrorCheck, 2);
                        return;
                    }
                    image = new WoImage();
                    image.ImageFile = child.getString("Imagefile");
                }
                if(jsonArray.length()==0){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120,120);
                    userImage.setLayoutParams(layoutParams);
                    userImage.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.kumkangcircle));
                }

                try {
                    byte[] array5 = Base64.decode(image.ImageFile, Base64.DEFAULT);
                    userImage.setBackground(new ShapeDrawable(new OvalShape()));
                    userImage.setClipToOutline(true);
                    userImage.setImageBitmap(BitmapFactory.decodeByteArray(array5, 0, array5.length));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120,120);
                userImage.setLayoutParams(layoutParams);
                userImage.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.kumkangcircle));
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

    private void setMenuItem() {
        item1 = menu.findItem(R.id.edit_menu);
        item2 = menu.findItem(R.id.work_request_management);
        item3 = menu.findItem(R.id.work_request_search);
        item4 = menu.findItem(R.id.my_work);
        item5 = menu.findItem(R.id.daily_report);
        item6 = menu.findItem(R.id.assignment_status);
        item7 = menu.findItem(R.id.register_expense);
        item8 = menu.findItem(R.id.assignment);
        item9 = menu.findItem(R.id.problem);
        item10 = menu.findItem(R.id.progress_information);
        item11 = menu.findItem(R.id.progress_floor);
        item12 = menu.findItem(R.id.support);
        item13 = menu.findItem(R.id.product);
        item14 = menu.findItem(R.id.message);
        item15 = menu.findItem(R.id.progress_floor_return);
        item16 = menu.findItem(R.id.location_progress);
        item1.setVisible(false);
        item2.setVisible(false);
        item3.setVisible(false);
        item4.setVisible(false);
        item5.setVisible(false);
        item6.setVisible(false);
        item7.setVisible(false);
        item8.setVisible(false);
        item9.setVisible(false);
        item10.setVisible(false);
        item11.setVisible(false);
        item12.setVisible(false);
        item13.setVisible(false);
        item14.setVisible(false);
        item15.setVisible(false);
        item16.setVisible(false);
    }

    //POST



    @Override
    protected void onResume() {//onCreate 다음 동작, 창내렸다 다시올려도 동작
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,//동적으로 리시버등록: 한 액티비티 내에서 등록
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {//SearchActivity가 아닐시 리시버를 해제한다.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);//리시버해제
        super.onPause();
    }

    private void viewNotice() {

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_notice, null);
        AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        try {
            tvTitle.setText("변경사항(version " + getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName + ")");
        } catch (PackageManager.NameNotFoundException e) {
            tvTitle.setText("변경사항");
        }


        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        tvContent.setText(noticeData);

        final AlertDialog dialog = buider.create();
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
        //Dialog 보이기

        dialog.show();

        Button btnOK = dialogView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chkNoView = dialogView.findViewById(R.id.chkNoView);

                if (chkNoView.isChecked()) {
                    SharedPreferences.Editor editor = noticePref.edit();
                    editor.putBoolean("viewNotice", false);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });


    }


    public void getInstanceIdToken() {
        //mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {//한번 토큰이 설정된후에 잘 설정 되었는지 확인하려고 한거같은데, RegistrationIntentService.java 에서 확인해보면 54 or 55번 쨰 줄에서 에러가 발생해서 catch문으로 빠짐
            //topics/global 이름이 잘못된거같다.


            @Override
            public void onReceive(Context context, Intent intent) {//
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences//데이터 저장을 위해서는 SharedPreferences.Editor ed = prefs.edit();;을 통하여 Editor인스턴스를 가져와야하는데 지금은 저장 필요없이 값의 존재여부만 보면 되니까 필요없다.
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);//키, 데이터 쌍으로 저장, QuickstartPreferences.SENT_TOKEN_TO_SERVER에 대한 데이터가 존재하지 않는다면 default로 false 저장
                if (sentToken) {

                } else {

                }
            }
        };

        if (checkPlayService()) {

            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }


    private boolean checkPlayService() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void SetDate() {
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        fromYear = cal.get(Calendar.YEAR);
        fromMonth = cal.get(Calendar.MONTH);
        fromDay = cal.get(Calendar.DATE);

        toYear = fromYear;
        toMonth = fromMonth;
        toDay = fromDay;

       /* fromYear = 2018;
        fromMonth = 9;
        fromDay = 21;

        toYear = 2018;
        toMonth = 11;
        toDay = 21;*/

        if (Users.BusinessClassCode == 9)//음성이면 초기값 작업일
            searchDateType = "작업일";

        String strReq = searchDateType;
        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

        String outTxt = searchDateType;

        // txtLeftCircle.setTextColor(Color.parseColor("#18A266"));
        // txtLeftCircle.setTextColor(Color.parseColor("#FFFFFF"));

        txtDate.setText(outTxt);
        btnFromDate.setText(strFromDate);
        btnToDate.setText(strToDate);
        /*try {
            tvVersion.setText("version " + getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

    }


    public void mOnClick(View v) {

        startProgress();
        switch (v.getId()) {

            case R.id.btnFromDate:
            case R.id.btnToDate:
                //Dialog에서 보여줄 입력화면 View 객체 생성 작업
                //Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                LayoutInflater inflater = getLayoutInflater();

                //res폴더>>layout폴더>>dialog_addmember.xml 레이아웃 리소스 파일로 View 객체 생성
                //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
                final View dialogView = inflater.inflate(R.layout.dialog_double_date_picker, null);
                //TextView txtEnd = dialogView.findViewById(R.id.txtEndDay);

                /** 스피너의 폰트, 글자색 변경을 위함
                 * */
                Spinner searchSpinner = dialogView.findViewById(R.id.spinnerSearchType);
                searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                //멤버의 세부내역 입력 Dialog 생성 및 보이기
                AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
                //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fromYear = upDatePicker.getYear();
                        fromMonth = upDatePicker.getMonth();
                        fromDay = upDatePicker.getDayOfMonth();

                        toYear = downDatePicker.getYear();
                        toMonth = downDatePicker.getMonth();
                        toDay = downDatePicker.getDayOfMonth();
                        int selectedNum = searchTypeSpinner.getSelectedItemPosition();
                        if (selectedNum == 0)
                            searchDateType = "요청일";
                        else if (selectedNum == 1)
                            searchDateType = "희망일";
                        else
                            searchDateType = "작업일";

                        String strReq = searchDateType;
                        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
                        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;
                        txtDate.setText(strReq);

                        btnFromDate.setText(strFromDate);
                        btnToDate.setText(strToDate);
                        progressOFF();
                    }
                });

                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressOFF();
                    }
                });

//설정한 값으로 AlertDialog 객체 생성

                AlertDialog dialog = buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();

                upDatePicker = dialog.findViewById(R.id.start_date);
                downDatePicker = dialog.findViewById(R.id.end_date);
                searchTypeSpinner = dialog.findViewById(R.id.spinnerSearchType);
                upDatePicker.updateDate(fromYear, fromMonth, fromDay);
                downDatePicker.updateDate(toYear, toMonth, toDay);


                String[] typeList = {"요청일", "희망일", "작업일"};
                int searchNum;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        typeList);
                searchTypeSpinner.setAdapter(adapter);

                if (searchDateType.equals("요청일")) {
                    searchNum = 0; //요청
                } else if (searchDateType.equals("희망일")) {
                    searchNum = 1; //희망
                } else {
                    searchNum = 2; //작업
                }
                searchTypeSpinner.setSelection(searchNum);
                break;
        }

    }

    /*
     * 메뉴편집으로 선택한 커스텀 메뉴버튼들을 설정한다.
     * */
    private void refreshCustomButton(String strButton3, String strButton4, String strButton5, String strButton6, String strButton7) {

        pref = getSharedPreferences("CustomButton", MODE_PRIVATE);
        SharedPreferences.Editor customEditor = pref.edit();

        CardView btn3 = findViewById(R.id.btn3);
        ImageView imv3 = findViewById(R.id.imv3);
        TextView txt3 = findViewById(R.id.txt3);
        CardView btn4 = findViewById(R.id.btn4);
        ImageView imv4 = findViewById(R.id.imv4);
        TextView txt4 = findViewById(R.id.txt4);
        CardView btn5 = findViewById(R.id.btn5);
        ImageView imv5 = findViewById(R.id.imv5);
        TextView txt5 = findViewById(R.id.txt5);
        CardView btn6 = findViewById(R.id.btn6);
        ImageView imv6 = findViewById(R.id.imv6);
        TextView txt6 = findViewById(R.id.txt6);
        CardView btn7 = findViewById(R.id.btn7);
        ImageView imv7 = findViewById(R.id.imv7);
        TextView txt7 = findViewById(R.id.txt7);

        //FButton fbtn =findViewById(R.id.btn3);

        if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저는 아래 버튼 두개 hidden

            btn5.setVisibility(View.INVISIBLE);
            btn6.setVisibility(View.INVISIBLE);
            btn7.setVisibility(View.INVISIBLE);
        }

        if(Users.LeaderType.equals("3")){
            btn7.setVisibility(View.INVISIBLE);
        }

        if (strButton3 == "")
            btn3.setVisibility(View.INVISIBLE);
        else
            btn3.setVisibility(View.VISIBLE);

        if (strButton4 == "")
            btn4.setVisibility(View.INVISIBLE);
        else
            btn4.setVisibility(View.VISIBLE);

        if (strButton5 == "")
            btn5.setVisibility(View.INVISIBLE);
        else
            btn5.setVisibility(View.VISIBLE);

        if (strButton6 == "")
            btn6.setVisibility(View.INVISIBLE);
        else
            btn6.setVisibility(View.VISIBLE);

        if (strButton7 == "")
            btn7.setVisibility(View.INVISIBLE);
        else
            btn7.setVisibility(View.VISIBLE);

        txt3.setText(strButton3);
        btn3.setTag(strButton3);
        Drawable img3 = FindImage(btn3.getTag().toString());
        imv3.setImageDrawable(img3);
        //imv3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

        txt4.setText(strButton4);
        btn4.setTag(strButton4);
        Drawable img4 = FindImage(btn4.getTag().toString());
        imv4.setImageDrawable(img4);
        //btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);


        txt5.setText(strButton5);
        btn5.setTag(strButton5);
        Drawable img5 = FindImage(btn5.getTag().toString());
        imv5.setImageDrawable(img5);
        //btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

        txt6.setText(strButton6);
        btn6.setTag(strButton6);
        Drawable img6 = FindImage(btn6.getTag().toString());
        imv6.setImageDrawable(img6);
        //btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

        txt7.setText(strButton7);
        btn7.setTag(strButton7);
        Drawable img7 = FindImage(btn7.getTag().toString());
        imv7.setImageDrawable(img7);
        // btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);

        customEditor.putString("Button3", btn3.getTag().toString());
        customEditor.putString("Button4", btn4.getTag().toString());
        customEditor.putString("Button5", btn5.getTag().toString());
        customEditor.putString("Button6", btn6.getTag().toString());
        customEditor.putString("Button7", btn7.getTag().toString());

        customEditor.commit();
    }

    /*
     * 커스텀 버튼 클릭
     * */
    public void customButtonClick(View v) {
        switch (v.getTag().toString()) {

            case "담당자 배정":
                startProgress();
                if (Users.BusinessClassCode == 9) {//음성
                    programType = "담당자배정";
                    AssignPerson();//담당자배정
                } else//창녕
                    ClickSearchButton2();//담당자배정
                break;

            case "담당자 배정현황":
                startProgress();
                if (Users.BusinessClassCode == 9) {//음성
                    programType = "담당자배정현황";
                    AssignPersonStatus();//담당자배정현황

                } else//창녕
                    ClickSearchButton3();//담당자배정현황
                break;

            case "작업요청내역 조회":
                startProgress();
                ClickSearchButton();
                break;

            case "나의 작업보기":
                startProgress();
                ClickSearchButton();
                break;

            case "작업요청 관리":
                startProgress();
                startActivity(new Intent(ActivityMenuTest3.this, ActivitySales.class));
                break;

            case "일보확인":
                startProgress();
                ClickSearchButton4();//일보확인
                break;

            case "진행기준정보 관리":
                startProgress();
                programType = "진행기준정보관리";
                ClickProgressFloor();
                break;

            case "진행층수 등록":
                startProgress();
                programType = "진행층수등록";
                ClickProgressFloor();
                break;
            case "진행층수 등록(회수)":
                startProgress();
                programType = "진행층수등록회수";
                ClickProgressFloor();
                break;

            case "경비등록":
                startProgress();
                if (Users.BusinessClassCode == 11)//창녕
                    startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCost2.class));
                else//음성
                    startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCostEumsung.class));
                break;

            case "현장 불만사례":
                startProgress();
                programType = "현장불만사례";
                ClickComplain();
                break;
            case "현장 지원요청":
                startProgress();
                programType = "현장지원요청";
                ClickProgressFloor();
                break;
            case "생산내역 조회":
                GoProduct();
                break;
            case "알림 메시지":
                programType = "알림 메시지";
                startActivity(new Intent(ActivityMenuTest3.this, ActivityMessageHistory.class));
                break;
            case "현장 진행현황":
                GoLocationProgress();
                break;

        }
    }

    /**
     * 현장 불만사례 액티비티를 띄운다.
     */
    private void ClickComplain() {
        if (Users.LeaderType.equals("0")) {//슈퍼바이저
            new ActivityMenuTest3.GetCustomerLocationByGet("내현장").execute(getString(R.string.service_address) + "getCustomerLocation/" + Users.USER_ID);
        } else if (Users.LeaderType.equals("1")) {//팀장
            new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address) + "getCustomerLocation2/" + "모든현장");
        } else if (Users.LeaderType.equals("2")) {//영업담당자
            new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address) + "getCustomerLocation2/" + "모든현장");
        }
    }


    private void SetButton() {
        //전체 버튼 셋팅

        SetFloatingButton();


        //빠른 버튼 셋팅
        CardView btn3 = findViewById(R.id.btn3);
        ImageView imv3 = findViewById(R.id.imv3);
        TextView txt3 = findViewById(R.id.txt3);
        CardView btn4 = findViewById(R.id.btn4);
        ImageView imv4 = findViewById(R.id.imv4);
        TextView txt4 = findViewById(R.id.txt4);
        CardView btn5 = findViewById(R.id.btn5);
        ImageView imv5 = findViewById(R.id.imv5);
        TextView txt5 = findViewById(R.id.txt5);
        CardView btn6 = findViewById(R.id.btn6);
        ImageView imv6 = findViewById(R.id.imv6);
        TextView txt6 = findViewById(R.id.txt6);
        CardView btn7 = findViewById(R.id.btn7);
        ImageView imv7 = findViewById(R.id.imv7);
        TextView txt7 = findViewById(R.id.txt7);


        String strButton3 = "";
        String strButton4 = "";
        String strButton5 = "";
        String strButton6 = "";
        String strButton7 = "";


        SharedPreferences initPref;//초기 버튼을 셋팅하기 위한 prefereneces
        pref = getSharedPreferences("CustomButton", MODE_PRIVATE);

        Boolean isInitButton = false;

        strButton3 = pref.getString("Button3", strButton3);
        strButton4 = pref.getString("Button4", strButton4);
        strButton5 = pref.getString("Button5", strButton5);
        strButton6 = pref.getString("Button6", strButton6);
        strButton7 = pref.getString("Button7", strButton7);

        if (strButton3 == "")
            btn3.setVisibility(View.INVISIBLE);
        else
            btn3.setVisibility(View.VISIBLE);
        if (strButton4 == "")
            btn4.setVisibility(View.INVISIBLE);
        else
            btn4.setVisibility(View.VISIBLE);
        if (strButton5 == "")
            btn5.setVisibility(View.INVISIBLE);
        else
            btn5.setVisibility(View.VISIBLE);
        if (strButton6 == "")
            btn6.setVisibility(View.INVISIBLE);
        else
            btn6.setVisibility(View.VISIBLE);

        if (strButton7 == "")
            btn7.setVisibility(View.INVISIBLE);
        else
            btn7.setVisibility(View.VISIBLE);

        initPref = getSharedPreferences("InitButton", MODE_PRIVATE);
        isInitButton = initPref.getBoolean("isInit", false);

        if (!CheckCustomButton(strButton3, strButton4, strButton5, strButton6, strButton7) && isInitButton) { //저장되어있는 버튼의 권한을 체크한 후, 적절하지 않은 권한이 있다면-> 초기설정으로 실행한다.
            isInitButton = false;
            Toast.makeText(getBaseContext(), "권한이 변경되어, '사용자지정 버튼'을 초기화 합니다.", Toast.LENGTH_LONG).show();
        }


        if (!isInitButton) {//초기 설정
            SharedPreferences.Editor initEditor = initPref.edit();
            SharedPreferences.Editor customEditor = pref.edit();

            if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("0")) {//음성 슈퍼바이저일때,1.담당자배정 2.나의현장보기(작업요청내역조회) 3.현장불만사례 4.경비등록

                txt3.setText("담당자 배정");
                btn3.setTag("담당자 배정");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("현장 불만사례");
                btn5.setTag("현장 불만사례");
                btn5.setVisibility(View.VISIBLE);

                txt6.setText("현장 지원요청");
                btn6.setTag("현장 지원요청");
                btn6.setVisibility(View.VISIBLE);

                txt7.setText("경비등록");
                btn7.setTag("경비등록");
                btn7.setVisibility(View.VISIBLE);
            } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("1")) {//음성 팀장일때,1.담당자배정현황 2.나의 작업보기 3.일보확인 4.현장 불만사례

                txt3.setText("담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                txt6.setText("현장 불만사례");
                btn6.setTag("현장 불만사례");
                btn6.setVisibility(View.VISIBLE);

                txt7.setText("현장 지원요청");
                btn7.setTag("현장 지원요청");
                btn7.setVisibility(View.VISIBLE);
            } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("2")) {//음성 영업담당자 일때,1.담당자배정현황 2.나의 작업보기 3.일보확인 4.현장 불만사례

                txt3.setText("담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                txt6.setText("현장 불만사례");
                btn6.setTag("현장 불만사례");
                btn6.setVisibility(View.VISIBLE);

                txt7.setText("현장 지원요청");
                btn7.setTag("현장 지원요청");
                btn7.setVisibility(View.VISIBLE);
            }
            else if(Users.BusinessClassCode == 9 && Users.LeaderType.equals("3")){
                txt3.setText(getString(R.string.progress_floor_return));
                btn3.setTag(getString(R.string.progress_floor_return));
                btn3.setVisibility(View.VISIBLE);

                //getString(R.string.progress_floor)

                txt4.setText(getString(R.string.location_progress));
                btn4.setTag(getString(R.string.location_progress));
                btn4.setVisibility(View.VISIBLE);

                txt5.setText(getString(R.string.my_work));
                btn5.setTag(getString(R.string.my_work));
                btn5.setVisibility(View.VISIBLE);

                txt6.setText(getString(R.string.message));
                btn6.setTag(getString(R.string.message));
                btn6.setVisibility(View.VISIBLE);

                btn7.setTag(getString(R.string.message));
                btn7.setVisibility(View.INVISIBLE);
            }

            else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저일때,1.작업요청내역 조회 2. 경비등록

                txt3.setText("작업요청내역 조회");
                btn3.setTag("작업요청내역 조회");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("경비등록");
                btn4.setTag("경비등록");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("알림 메시지");
                btn5.setTag("알림 메시지");
                btn5.setVisibility(View.VISIBLE);

                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);

            }

            else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("1")) {//창녕 팀장일때,1.담당자배정 현황 2.담당자배정 3.일보확인 4.작업요청 관리

                txt3.setText("담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("담당자 배정");
                btn4.setTag("담당자 배정");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                txt6.setText("작업요청 관리");
                btn6.setTag("작업요청 관리");
                btn6.setVisibility(View.VISIBLE);

                txt7.setText("작업요청내역 조회");
                btn7.setTag("작업요청내역 조회");
                btn7.setVisibility(View.VISIBLE);
            }

           else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("2")) {//창녕 영업담당자 일때,1.일보확인 2.작업요청 관리 3.작업요청내역 조회 4.경비등록

                txt3.setText("일보확인");
                btn3.setTag("일보확인");
                btn3.setVisibility(View.VISIBLE);

                txt4.setText("작업요청 관리");
                btn4.setTag("작업요청 관리");
                btn4.setVisibility(View.VISIBLE);

                txt5.setText("작업요청내역 조회");
                btn5.setTag("작업요청내역 조회");
                btn5.setVisibility(View.VISIBLE);

                txt6.setText("경비등록");
                btn6.setTag("경비등록");
                btn6.setVisibility(View.VISIBLE);

                txt7.setText("알림 메시지");
                btn7.setTag("알림 메시지");
                btn7.setVisibility(View.VISIBLE);
            }

            else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("3")) {

                txt3.setText(getString(R.string.progress_floor_return));
                btn3.setTag(getString(R.string.progress_floor_return));
                btn3.setVisibility(View.VISIBLE);

                //getString(R.string.progress_floor)

                txt4.setText(getString(R.string.location_progress));
                btn4.setTag(getString(R.string.location_progress));
                btn4.setVisibility(View.VISIBLE);

                txt5.setText(getString(R.string.my_work));
                btn5.setTag(getString(R.string.my_work));
                btn5.setVisibility(View.VISIBLE);

                txt6.setText(getString(R.string.message));
                btn6.setTag(getString(R.string.message));
                btn6.setVisibility(View.VISIBLE);

                btn7.setTag(getString(R.string.message));
                btn7.setVisibility(View.INVISIBLE);
            }

            Drawable img3 = FindImage(btn3.getTag().toString());
            imv3.setImageDrawable(img3);
            //btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

            Drawable img4 = FindImage(btn4.getTag().toString());
            imv4.setImageDrawable(img4);
            //btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);

            Drawable img5 = FindImage(btn5.getTag().toString());
            imv5.setImageDrawable(img5);
            //btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

            if (!(Users.BusinessClassCode == 11 && Users.LeaderType.equals("0"))) {
                Drawable img6 = FindImage(btn6.getTag().toString());
                imv6.setImageDrawable(img6);
                //btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

                Drawable img7 = FindImage(btn7.getTag().toString());
                imv7.setImageDrawable(img7);
                //btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);
            }

            customEditor.putString("Button3", btn3.getTag().toString());
            customEditor.putString("Button4", btn4.getTag().toString());
            customEditor.putString("Button5", btn5.getTag().toString());


            if (!(Users.BusinessClassCode == 11 && Users.LeaderType.equals("0"))) {
                customEditor.putString("Button6", btn6.getTag().toString());
                customEditor.putString("Button7", btn7.getTag().toString());
            }

            customEditor.commit();

            initEditor.putBoolean("isInit", true);//초기 버튼 설정후 true로 변경
            initEditor.commit();
        } else {//초기 이후 부터

            txt3.setText(strButton3);
            btn3.setTag(strButton3);
            Drawable img3 = FindImage(btn3.getTag().toString());
            imv3.setImageDrawable(img3);
            //btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

            txt4.setText(strButton4);
            btn4.setTag(strButton4);
            Drawable img4 = FindImage(btn4.getTag().toString());
            imv4.setImageDrawable(img4);
            //btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);


            txt5.setText(strButton5);
            btn5.setTag(strButton5);
            Drawable img5 = FindImage(btn5.getTag().toString());
            imv5.setImageDrawable(img5);
            //btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

            txt6.setText(strButton6);
            btn6.setTag(strButton6);
            Drawable img6 = FindImage(btn6.getTag().toString());
            imv6.setImageDrawable(img6);
            //btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

            txt7.setText(strButton7);
            btn7.setTag(strButton7);
            Drawable img7 = FindImage(btn7.getTag().toString());
            imv7.setImageDrawable(img7);

            if(Users.LeaderType.equals("3")){//회수담당
                //회수는 일단 강제 메뉴 3개 셋팅
                //1. 진행층수 등록(회수)
                //2. 현장 진행현황
                //3. 알림 메시지
                txt3.setText(getString(R.string.progress_floor_return));
                btn3.setTag(getString(R.string.progress_floor_return));
                Drawable _img3 = FindImage(getString(R.string.progress_floor_return));
                imv3.setImageDrawable(_img3);
                //btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

                txt4.setText(getString(R.string.location_progress));
                btn4.setTag(getString(R.string.location_progress));
                Drawable _img4 = FindImage(getString(R.string.location_progress));
                imv4.setImageDrawable(_img4);
                //btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);

                txt5.setText(getString(R.string.my_work));
                btn5.setTag(getString(R.string.my_work));
                Drawable _img5 = FindImage(getString(R.string.my_work));
                imv5.setImageDrawable(_img5);

                txt6.setText(getString(R.string.message));
                btn6.setTag(getString(R.string.message));
                Drawable _img6 = FindImage(getString(R.string.message));
                imv6.setImageDrawable(_img6);

                btn7.setVisibility(View.INVISIBLE);
            }
            //btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);
        }

        /*final BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setButtonEnum(ButtonEnum.SimpleCircle);*/





        /*if(Users.BusinessClassCode==9){//음성일시만 초기 메뉴 펼친다.
         *//*
         * 시작시, Boom메뉴 펼치기
         * *//*
            bmb.post(new Runnable() {
                @Override
                public void run() {
                    bmb.post(new Runnable() {
                        @Override
                        public void run() {
                            bmb.boomImmediately();
                        }
                    });
                }
            });
        }*/


    }

    private void SetFloatingButton() {
        setMenuItem();
        if(Users.LeaderType.equals("3")){//회수담당
            //4.나의 작업보기
            //14.알림 메시지
            //15.진행층수 등록(회수)
            //16.현장 진행현황
            item4.setVisible(true);
            item14.setVisible(true);
            item15.setVisible(true);
            item16.setVisible(true);
        }
        else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("1")) { //창녕 팀장권한, 메뉴갯수 8개
            //1.메뉴 편집
            //2.작업요청 관리
            //3.작업요청내역 조회
            //5.일보확인
            //6.담당자 배정현황
            //7.경비등록
            //8.담당자 배정
            //14.알림 메시지
            //15.진행층수 등록(회수)
            //16.현장 진행현황
            item1.setVisible(true);
            item2.setVisible(true);
            item3.setVisible(true);
            item5.setVisible(true);
            item6.setVisible(true);
            item7.setVisible(true);
            item8.setVisible(true);
            item14.setVisible(true);
            item15.setVisible(true);
            item16.setVisible(true);

        } else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("2")) {//창녕 영업담당자 권한, 8개
            //1.메뉴 편집
            //2.작업요청 관리
            //3.작업요청내역 조회
            //5.일보확인
            //6.담당자 배정현황
            //7.경비등록
            //8.담당자 배정
            //14.알림 메시지
            item1.setVisible(true);
            item2.setVisible(true);
            item3.setVisible(true);
            item5.setVisible(true);
            item6.setVisible(true);
            item7.setVisible(true);
            item8.setVisible(true);
            item14.setVisible(true);

        } else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저 권한, 5개

            //1.메뉴 편집
            //3.작업요청내역 조회
            //7.경비등록
            //14.알림 메시지
            item1.setVisible(true);
            item3.setVisible(true);
            item7.setVisible(true);
            item14.setVisible(true);

        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("1")) {//음성 팀장 권한, 11개
            //1.메뉴 편집
            //4.나의 작업보기
            //5.일보확인
            //6.담당자 배정현황
            //7.경비등록
            //8.담당자 배정
            //9.현장불만사례
            //10.진행기준정보 관리
            //11.진행층수 관리
            //12.현장 지원요청
            //13.생산내역 조회
            //14.알림 메시지
            //15.진행층수 등록(회수)
            //16.현장 진행현황
            item1.setVisible(true);
            item4.setVisible(true);
            item5.setVisible(true);
            item6.setVisible(true);
            item7.setVisible(true);
            item8.setVisible(true);
            item9.setVisible(true);
            item10.setVisible(true);
            item11.setVisible(true);
            item12.setVisible(true);
            item13.setVisible(true);
            item14.setVisible(true);
            item15.setVisible(true);
            item16.setVisible(true);
        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("2")) {//음성 영업담당자 권한, 11개
            //1.메뉴 편집
            //4.나의 작업보기
            //5.일보확인
            //6.담당자 배정현황
            //7.경비등록
            //8.담당자 배정
            //9.현장불만사례
            //10.진행기준정보 관리
            //11.진행층수 관리
            //12.현장 지원요청
            //13.생산내역 조회
            //14.알림 메시지
            item1.setVisible(true);
            item4.setVisible(true);
            item5.setVisible(true);
            item6.setVisible(true);
            item7.setVisible(true);
            item8.setVisible(true);
            item9.setVisible(true);
            item10.setVisible(true);
            item11.setVisible(true);
            item12.setVisible(true);
            item13.setVisible(true);
            item14.setVisible(true);
        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("0")) {//음성 슈퍼바이저 권한, 9개
            //1.메뉴 편집
            //4.나의 작업보기
            //7.경비등록
            //8.담당자 배정
            //9.현장불만사례
            //10.진행기준정보 관리
            //11.진행층수 관리
            //12.현장 지원요청
            //13.생산내역 조회
            //14.알림 메시지
            item1.setVisible(true);
            item4.setVisible(true);
            item7.setVisible(true);
            item8.setVisible(true);
            item9.setVisible(true);
            item10.setVisible(true);
            item11.setVisible(true);
            item12.setVisible(true);
            item13.setVisible(true);
            item14.setVisible(true);
        }
    }


    /*
     * 저장되어있는 버튼의 권한을 체크한 후, 적절하지 않은 권한이 있다면-> 초기설정으로 실행한다. + 사용자 사업장, 권한별 버튼 지정도 함께
     * */
    private boolean CheckCustomButton(String strButton3, String strButton4, String strButton5, String strButton6, String strButton7) {

        if(Users.LeaderType.equals("3")){//회수담당
            myDefaultButtonList = returnUser;
            for (String str : returnUser) {
                if (!returnUser.contains(strButton3)) {//포함하지않는다. -> Problem
                    return false;
                }
                if (!returnUser.contains(strButton4)) {//포함하지않는다. -> Problem
                    return false;
                }
                if (!returnUser.contains(strButton5)) {//포함하지않는다. -> Problem
                    return false;
                }
                if (!returnUser.contains(strButton6)) {//포함하지않는다. -> Problem
                    return false;
                }
                if (!returnUser.contains(strButton7)) {//포함하지않는다. -> Problem
                    return false;
                }
            }
            return true;
        }

        else if (Users.BusinessClassCode == 9) {//음성
            switch (Users.LeaderType) {
                case "0"://슈퍼바이저
                    myDefaultButtonList = eumSungSuper;
                    for (String str : eumSungSuper) {
                        if (!eumSungSuper.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSuper.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSuper.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSuper.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }

                        if (!eumSungSuper.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
                case "1"://팀장
                    myDefaultButtonList = eumSungTeam;
                    for (String str : eumSungTeam) {
                        if (!eumSungTeam.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungTeam.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungTeam.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungTeam.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungTeam.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
                case "2"://영업담당자
                    myDefaultButtonList = eumSungSale;
                    for (String str : eumSungSale) {
                        if (!eumSungSale.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSale.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSale.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSale.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!eumSungSale.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
            }
        } else if (Users.BusinessClassCode == 11) {//창녕
            switch (Users.LeaderType) {
                case "0"://슈퍼바이저
                    myDefaultButtonList = ChangSuper;
                    for (String str : ChangSuper) {
                        if (!ChangSuper.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSuper.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSuper.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSuper.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSuper.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
                case "1"://팀장
                    myDefaultButtonList = ChangTeam;
                    for (String str : ChangTeam) {
                        if (!ChangTeam.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangTeam.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangTeam.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangTeam.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangTeam.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
                case "2"://영업담당자
                    myDefaultButtonList = ChangSale;
                    for (String str : ChangSale) {
                        if (!ChangSale.contains(strButton3)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSale.contains(strButton4)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSale.contains(strButton5)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSale.contains(strButton6)) {//포함하지않는다. -> Problem
                            return false;
                        }
                        if (!ChangSale.contains(strButton7)) {//포함하지않는다. -> Problem
                            return false;
                        }
                    }
                    return true;
            }
        }
        return true;
    }


    /*
     * 버튼에 맞는 이미지를 찾는다.
     * */
    private Drawable FindImage(String str) {
        /*switch (str) {
            case "담당자 배정":
                return getBaseContext().getResources().getDrawable(R.drawable.round_check_circle_outline_white_48);

            case "담당자 배정현황":
                return getBaseContext().getResources().getDrawable(R.drawable.round_checklist_white_48);

            case "작업요청내역 조회":
                return getBaseContext().getResources().getDrawable(R.drawable.round_search_white_48);

            case "나의 작업보기":
                return getBaseContext().getResources().getDrawable(R.drawable.round_search_white_48);

            case "작업요청 관리":
                return getBaseContext().getResources().getDrawable(R.drawable.round_note_alt_white_48);

            case "일보확인":
                return getBaseContext().getResources().getDrawable(R.drawable.round_menu_book_white_48);

            case "진행기준정보 관리":
                return getBaseContext().getResources().getDrawable(R.drawable.round_cached_white_48);

            case "진행층수 등록":
                return getBaseContext().getResources().getDrawable(R.drawable.round_stairs_white_48);

            case "경비등록":
                return getBaseContext().getResources().getDrawable(R.drawable.round_receipt_long_white_48);

            case "현장 불만사례":
                return getBaseContext().getResources().getDrawable(R.drawable.round_edit_location_alt_white_48);

            case "알림 메시지":
                return getBaseContext().getResources().getDrawable(R.drawable.round_sms_white_48);

            case "현장 지원요청":
                return getBaseContext().getResources().getDrawable(R.drawable.round_support_agent_white_48);

            default:
                return getBaseContext().getResources().getDrawable(R.drawable.round_check_circle_outline_white_48);
        }*/

        if (str.equals(getString(R.string.assignment))) {//담당자 배정
            return getBaseContext().getResources().getDrawable(R.drawable.round_check_circle_outline_white_48);
        } else if (str.equals(getString(R.string.assignment_status))) {//담당자 배정현황
            return getBaseContext().getResources().getDrawable(R.drawable.round_checklist_white_48);
        } else if (str.equals(getString(R.string.work_request_search))) {//작업요청내역 조회
            return getBaseContext().getResources().getDrawable(R.drawable.round_search_white_48);
        } else if (str.equals(getString(R.string.my_work))) {//나의 작업보기
            return getBaseContext().getResources().getDrawable(R.drawable.round_search_white_48);
        } else if (str.equals(getString(R.string.work_request_management))) {//작업요청 관리
            return getBaseContext().getResources().getDrawable(R.drawable.round_note_alt_white_48);
        } else if (str.equals(getString(R.string.daily_report))) {//일보확인
            return getBaseContext().getResources().getDrawable(R.drawable.round_menu_book_white_48);
        } else if (str.equals(getString(R.string.progress_information))) {//진행기준정보 관리
            return getBaseContext().getResources().getDrawable(R.drawable.round_cached_white_48);
        } else if (str.equals(getString(R.string.progress_floor))) {//진행층수 등록
            return getBaseContext().getResources().getDrawable(R.drawable.round_stairs_white_48);
        } else if (str.equals(getString(R.string.progress_floor_return))) {//진행층수 등록(회수)
            return getBaseContext().getResources().getDrawable(R.drawable.round_stairs_white_48);
        } else if (str.equals(getString(R.string.register_expense))) {//경비등록
            return getBaseContext().getResources().getDrawable(R.drawable.round_receipt_long_white_48);
        } else if (str.equals(getString(R.string.problem))) {//현장 불만사례
            return getBaseContext().getResources().getDrawable(R.drawable.round_edit_location_alt_white_48);
        } else if (str.equals(getString(R.string.support))) {//현장 지원요청
            return getBaseContext().getResources().getDrawable(R.drawable.round_support_agent_white_48);
        } else if (str.equals(getString(R.string.product))) {//생산내역 조회
            return getBaseContext().getResources().getDrawable(R.drawable.round_precision_manufacturing_white_48);
        } else if (str.equals(getString(R.string.message))) {//알림 메시지
            return getBaseContext().getResources().getDrawable(R.drawable.round_sms_white_48);
        }
        else if (str.equals(getString(R.string.location_progress))) {//현장 진행현황
            return getBaseContext().getResources().getDrawable(R.drawable.round_published_with_changes_white_48);}
        else {
            return getBaseContext().getResources().getDrawable(R.drawable.round_check_circle_outline_white_48);
        }
    }


    /*
    작업 요청 관리
     */
    /*private void GoWorkRequestManagement(BoomMenuButton bmb) {
        SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.colorPrimary)
                .normalImageRes(R.drawable.txt_work_request)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(ActivityMenuTest3.this, ActivitySales.class));
                    }
                })
                .isRound(false);
        bmb.addBuilder(builder);
    }*/

    private void GoWorkRequestManagement() {
        startActivity(new Intent(ActivityMenuTest3.this, ActivitySales.class));
    }

    /*
    작업 요청 내역 조회
     */
    private void GoWorkRequestSearch() {
        startProgress();
        ClickSearchButton();
    }


    /*
    일보확인
     */
    private void GoDailyReport() {
        ClickSearchButton4();//일보확인

    }

    /*
   현장불만사례
    */
    private void GoProblem() {
        programType = "현장불만사례";
        startProgress();
        ClickComplain();
    }

    /*
    진행기준정보관리
   */
    private void GoProgressInformation() {


        programType = "진행기준정보관리";

        startProgress();
        ClickProgressFloor();


    }

    /*
    진행층수 관리
   */
    private void GoProgressFloor() {
        programType = "진행층수등록";
        startProgress();
        ClickProgressFloor();
    }

    private void GoProgressFloorReturn() {
        programType = "진행층수등록회수";
        startProgress();
        ClickProgressFloor();
    }

    /*
    담당자 배정현황
   */
    private void GoAssignmentStatus() {


        startProgress();
        if (Users.BusinessClassCode == 9) {//음성
            programType = "담당자배정현황";
            AssignPersonStatus();//담당자배정현황

        } else//창녕
            ClickSearchButton3();//담당자배정현황

    }

    /*
   경비등록
  */
    private void GoRegisterExpense() {


        if (Users.BusinessClassCode == 11)//창녕
            startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCost2.class));
        else//음성
            startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCostEumsung.class));

    }

    /*
   담당자 배정
  */
    private void GoAssignment() {
        startProgress();
        if (Users.BusinessClassCode == 9) {//음성
            programType = "담당자배정";
            AssignPerson();//담당자배정

        } else//창녕
            ClickSearchButton2();//담당자배정
    }


    /*
 알림 메시지
*/
    private void GoMessage() {
        startActivity(new Intent(ActivityMenuTest3.this, ActivityMessageHistory.class));
    }

    /*
    현장 지원요청
 */
    private void GoSupport() {
        programType = "현장지원요청";
        startProgress();
        ClickProgressFloor();
    }

    private void GoProduct() {
        programType = "생산내역조회";
        Intent i;
        i = new Intent(getBaseContext(), LocationTreeViewActivitySearchForAll.class);//todo

        i.putExtra("type", programType);
        i.putExtra("programType", programType);
        startActivity(i);
    }


    private void GoLocationProgress() {
        programType = "현장진행현황";
        startProgress();
        ClickProgressFloor();
    }

    /*
     * 담당자 배정
     * */
    private void AssignPerson() {
        new ActivityMenuTest3.GetCustomerLocationByGet("미배정").execute(getString(R.string.service_address) + "getCustomerLocation2/" + Users.USER_ID);
    }

    /*
     * 담당자 배정 현황
     * */
    private void AssignPersonStatus() {
        new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address) + "getCustomerLocation2/" + "모든현장");
    }

    /**
     * 담당자 배정(미지정)
     * 배정리스트 조회(미지정인 항목들)
     */
    private void ClickSearchButton2() {

        suWorders = new ArrayList<SuWorder>();

        if (searchDateType.equals("요청일")) {
            searchDateNum = "1"; //요청
        } else if (searchDateType.equals("희망일")) {
            searchDateNum = "0"; //희망
        } else {
            searchDateNum = "2"; //작업
        }

        searchSupervisor = Users.USER_ID;
        searchtype4 = "16001";//담당자 미배정 상태
        searchFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        searchToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;
        searchStatusFlag1 = "0";//어차피 미배정이므로 뭘넣든가 영향 x
        searchStatusFlag2 = "0";//어차피 미배정이므로 뭘넣든가 영향 x
        activityType = "배정";
        new ActivityMenuTest3.HttpAsyncTask().execute(getString(R.string.service_address) + "getassigndata2");
    }


    /**
     * 담당자 배정현황
     * 배정된 항목들 조회(배정+ 요청, 요청(확인)상태)
     */
    private void ClickSearchButton3() {

        suWorders = new ArrayList<SuWorder>();

        if (searchDateType.equals("요청일")) {
            searchDateNum = "1"; //요청
        } else if (searchDateType.equals("희망일")) {
            searchDateNum = "0"; //희망
        } else {
            searchDateNum = "2"; //작업
        }

        searchSupervisor = Users.USER_ID;
        searchtype4 = "0";
        searchFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        searchToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;
        searchStatusFlag1 = "-1";
        searchStatusFlag2 = "0";

        activityType = "배정";
        new ActivityMenuTest3.HttpAsyncTask().execute(getString(R.string.service_address) + "getassigndata2");
    }

    /*
     * 진행층수, 진행정보관리 데이터 가져오기
     * */
    private void ClickProgressFloor() {
        new ActivityMenuTest3.GetCustomerLocationByGet("내현장").execute(getString(R.string.service_address) + "getCustomerLocation/" + Users.USER_ID);
    }

    public class GetCustomerLocationByGet extends AsyncTask<String, Void, String> {

        String type = "";

        public GetCustomerLocationByGet(String type) {
            this.type = type;
        }

        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                //Log.i("ReadJSONFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray;
                if (this.type.equals("내현장"))//내현장만
                    jsonArray = jsonObject.optJSONArray("GetCustomerLocationResult");
                else//내현장 + 미배정 or 모든현장
                    jsonArray = jsonObject.optJSONArray("GetCustomerLocation2Result");

                HashMap<String, Customer> customerHashMap;
                customerHashMap = new HashMap<>();
                Customer customer = null;
                String key = null;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    key = child.getString("CustomerCode");

                    if (!customerHashMap.containsKey(key)) {//없으면
                        customer = new Customer(child.getString("CustomerCode"),
                                child.getString("CustomerName"));
                        customerHashMap.put(key, customer);
                    } else {//있으면
                        customer = customerHashMap.get(key);
                    }
                    customer.addData(child.getString("LocationNo"), child.getString("LocationName"), child.getString("ContractNo"));
                }
                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();


                Intent i;
                if (this.type.equals("내현장")) {//진행층수 등록, 진행기준 정보관리, 현장 지원요청, 진행층수 등록(회수), 현장진행현황
                    i = new Intent(getBaseContext(), LocationTreeViewActivity.class);//todo
                } else {//담당자배정 or 담당자 배정현황
                    i = new Intent(getBaseContext(), LocationTreeViewActivitySearch.class);//todo
                }

                if (this.type.equals("미배정")) {//담당자배정
                    i.putExtra("type", "담당자배정");
                } else if (this.type.equals("모든현장")) {//담당자 배정현황
                    i.putExtra("type", "담당자배정현황");
                }

                i.putExtra("programType", programType);
                i.putExtra("hashMap", customerHashMap);


                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 작업요청내역 조회 = 나의 작업 보기
     */
    private void ClickSearchButton() {

        //사용자 ID 와 일자 조건을 통하여 데이터를 조회한다.

        //검색조건
        String userID = Users.USER_ID;
        String fromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        String toDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

        suWorders = new ArrayList<SuWorder>();

        restURL = "";

        if (searchDateType.equals("요청일")) {
            restURL = getString(R.string.service_address) + "getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/1"; //요청
        } else if (searchDateType.equals("희망일")) {
            restURL = getString(R.string.service_address) + "getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/0"; //희망
        } else {//작업일
            restURL = getString(R.string.service_address) + "getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/2"; //작업
        }

        //mProgress = ProgressDialog.show(SearchActivity.this, "Wait", "Loading...");
        new ActivityMenuTest3.ReadJSONFeedTask().execute(restURL);
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                //Log.i("ReadJSONFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetSWorderListResult");

                String LocationName = "";
                String SupervisorWoNo = "";
                String WorkDate = "";
                String StartTime = "";
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String Dong = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    LocationName = child.getString("LocationName");
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    WorkDate = child.getString("WorkDate");
                    StartTime = child.getString("StartTime");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    Dong = child.getString("Dong");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StartTime, StatusFlag, CustomerName, Supervisor, WorkTypeName, Dong));
                }
                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();

                Intent i;
                if (Users.BusinessClassCode == 9) {//음성
                    i = new Intent(getBaseContext(), SuListViewActivity2.class);
                    i.putExtra("data", suWorders);
                    i.putExtra("type", "작업");
                    i.putExtra("url", restURL);
                    i.putExtra("arrayName", "GetSWorderListResult");
                } else {//창녕
                    i = new Intent(getBaseContext(), SuListViewActivity.class);
                    i.putExtra("data", suWorders);
                    i.putExtra("type", "작업");
                    i.putExtra("url", restURL);
                    i.putExtra("arrayName", "GetSWorderListResult");
                }

                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SuWorder MakeData(String woNo, String locationName, String workDate, String startTime, String statusFlag, String customerName, String supervisor, String workTypeName, String dong) {

        SuWorder suWorder = new SuWorder();

        suWorder.WorkNo = woNo;
        suWorder.LocationName = locationName;
        suWorder.WorkDate = workDate;
        suWorder.StartTime = startTime;
        suWorder.Status = statusFlag;
        suWorder.CustomerName = customerName;
        suWorder.Supervisor = supervisor;
        suWorder.WorkTypeName = workTypeName;
        suWorder.Dong = dong;
        return suWorder;
    }

    public String readJSONFeed(String URL) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);

                }
                inputStream.close();
            } else {
                //Log.d("JSON", "Failed to download file");

            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }

        return stringBuilder.toString();
    }


    /**
     * * 배정+(작성, 확정상태)
     * * 일보확인
     * *
     * *
     */
    private void ClickSearchButton4() {

        if (GetMonthDiff() == -1) {
            Toast.makeText(ActivityMenuTest3.this, "날짜 입력이 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
            progressOFF();
            return;
        }

        if (GetMonthDiff() > 2) {
            Toast.makeText(ActivityMenuTest3.this, "일보확인은 2개월 이내에만 가능합니다.", Toast.LENGTH_SHORT).show();
            progressOFF();
            return;
        }


        suWorders = new ArrayList<SuWorder>();

        if (searchDateType.equals("요청일")) {
            searchDateNum = "1"; //요청
        } else if (searchDateType.equals("희망일")) {
            searchDateNum = "0"; //희망
        } else {
            searchDateNum = "2"; //작업
        }

        searchSupervisor = "-1";
        searchtype4 = "0";
        searchFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        searchToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;
        searchStatusFlag1 = "1";
        searchStatusFlag2 = "2";
        activityType = "확인";
        new ActivityMenuTest3.HttpAsyncTask().execute(
                getString(R.string.service_address) + "getassigndataNew");
    }

    private int GetMonthDiff() {

        int diffMonth = 0;
        int diffYear;

        if (toYear == fromYear) {//같은 해일시에
            return toMonth - fromMonth;
        } else if (fromYear > toYear) {
            //날짜 입력 잘못
            return -1;
        } else {
            diffYear = toYear - fromYear;

            diffMonth = (toMonth + 12 * diffYear) - fromMonth;
            return diffMonth;
        }
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {//todo

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String LocationName = "";
                String SupervisorWoNo = "";
                String WorkDate = "";
                String StartTime = "";
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String Dong = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    LocationName = child.getString("LocationName");
                    WorkDate = child.getString("WorkDate");
                    StartTime = child.getString("StartTime");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    Dong = child.getString("Dong");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StartTime, StatusFlag, CustomerName, Supervisor, WorkTypeName, Dong));
                }

                Intent i = new Intent(getBaseContext(), SuListViewActivity.class);//todo
                i.putExtra("data", suWorders);
                i.putExtra("type", activityType);
                i.putExtra("url", restURL);

                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            //Delete & Insert
            jsonObject.put("Supervisor", searchSupervisor);//사원번호
            jsonObject.put("FromDate", searchFromDate);//fromdate
            jsonObject.put("ToDate", searchToDate);//todate
            jsonObject.put("Type1", searchDateNum);//조회기준 조건: 요청, 희망일, 작업일
            jsonObject.put("Type2", searchStatusFlag1);//요청서 상태
            jsonObject.put("Type3", searchStatusFlag2);//요청서 상태
            jsonObject.put("Type4", searchtype4);//배정상태, "16001"이면 미배정

            jsonObject.put("SupervisorCode", Users.USER_ID);// 위에 Supervisor 랑 똑같음 근데, Supervisor =-1 일때, 사업장 어딘지 알기위해서 추가함

            json = jsonObject.toString();
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json, "UTF-8");
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 9. receive response as inputStream

            HttpEntity entity = httpResponse.getEntity();
            inputStream = entity.getContent();
            //inputStream = httpResponse.getEntity().getContent();
            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            //Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        //Log.i("result", result.toString());
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        String message = "";
        String resultCode = "";

        try {
            //.i("JSON", result);
            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }

    @Override
    public void onBackPressed() {

        backpressed.onBackPressed();
    }

}

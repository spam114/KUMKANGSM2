package com.kumkangkind.kumkangsm2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;
import com.kumkangkind.kumkangsm2.fcm.BadgeControl;
import com.kumkangkind.kumkangsm2.fcm.QuickstartPreferences;
import com.kumkangkind.kumkangsm2.fcm.RegistrationIntentService;
import com.kumkangkind.kumkangsm2.sale.ActivitySales;
import com.kumkangkind.kumkangsm2.sale.WorkType;
import com.kumkangkind.kumkangsm2.sqlite.ActivityMessageHistory;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Util;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityMenuTest3 extends BaseActivity {

    TextView txtDate;
    TextView tvVersion;

    private static final String TAG = "SearchActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    ArrayList<String> eumSungTeam = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "나의 작업보기", "일보확인", "진행기준정보 관리", "진행층수 등록", "경비등록", "현장 불만사례", "현장 지원요청", ""));
    ArrayList<String> eumSungSale = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "나의 작업보기", "일보확인", "진행기준정보 관리", "진행층수 등록", "경비등록", "현장 불만사례", "현장 지원요청", ""));
    ArrayList<String> eumSungSuper = new ArrayList<>(Arrays.asList("담당자 배정", "나의 작업보기", "진행기준정보 관리", "진행층수 등록", "경비등록", "현장 불만사례", "현장 지원요청", ""));


    ArrayList<String> ChangTeam = new ArrayList<>(Arrays.asList("담당자 배정", "담당자 배정현황", "작업요청내역 조회", "작업요청 관리", "일보확인", "경비등록", "알림 메시지", ""));
    ArrayList<String> ChangSale = new ArrayList<>(Arrays.asList("작업요청내역 조회", "작업요청 관리", "일보확인", "경비등록", "알림 메시지", ""));
    ArrayList<String> ChangSuper = new ArrayList<>(Arrays.asList("작업요청내역 조회", "경비등록", "알림 메시지", ""));

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

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    SharedPreferences pref;// 커스텀 버튼 내용을 저장
    SharedPreferences noticePref;//공지 유무를 저장



    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main4);
        tvVersion=findViewById(R.id.tvVersion);
        txtDate = findViewById(R.id.txtDate);

        SetDate();
        SetButton();

        getInstanceIdToken();
        BadgeControl.clearBadgeCount(this);
        noticePref=getSharedPreferences("NoticePref",MODE_PRIVATE);

        if(Users.BusinessClassCode==9) {//음성만 공지사항 사용
            boolean viewNotice=true;
            viewNotice=noticePref.getBoolean("viewNotice",true);

            if(viewNotice==true){
                String restURL = getString(R.string.service_address) + "getNoticeData";
                new GetNoticeData().execute(restURL);
            }



        }
    }

    //POST
    private class GetNoticeData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForNotice(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    noticeData = child.getString("AppRemark");
                }

                viewNotice();


            } catch (Exception e) {
                e.printStackTrace();
            }


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
                // message = jsonArray.getJSONObject(0).getString("Message");
                //  resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

        public String PostForNotice(String url) {
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
                jsonObject.put("AppCode", getString(R.string.app_code));//앱코드

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
    }


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

    private void viewNotice(){

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_notice, null);
            AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
            //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
            buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

            TextView tvTitle=dialogView.findViewById(R.id.tvTitle);
            try {
                tvTitle.setText("변경사항(version "+getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName+")");
            } catch (PackageManager.NameNotFoundException e) {
                tvTitle.setText("변경사항");
            }


            TextView tvContent=dialogView.findViewById(R.id.tvContent);
            tvContent.setText(noticeData);

            final AlertDialog dialog = buider.create();
            //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
            dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
            //Dialog 보이기

            dialog.show();

            Button btnOK=dialogView.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox chkNoView=dialogView.findViewById(R.id.chkNoView);

                    if(chkNoView.isChecked()){
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

        if(Users.BusinessClassCode==9)//음성이면 초기값 작업일
            searchDateType="작업일";

        String strReq = searchDateType + " ";
        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay + " ~ ";
        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

        String outTxt = strReq + strFromDate + strToDate;
        SpannableStringBuilder ssb = new SpannableStringBuilder(outTxt);

        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#efd36d")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // txtLeftCircle.setTextColor(Color.parseColor("#18A266"));
        // txtLeftCircle.setTextColor(Color.parseColor("#FFFFFF"));

        txtDate.setText(ssb);
        try {
            tvVersion.setText("version "+getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void mOnClick(View v) {

        startProgress();
        switch (v.getId()) {

            case R.id.txtDate://두개의 데이트 피커

                //Dialog에서 보여줄 입력화면 View 객체 생성 작업
                //Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                LayoutInflater inflater = getLayoutInflater();

                //res폴더>>layout폴더>>dialog_addmember.xml 레이아웃 리소스 파일로 View 객체 생성
                //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
                final View dialogView = inflater.inflate(R.layout.dialog_double_date_picker, null);

                TextView txtStart = dialogView.findViewById(R.id.txtStartDay);
                TextView txtEnd = dialogView.findViewById(R.id.txtEndDay);
                /*
                 * 스피너의 폰트, 글자색 변경을 위함
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

                        String strReq = searchDateType + " ";
                        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay + " ~ ";
                        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

                        String outTxt = strReq + strFromDate + strToDate;
                        SpannableStringBuilder ssb = new SpannableStringBuilder(outTxt);

                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#efd36d")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                        // txtLeftCircle.setTextColor(Color.parseColor("#18A266"));
                        // txtLeftCircle.setTextColor(Color.parseColor("#FFFFFF"));

                        txtDate.setText(ssb);
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

            case R.id.mailButton://메일버튼
                startActivity(new Intent(ActivityMenuTest3.this, ActivityMessageHistory.class));
                break;


            case R.id.btnEditMenu://메뉴 편집
                ArrayList<String> btnList= new ArrayList<>();
                FButton btn3= findViewById(R.id.btn3);
                FButton btn4= findViewById(R.id.btn4);
                FButton btn5= findViewById(R.id.btn5);
                FButton btn6= findViewById(R.id.btn6);
                FButton btn7= findViewById(R.id.btn7);

                btnList.add(btn3.getTag().toString());
                btnList.add(btn4.getTag().toString());
                try {
                    btnList.add(btn5.getTag().toString());
                    btnList.add(btn6.getTag().toString());
                    btnList.add(btn7.getTag().toString());
                }
                catch (Exception E){

                }
                myDefaultButtonList.remove("");
                final AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.create();
                build.setTitle("메뉴 편집");


                final String[] items = new String[myDefaultButtonList.size()];
                for (int i = 0; i < myDefaultButtonList.size(); i++)
                    items[i] = myDefaultButtonList.get(i).toString();

                final boolean[] checkedItems = new boolean[items.length];

                for (int i = 0; i < items.length; i++){
                    if(btnList.contains(items[i]))
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
                                if(i+1>stringArrayList.size()) {
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
                break;

        }

    }

    /*
     * 메뉴편집으로 선택한 커스텀 메뉴버튼들을 설정한다.
     * */
    private void refreshCustomButton(String strButton3, String strButton4, String strButton5, String strButton6, String strButton7) {

        pref = getSharedPreferences("CustomButton", MODE_PRIVATE);
        SharedPreferences.Editor customEditor = pref.edit();

        FButton btn3 = findViewById(R.id.btn3);
        FButton btn4 = findViewById(R.id.btn4);
        FButton btn5 = findViewById(R.id.btn5);
        FButton btn6 = findViewById(R.id.btn6);
        FButton btn7 = findViewById(R.id.btn7);

        if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저는 아래 버튼 두개 hidden

            btn5.setVisibility(View.INVISIBLE);
            btn6.setVisibility(View.INVISIBLE);
            btn7.setVisibility(View.INVISIBLE);
        }

        if(strButton3=="")
            btn3.setVisibility(View.INVISIBLE);
        else
            btn3.setVisibility(View.VISIBLE);

        if(strButton4=="")
            btn4.setVisibility(View.INVISIBLE);
        else
            btn4.setVisibility(View.VISIBLE);

        if(strButton5=="")
            btn5.setVisibility(View.INVISIBLE);
        else
            btn5.setVisibility(View.VISIBLE);

        if(strButton6=="")
            btn6.setVisibility(View.INVISIBLE);
        else
            btn6.setVisibility(View.VISIBLE);

        if(strButton7=="")
            btn7.setVisibility(View.INVISIBLE);
        else
            btn7.setVisibility(View.VISIBLE);

        btn3.setText("     " + strButton3);
        btn3.setTag(strButton3);
        Drawable img3 = FindImage(btn3.getTag().toString());
        btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

        btn4.setText("     " + strButton4);
        btn4.setTag(strButton4);
        Drawable img4 = FindImage(btn4.getTag().toString());
        btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);


        btn5.setText("     " + strButton5);
        btn5.setTag(strButton5);
        Drawable img5 = FindImage(btn5.getTag().toString());
        btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

        btn6.setText("     " + strButton6);
        btn6.setTag(strButton6);
        Drawable img6 = FindImage(btn6.getTag().toString());
        btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

        btn7.setText("     " + strButton7);
        btn7.setTag(strButton7);
        Drawable img7 = FindImage(btn7.getTag().toString());
        btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);

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

        startProgress();

        switch (v.getTag().toString()) {

            case "담당자 배정":
                if (Users.BusinessClassCode == 9) {//음성
                    programType = "담당자배정";
                    AssignPerson();//담당자배정
                }
                else//창녕
                    ClickSearchButton2();//담당자배정
                break;

            case "담당자 배정현황":
                if (Users.BusinessClassCode == 9) {//음성
                    programType = "담당자배정현황";
                    AssignPersonStatus();//담당자배정현황

                }
                else//창녕
                    ClickSearchButton3();//담당자배정현황
                break;

            case "작업요청내역 조회":

                ClickSearchButton();
                break;

            case "나의 작업보기":
                ClickSearchButton();
                break;

            case "작업요청 관리":
                startActivity(new Intent(ActivityMenuTest3.this, ActivitySales.class));
                break;

            case "일보확인":
                ClickSearchButton4();//일보확인
                break;

            case "진행기준정보 관리":
                programType = "진행기준정보관리";

                ClickProgressFloor();

                break;

            case "진행층수 등록":
                programType = "진행층수등록";

                ClickProgressFloor();
                break;

            case "경비등록":
                if(Users.BusinessClassCode==11)//창녕
                    startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCost2.class));
                else//음성
                    startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCostEumsung.class));
                break;

            case "현장 불만사례":
                programType = "현장불만사례";
                ClickComplain();
                break;

            case "알림 메시지":
                programType = "알림 메시지";
                startActivity(new Intent(ActivityMenuTest3.this, ActivityMessageHistory.class));
                break;

            case "현장 지원요청":
                programType = "현장지원요청";

                ClickProgressFloor();
                break;


        }
    }

    /**
     * 현장 불만사례 액티비티를 띄운다.
     */
    private void ClickComplain(){
        if(Users.LeaderType.equals("0")){//슈퍼바이저
            new ActivityMenuTest3.GetCustomerLocationByGet("내현장").execute(getString(R.string.service_address)+"getCustomerLocation/" + Users.USER_ID);
        }
        else if(Users.LeaderType.equals("1")){//팀장
            new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address)+"getCustomerLocation2/" + "모든현장");
        }
        else if(Users.LeaderType.equals("2")){//영업담당자
            new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address)+"getCustomerLocation2/" + "모든현장");
        }
    }

    /*
     * 권한에 따른 BoomButton을 셋팅한다.
     * */
    private void SetButton() {

        FButton btn3 = findViewById(R.id.btn3);
        FButton btn4 = findViewById(R.id.btn4);
        FButton btn5 = findViewById(R.id.btn5);
        FButton btn6 = findViewById(R.id.btn6);
        FButton btn7 = findViewById(R.id.btn7);


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

        if(strButton3=="")
            btn3.setVisibility(View.INVISIBLE);
        else
            btn3.setVisibility(View.VISIBLE);
        if(strButton4=="")
            btn4.setVisibility(View.INVISIBLE);
        else
            btn4.setVisibility(View.VISIBLE);
        if(strButton5=="")
            btn5.setVisibility(View.INVISIBLE);
        else
            btn5.setVisibility(View.VISIBLE);
        if(strButton6=="")
            btn6.setVisibility(View.INVISIBLE);
        else
            btn6.setVisibility(View.VISIBLE);

        if(strButton7=="")
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

                btn3.setText("     " + "담당자 배정");
                btn3.setTag("담당자 배정");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "현장 불만사례");
                btn5.setTag("현장 불만사례");
                btn5.setVisibility(View.VISIBLE);

                btn6.setText("     " + "현장 지원요청");
                btn6.setTag("현장 지원요청");
                btn6.setVisibility(View.VISIBLE);

                btn7.setText("     " + "경비등록");
                btn7.setTag("경비등록");
                btn7.setVisibility(View.VISIBLE);
            } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("1")) {//음성 팀장일때,1.담당자배정현황 2.나의 작업보기 3.일보확인 4.현장 불만사례

                btn3.setText("     " + "담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                btn6.setText("     " + "현장 불만사례");
                btn6.setTag("현장 불만사례");
                btn6.setVisibility(View.VISIBLE);

                btn7.setText("     " + "현장 지원요청");
                btn7.setTag("현장 지원요청");
                btn7.setVisibility(View.VISIBLE);
            } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("2")) {//음성 영업담당자 일때,1.담당자배정현황 2.나의 작업보기 3.일보확인 4.현장 불만사례

                btn3.setText("     " + "담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "나의 작업보기");
                btn4.setTag("나의 작업보기");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                btn6.setText("     " + "현장 불만사례");
                btn6.setTag("현장 불만사례");
                btn6.setVisibility(View.VISIBLE);

                btn7.setText("     " + "현장 지원요청");
                btn7.setTag("현장 지원요청");
                btn7.setVisibility(View.VISIBLE);
            }

            if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저일때,1.작업요청내역 조회 2. 경비등록

                btn3.setText("     " + "작업요청내역 조회");
                btn3.setTag("작업요청내역 조회");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "경비등록");
                btn4.setTag("경비등록");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "알림 메시지");
                btn5.setTag("알림 메시지");
                btn5.setVisibility(View.VISIBLE);

                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);

            }

            if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("1")) {//창녕 팀장일때,1.담당자배정 현황 2.담당자배정 3.일보확인 4.작업요청 관리

                btn3.setText("     " + "담당자 배정현황");
                btn3.setTag("담당자 배정현황");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "담당자 배정");
                btn4.setTag("담당자 배정");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "일보확인");
                btn5.setTag("일보확인");
                btn5.setVisibility(View.VISIBLE);

                btn6.setText("     " + "작업요청 관리");
                btn6.setTag("작업요청 관리");
                btn6.setVisibility(View.VISIBLE);

                btn7.setText("     " + "작업요청내역 조회");
                btn7.setTag("작업요청내역 조회");
                btn7.setVisibility(View.VISIBLE);
            }

            if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("2")) {//창녕 영업담당자 일때,1.일보확인 2.작업요청 관리 3.작업요청내역 조회 4.경비등록

                btn3.setText("     " + "일보확인");
                btn3.setTag("일보확인");
                btn3.setVisibility(View.VISIBLE);

                btn4.setText("     " + "작업요청 관리");
                btn4.setTag("작업요청 관리");
                btn4.setVisibility(View.VISIBLE);

                btn5.setText("     " + "작업요청내역 조회");
                btn5.setTag("작업요청내역 조회");
                btn5.setVisibility(View.VISIBLE);

                btn6.setText("     " + "경비등록");
                btn6.setTag("경비등록");
                btn6.setVisibility(View.VISIBLE);

                btn7.setText("     " + "알림 메시지");
                btn7.setTag("알림 메시지");
                btn7.setVisibility(View.VISIBLE);
            }

            Drawable img3 = FindImage(btn3.getTag().toString());
            btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

            Drawable img4 = FindImage(btn4.getTag().toString());
            btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);

            Drawable img5 = FindImage(btn5.getTag().toString());
            btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

            if (!(Users.BusinessClassCode == 11 && Users.LeaderType.equals("0"))) {
                Drawable img6 = FindImage(btn6.getTag().toString());
                btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

                Drawable img7 = FindImage(btn7.getTag().toString());
                btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);
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

            btn3.setText("     " + strButton3);
            btn3.setTag(strButton3);
            Drawable img3 = FindImage(btn3.getTag().toString());
            btn3.setCompoundDrawablesWithIntrinsicBounds(img3, null, null, null);

            btn4.setText("     " + strButton4);
            btn4.setTag(strButton4);
            Drawable img4 = FindImage(btn4.getTag().toString());
            btn4.setCompoundDrawablesWithIntrinsicBounds(img4, null, null, null);


            btn5.setText("     " + strButton5);
            btn5.setTag(strButton5);
            Drawable img5 = FindImage(btn5.getTag().toString());
            btn5.setCompoundDrawablesWithIntrinsicBounds(img5, null, null, null);

            btn6.setText("     " + strButton6);
            btn6.setTag(strButton6);
            Drawable img6 = FindImage(btn6.getTag().toString());
            btn6.setCompoundDrawablesWithIntrinsicBounds(img6, null, null, null);

            btn7.setText("     " + strButton7);
            btn7.setTag(strButton7);
            Drawable img7 = FindImage(btn7.getTag().toString());
            btn7.setCompoundDrawablesWithIntrinsicBounds(img7, null, null, null);
        }


        final BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setButtonEnum(ButtonEnum.SimpleCircle);

        if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("1")) { //창녕 팀장권한, 메뉴갯수 7개

            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_1);

            GoWorkRequestManagement(bmb); //작업 요청 관리
            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            GoDailyReport(bmb); //일보확인
            //GoAlformSchedule(bmb); //일정관리조회
            //GoProgressInformation(bmb); //진행기준정보관리
            // GoProgressFloor(bmb); //진행층수 관리
            GoAssignmentStatus(bmb); //담당자 배정현황
            GoRegisterExpense(bmb); //경비등록
            GoAssignment(bmb); //담당자 배정
            GoMessage(bmb);//알림 메시지

        } else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("2")) {//창녕 영업담당자 권한, 5개

            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_1);

            GoWorkRequestManagement(bmb); //작업 요청 관리
            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            GoDailyReport(bmb); //일보확인
            //GoAlformSchedule(bmb); //일정관리조회
            // GoProgressInformation(bmb); //진행기준정보관리
            //GoProgressFloor(bmb); //진행층수 관리
            GoRegisterExpense(bmb); //경비등록
            GoMessage(bmb);//알림 메시지
        } else if (Users.BusinessClassCode == 11 && Users.LeaderType.equals("0")) {//창녕 슈퍼바이저 권한, 3개

            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);

            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            //  GoAlformSchedule(bmb); //일정관리조회
            //    GoProgressFloor(bmb); //진행층수 관리
            //   GoProgressInformation(bmb); //진행기준정보관리
            GoRegisterExpense(bmb); //경비등록
            GoMessage(bmb);//알림 메시지

        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("1")) {//음성 팀장 권한, 9개
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);

            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            GoDailyReport(bmb); //일보확인
            GoProblem(bmb); //현장불만사례
            GoProgressInformation(bmb); //진행기준정보관리
            GoProgressFloor(bmb); //진행층수 관리
            GoAssignmentStatus(bmb); //담당자 배정현황
            GoRegisterExpense(bmb); //경비등록
            GoAssignment(bmb); //담당자 배정
            GoSupport(bmb);//현장 지원요청
        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("2")) {//음성 영업담당자 권한, 9개

            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);

            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            GoDailyReport(bmb); //일보확인
            GoProblem(bmb); //현장불만사례
            GoProgressInformation(bmb); //진행기준정보관리
            GoProgressFloor(bmb); //진행층수 관리
            GoAssignmentStatus(bmb); //담당자 배정현황
            GoRegisterExpense(bmb); //경비등록
            GoAssignment(bmb); //담당자 배정
            GoSupport(bmb);//현장 지원요청
        } else if (Users.BusinessClassCode == 9 && Users.LeaderType.equals("0")) {//음성 슈퍼바이저 권한, 7개
            bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_1);
            bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_1);

            GoWorkRequestSearch(bmb); //작업 요청 내역 조회
            GoProblem(bmb); //현장불만사례
            GoProgressFloor(bmb); //진행층수 관리
            GoProgressInformation(bmb); //진행기준정보관리
            GoRegisterExpense(bmb); //경비등록
            GoAssignment(bmb); //담당자 배정
            GoSupport(bmb);//현장 지원요청
        }



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


    /*
     * 저장되어있는 버튼의 권한을 체크한 후, 적절하지 않은 권한이 있다면-> 초기설정으로 실행한다. + 사용자 사업장, 권한별 버튼 지정도 함께
     * */
    private boolean CheckCustomButton(String strButton3, String strButton4, String strButton5, String strButton6, String strButton7) {


        if (Users.BusinessClassCode == 9) {//음성
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
        } else {//창녕
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

        switch (str) {
            case "담당자 배정":
                return getBaseContext().getResources().getDrawable(R.drawable.baejung);

            case "담당자 배정현황":
                return getBaseContext().getResources().getDrawable(R.drawable.hyunhwang);

            case "작업요청내역 조회":
                return getBaseContext().getResources().getDrawable(R.drawable.search_find);

            case "나의 작업보기":
                return getBaseContext().getResources().getDrawable(R.drawable.search_find);

            case "작업요청 관리":
                return getBaseContext().getResources().getDrawable(R.drawable.yochung_gwanri);

            case "일보확인":
                return getBaseContext().getResources().getDrawable(R.drawable.ilbo);

            case "진행기준정보 관리":
                return getBaseContext().getResources().getDrawable(R.drawable.orion_loading);

            case "진행층수 등록":
                return getBaseContext().getResources().getDrawable(R.drawable.orion_escalator_up);

            case "경비등록":
                return getBaseContext().getResources().getDrawable(R.drawable.kyungbi);

            case "현장 불만사례":
                return getBaseContext().getResources().getDrawable(R.drawable.boolman);

            case "알림 메시지":
                return getBaseContext().getResources().getDrawable(R.drawable.mess);

            case "현장 지원요청":
                return getBaseContext().getResources().getDrawable(R.drawable.img_support);

            default:
                return getBaseContext().getResources().getDrawable(R.drawable.baejung);

        }

    }


    /*
    작업 요청 관리
     */
    private void GoWorkRequestManagement(BoomMenuButton bmb) {
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
    }

    /*
    작업 요청 내역 조회
     */
    private void GoWorkRequestSearch(BoomMenuButton bmb) {


        int image;
        if (Users.BusinessClassCode == 11)
            image = R.drawable.work_search;
        else
            image = R.drawable.mywork;

        SimpleCircleButton.Builder builder2 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_amethyst)
                .normalImageRes(image)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startProgress();
                        ClickSearchButton();
                    }
                })
                .isRound(false);
        bmb.addBuilder(builder2);
    }


    /*
    일보확인
     */
    private void GoDailyReport(BoomMenuButton bmb) {
        SimpleCircleButton.Builder builder3 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_midnight_blue)
                .normalImageRes(R.drawable.daily_report)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ClickSearchButton4();//일보확인
                    }
                })
                .isRound(false);
        bmb.addBuilder(builder3);

    }

    /*
   현장불만사례
    */
    private void GoProblem(BoomMenuButton bmb) {
        SimpleCircleButton.Builder builder4 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_pumpkin)
                .normalImageRes(R.drawable.problem)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        programType="현장불만사례";
                        startProgress();
                        ClickComplain();
                    }
                })
                .isRound(false);

        bmb.addBuilder(builder4);
    }

    /*
    진행기준정보관리
   */
    private void GoProgressInformation(BoomMenuButton bmb) {

        SimpleCircleButton.Builder builder5 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_green_sea)
                .normalImageRes(R.drawable.progress_management)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {

                        programType = "진행기준정보관리";

                        startProgress();
                        ClickProgressFloor();

                    }
                })
                .isRound(false);

        bmb.addBuilder(builder5);
    }

    /*
    진행층수 관리
   */
    private void GoProgressFloor(BoomMenuButton bmb) {


        SimpleCircleButton.Builder builder6 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.colorPrimaryDark)
                .normalImageRes(R.drawable.progress_floor)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        programType = "진행층수등록";
                        startProgress();
                        ClickProgressFloor();
                    }
                })
                .isRound(false);

        bmb.addBuilder(builder6);
    }

    /*
    담당자 배정현황
   */
    private void GoAssignmentStatus(BoomMenuButton bmb) {

        SimpleCircleButton.Builder builder7 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_nephritis)
                .normalImageRes(R.drawable.assignment_register)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {

                        startProgress();
                        if (Users.BusinessClassCode == 9) {//음성
                            programType = "담당자배정현황";
                            AssignPersonStatus();//담당자배정현황

                        }
                        else//창녕
                            ClickSearchButton3();//담당자배정현황
                    }
                })
                .isRound(false);

        bmb.addBuilder(builder7);
    }

    /*
   경비등록
  */
    private void GoRegisterExpense(BoomMenuButton bmb) {

        SimpleCircleButton.Builder builder8 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_pomegranate)
                .normalImageRes(R.drawable.expense)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {

                        if(Users.BusinessClassCode==11)//창녕
                            startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCost2.class));
                        else//음성
                            startActivity(new Intent(ActivityMenuTest3.this, ActivityDailyCostEumsung.class));
                    }
                })
                .isRound(false);

        bmb.addBuilder(builder8);
    }

    /*
   담당자 배정
  */
    private void GoAssignment(BoomMenuButton bmb) {


        SimpleCircleButton.Builder builder9 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_peter_river)
                .normalImageRes(R.drawable.assignment)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startProgress();
                        if (Users.BusinessClassCode == 9) {//음성
                            programType = "담당자배정";
                            AssignPerson();//담당자배정

                        }
                        else//창녕
                            ClickSearchButton2();//담당자배정
                    }
                })
                .isRound(false);

        bmb.addBuilder(builder9);
    }


    /*
 알림 메시지
*/
    private void GoMessage(BoomMenuButton bmb) {


        SimpleCircleButton.Builder builder9 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_midnight_blue)
                .normalImageRes(R.drawable.txt_message)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(ActivityMenuTest3.this, ActivityMessageHistory.class));

                    }
                })
                .isRound(false);

        bmb.addBuilder(builder9);
    }

    /*
    현장 지원요청
 */
    private void GoSupport(BoomMenuButton bmb) {


        SimpleCircleButton.Builder builder9 = new SimpleCircleButton.Builder()
                .normalColorRes(R.color.fbutton_color_wisteria)
                .normalImageRes(R.drawable.txt_support)
                .buttonRadius(Util.dp2px(40))
                // Set the corner-radius of button.
                .buttonCornerRadius(Util.dp2px(20))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        programType = "현장지원요청";

                        startProgress();
                        ClickProgressFloor();

                    }
                })
                .isRound(false);

        bmb.addBuilder(builder9);
    }


    /*
     * 담당자 배정
     * */
    private void AssignPerson() {
        new ActivityMenuTest3.GetCustomerLocationByGet("미배정").execute(getString(R.string.service_address)+"getCustomerLocation2/" + Users.USER_ID);
    }

    /*
     * 담당자 배정 현황
     * */
    private void AssignPersonStatus() {
        new ActivityMenuTest3.GetCustomerLocationByGet("모든현장").execute(getString(R.string.service_address)+"getCustomerLocation2/" + "모든현장");
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
        new ActivityMenuTest3.HttpAsyncTask().execute(getString(R.string.service_address)+"getassigndata2");
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
        new ActivityMenuTest3.HttpAsyncTask().execute(getString(R.string.service_address)+"getassigndata2");
    }


    /*
     * 진행층수, 진행정보관리 데이터 가져오기
     * */
    private void ClickProgressFloor() {

        new ActivityMenuTest3.GetCustomerLocationByGet("내현장").execute(getString(R.string.service_address)+"getCustomerLocation/" + Users.USER_ID);


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
                if (this.type.equals("내현장")) {//진행층수 등록, 진행기준 정보관리, 현장 지원요청
                    i = new Intent(getBaseContext(), LocationTreeViewActivity.class);//todo
                } else {//담당자배정 or 담당자 배정현황
                    i = new Intent(getBaseContext(), LocationTreeViewActivitySearch.class);//todo
                }

                if (this.type.equals("미배정")) {//담당자배정
                    i.putExtra("type", "담당자배정");
                }
                else if (this.type.equals("모든현장")) {//담당자 배정현황
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
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/1"; //요청
        } else if (searchDateType.equals("희망일")) {
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/0"; //희망
        } else {//작업일
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/2"; //작업
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
                String StartTime="";
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String Dong="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    LocationName = child.getString("LocationName");
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    WorkDate = child.getString("WorkDate");
                    StartTime= child.getString("StartTime");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    Dong=child.getString("Dong");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StartTime, StatusFlag, CustomerName, Supervisor, WorkTypeName, Dong));
                }
                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();

                Intent i;
                if (Users.BusinessClassCode == 9){//음성
                    i = new Intent(getBaseContext(), SuListViewActivity2.class);
                    i.putExtra("data", suWorders);
                    i.putExtra("type", "작업");
                    i.putExtra("url", restURL);
                    i.putExtra("arrayName", "GetSWorderListResult");
                }

                else {//창녕
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
        suWorder.StartTime=startTime;
        suWorder.Status = statusFlag;
        suWorder.CustomerName = customerName;
        suWorder.Supervisor = supervisor;
        suWorder.WorkTypeName = workTypeName;
        suWorder.Dong=dong;
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

        if(GetMonthDiff()==-1){
            Toast.makeText(ActivityMenuTest3.this, "날짜 입력이 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
            progressOFF();
            return;
        }

        if(GetMonthDiff()>2){
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
                getString(R.string.service_address)+"getassigndataNew");
    }

    private int GetMonthDiff(){

        int diffMonth=0;
        int diffYear;

        if(toYear==fromYear){//같은 해일시에
            return toMonth-fromMonth;
        }

        else if(fromYear>toYear){
            //날짜 입력 잘못
            return -1;
        }
        else{
            diffYear=toYear-fromYear;

            diffMonth=(toMonth + 12*diffYear)-fromMonth;
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
                String StartTime="";
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
                    StartTime=child.getString("StartTime");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    Dong =  child.getString("Dong");
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

}

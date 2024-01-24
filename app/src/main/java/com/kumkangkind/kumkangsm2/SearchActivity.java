package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * 1. 해당 클래스는 데이터를 조회하기 위한 검색조건을 입력 받는다.
 * 2. 조회 버튼을 클릭할 경우, 서버와 통신하여 조건에 맞는 리스트를 가져온다.
 * 3. 리스트 뷰 액티비티로 이동한다.
 */
public class SearchActivity extends Activity {

    private static final String TAG = "SearchActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    TextView textViewDate1;
    TextView textViewDate2;
    TextView textViewVersion;
    Button buttonAssign;
    Button buttonAssign2;
    Button buttonConfirm;
    Button buttonSale;
    Button menuTest;
    ProgressDialog mProgressDialog;
    BackPressControl backpressed;
    ArrayList<SuWorder> suWorders;
    RadioButton radio1;
    RadioButton radio2;
    String restURL;
    String searchFromDate = "";
    String searchToDate = "";
    String searchSupervisor = "";
    String searchDateType = "";

    /*작업요청서 상태코드: StatusFlag
    처음 등록 시 -1(요청)
    담당자가 확인 시 0(요청(확인))
    작업일보 작업 시 1 (작성)
    작업일보 확정 시 2(확정)
    */

    String searchStatusFlag1 = "";
    String searchStatusFlag2 = "";

    String searchtype4 = "0";
    String activityType = "배정";

    String supervisorCode;
    String type1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        menuTest = findViewById(R.id.btnMenuTest);
        backpressed = new BackPressControl(this);
        buttonSale = (Button) findViewById(R.id.btnSale);
        buttonAssign = (Button) findViewById(R.id.buttonAssgin);
        buttonAssign2 = (Button) findViewById(R.id.buttonAssgin2);
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        textViewVersion.setText("version : " + Users.CurrentVersion);
        radio1 = (RadioButton) findViewById(R.id.radioButton);
        radio2 = (RadioButton) findViewById(R.id.radioButton2);

        if (Users.LeaderType.equals("1")) {
            buttonAssign.setVisibility(View.VISIBLE);
            buttonAssign2.setVisibility(View.VISIBLE);
            buttonConfirm.setVisibility(View.VISIBLE);
            buttonSale.setVisibility(View.VISIBLE);

        } else if (Users.LeaderType.equals("2")) {
            buttonAssign.setVisibility(View.VISIBLE);
            buttonConfirm.setVisibility(View.VISIBLE);
            buttonSale.setVisibility(View.VISIBLE);
            buttonAssign.setVisibility(View.INVISIBLE);
            buttonAssign2.setVisibility(View.INVISIBLE);
        } else {
            buttonAssign.setVisibility(View.INVISIBLE);
            buttonAssign2.setVisibility(View.INVISIBLE);
            buttonConfirm.setVisibility(View.INVISIBLE);
            buttonSale.setVisibility(View.INVISIBLE);

        }

        SetDate();

        getInstanceIdToken();
        BadgeControl.clearBadgeCount(this);

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                                    getString(R.string.gcm_send_message), true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {//2000Millis 뒤에 꺼지지 않았다면
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {//꺼라
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 1000);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                                    getString(R.string.token_error_message), true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 1000);
                        }
                    });
                }
            }
        };

        if (checkPlayService()) {

            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
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

    @Override
    public void onBackPressed() {

        backpressed.onBackPressed();
    }

    /**
     * 현재일자를 yy-mm-dd 형식으로 맞춤.
     */
    private void SetDate() {
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);

        //일자를 현재 일자로 세팅한다.
        textViewDate1 = (TextView) findViewById(R.id.textViewDate1);
        textViewDate1.setText(String.format("%d-%d-%d", mYear,
                mMonth + 1, mDay));

        textViewDate2 = (TextView) findViewById(R.id.textViewDate2);
        textViewDate2.setText(String.format("%d-%d-%d", mYear,
                mMonth + 1, mDay));
    }

    /**
     * 버튼 클릭
     */
    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.textViewDate1:
                ShowDatePickDialog(v.getId());
                break;
            case R.id.textViewDate2:
                ShowDatePickDialog(v.getId());
                break;
            case R.id.buttonSearch://작업요청 내역 조회
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                                "잠시만 기다려 주세요.", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                });

                ClickSearchButton();
                break;

            case R.id.buttonAssgin://담당자 배정
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                                "잠시만 기다려 주세요.", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                });
                ClickSearchButton2();//담당자배정
                break;

            case R.id.buttonAssgin2://담당자 배정현황 클릭
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                                "잠시만 기다려 주세요.", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                });
                ClickSearchButton3();//담당자 배정현황
                break;

            case R.id.buttonConfirm://일보확인
                ClickSearchButton4();//일보확인
                break;

            case R.id.btndailycostactivity:
                //경비등록 액티비티를 호출한다
                startActivity(new Intent(SearchActivity.this, ActivityDailyCost2.class));
                break;

            case R.id.btnSale://작업요청관리
                startActivity(new Intent(SearchActivity.this, ActivitySales.class));
                break;

            case R.id.btnMessageHistory:
                startActivity(new Intent(SearchActivity.this, ActivityMessageHistory.class));
                break;

            case R.id.btnMenuTest:
                startActivity(new Intent(SearchActivity.this, ActivityMenuTest3.class));
        }
    }


    /**
     * 담당자 배정(미지정)
     * 배정리스트 조회(미지정인 항목들)
     */
    private void ClickSearchButton2() {

        suWorders = new ArrayList<SuWorder>();
        if (radio1.isChecked()) {
            searchDateType = "1"; //요청
        } else if (radio2.isChecked()) {

            searchDateType = "0"; //희망
        } else {
            searchDateType = "2"; //작업
        }

        searchSupervisor = Users.USER_ID;
        searchtype4 = "16001";//담당자 미배정 상태
        searchFromDate = textViewDate1.getText().toString();
        searchToDate = textViewDate2.getText().toString();
        searchStatusFlag1 = "0";//어차피 미배정이므로 뭘넣든가 영향 x
        searchStatusFlag2 = "0";//어차피 미배정이므로 뭘넣든가 영향 x
        activityType = "배정";
        new HttpAsyncTask().execute(getString(R.string.service_address)+"getassigndata2");
    }

    /**
     * 담당자 배정현황
     * 배정된 항목들 조회(배정+ 요청, 요청(확인)상태)
     */
    private void ClickSearchButton3() {

        suWorders = new ArrayList<SuWorder>();
        if (radio1.isChecked()) {
            searchDateType = "1"; //요청일
        } else if (radio2.isChecked()) {

            searchDateType = "0"; //희망일
        } else {
            searchDateType = "2"; //작업일
        }

        searchSupervisor = Users.USER_ID;
        searchtype4 = "0";
        searchFromDate = textViewDate1.getText().toString();
        searchToDate = textViewDate2.getText().toString();
        searchStatusFlag1 = "-1";
        searchStatusFlag2 = "0";

        activityType = "배정";
        new HttpAsyncTask().execute(getString(R.string.service_address)+"getassigndata2");
    }

    /**
     * * 배정+(작성, 확정상태)
     * * 일보확인
     * *
     * *
     */
    private void ClickSearchButton4() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(SearchActivity.this, "",
                        "잠시만 기다려 주세요.", true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);
            }
        });

        suWorders = new ArrayList<SuWorder>();

        if (radio1.isChecked()) {
            searchDateType = "1";
        } else if (radio2.isChecked()) {

            searchDateType = "0";
        } else {
            searchDateType = "2";
        }
        searchSupervisor = "-1";
        searchtype4 = "0";
        searchFromDate = textViewDate1.getText().toString();
        searchToDate = textViewDate2.getText().toString();
        searchStatusFlag1 = "1";
        searchStatusFlag2 = "2";
        activityType = "확인";
        new HttpAsyncTask().execute(getString(R.string.service_address)+"getassigndata");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
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
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String SupervisorCode="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    LocationName = child.getString("LocationName");
                    WorkDate = child.getString("WorkDate");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    SupervisorCode = child.getString("SupervisorCode");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StatusFlag, CustomerName, Supervisor, WorkTypeName, SupervisorCode));
                }

                mHandler.sendEmptyMessage(0);
                Intent i = new Intent(getBaseContext(), SuListViewActivity.class);
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
            jsonObject.put("Type1", searchDateType);//조회기준 조건: 요청, 희망일, 작업일
            jsonObject.put("Type2", searchStatusFlag1);//요청서 상태
            jsonObject.put("Type3", searchStatusFlag2);//요청서 상태
            jsonObject.put("Type4", searchtype4);//배정상태, "16001"이면 미배정

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
        mProgressDialog.dismiss();
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
        }
    };


    /**
     * 작업요청내역 조회
     */
    private void ClickSearchButton() {

        //사용자 ID 와 일자 조건을 통하여 데이터를 조회한다.

        //검색조건
        String userID = Users.USER_ID;
        String fromDate = textViewDate1.getText().toString();
        String toDate = textViewDate2.getText().toString();

        suWorders = new ArrayList<SuWorder>();

        restURL = "";
        if (radio1.isChecked()) {
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/1"; //요청
        } else if (radio2.isChecked()) {
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/0"; //희망
        } else {
            restURL = getString(R.string.service_address)+"getsimpledata/" + Users.USER_ID.toString() + "/" + fromDate + "/" + toDate + "/2"; //작업
        }

        //mProgress = ProgressDialog.show(SearchActivity.this, "Wait", "Loading...");
        new ReadJSONFeedTask().execute(restURL);
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
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String SupervisorCode="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    LocationName = child.getString("LocationName");
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    WorkDate = child.getString("WorkDate");
                    StatusFlag = child.getString("StatusFlag");
                    CustomerName = child.getString("CustomerName");
                    Supervisor = child.getString("SupervisorName");
                    WorkTypeName = child.getString("WorkTypeName");
                    SupervisorCode = child.getString("SupervisorCode");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StatusFlag, CustomerName, Supervisor, WorkTypeName, SupervisorCode));
                }
                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();

                mHandler.sendEmptyMessage(0);
                Intent i = new Intent(getBaseContext(), SuListViewActivity.class);
                i.putExtra("data", suWorders);
                i.putExtra("type", "작업");
                i.putExtra("url", restURL);
                i.putExtra("arrayName", "GetSWorderListResult");
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SuWorder MakeData(String woNo, String locationName, String workDate,
                              String statusFlag, String customerName, String supervisor, String workTypeName, String supervisorCode) {

        SuWorder suWorder = new SuWorder();

        suWorder.WorkNo = woNo;
        suWorder.LocationName = locationName;
        suWorder.WorkDate = workDate;
        suWorder.Status = statusFlag;
        suWorder.CustomerName = customerName;
        suWorder.Supervisor = supervisor;
        suWorder.WorkTypeName = workTypeName;
        suWorder.SupervisorCode = supervisorCode;
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
     * 날짜를 선택할 수있는 다이아로그를 호출한다.
     */
    private void ShowDatePickDialog(int viewId) {

        if (viewId == R.id.textViewDate1)
            new DatePickerDialog(SearchActivity.this, mDateSetListener1, mYear, mMonth, mDay).show();
        else
            new DatePickerDialog(SearchActivity.this, mDateSetListener2, mYear, mMonth, mDay).show();
    }


    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    textViewDate1 = (TextView) findViewById(R.id.textViewDate1);
                    textViewDate1.setText(String.format("%d-%d-%d", mYear,
                            mMonth + 1, mDay));
                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    textViewDate2 = (TextView) findViewById(R.id.textViewDate2);
                    textViewDate2.setText(String.format("%d-%d-%d", mYear,
                            mMonth + 1, mDay));
                }
            };
}

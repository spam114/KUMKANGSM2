package com.kumkangkind.kumkangsm2.sale;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.SuWorder;
import com.kumkangkind.kumkangsm2.Users;

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
public class ActivitySales extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    TextView textViewDate1;
    TextView textViewDate2;
    TextView textViewVersion;
    ProgressDialog mProgressDialog;
    ArrayList<SuWorder> suWorders;
    RadioButton radio1;
    RadioButton radio2;
    String restURL;
    String searchFromDate = "";
    String searchToDate = "";
    String searchSupervisor ="";
    String searchDateType = "";
    String searchStatusFlag1 = "";
    String searchStatusFlag2 = "";
    String searchtype4 = "0";

    String supervisorCode;
    String type1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_main);

        radio1 = (RadioButton)findViewById(R.id.radioButton);
        radio2 = (RadioButton)findViewById(R.id.radioButton2);

        textViewVersion = (TextView)findViewById(R.id.textViewVersion);
        textViewVersion.setText("version : " + Users.CurrentVersion);

        SetDate();

        progressOFF();

        /*
        getInstanceIdToken();
        BadgeControl.clearBadgeCount(this);
        */
    }

    /**
     * 현재일자를 yy-mm-dd 형식으로 맞춤.
     */
    private void SetDate(){
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);

        //일자를 현재 일자로 세팅한다.
        textViewDate1 = (TextView)findViewById(R.id.textViewDate1);
        textViewDate1.setText(String.format("%d-%d-%d", mYear,
                mMonth + 1, mDay));

        textViewDate2 = (TextView)findViewById(R.id.textViewDate2);
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
            case R.id.buttonSearch:

                try {

                    ClickSearchButton4();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
            case R.id.buttonRegister :

                try{

                    Intent intent = new Intent(ActivitySales.this, ActivityRegister.class);
                    intent.putExtra("key", "");
                    startActivity(intent);

                }catch (Exception ex){
                    ex.printStackTrace();
                }

        }
    }

    private void ClickSearchButton4() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(ActivitySales.this, "",
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
                }, 10000);
            }
        });

        suWorders= new ArrayList<SuWorder>();

        if(radio1.isChecked()) {
            searchDateType = "1";
        }else if(radio2.isChecked()){

            searchDateType = "0";
        }else{
            searchDateType = "2";
        }
        searchSupervisor = Users.USER_ID;
        searchtype4 = "0";
        searchFromDate = textViewDate1.getText().toString();
        searchToDate = textViewDate2.getText().toString();
        searchStatusFlag1 = "-1";
        searchStatusFlag2 = "2";
        new HttpAsyncTask().execute(Users.ServiceAddress+"getassigndata3");
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

                String LocationName ="";
                String SupervisorWoNo = "";
                String WorkDate = "";
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor  = "";
                String WorkTypeName  = "";
                String SupervisorCode = "";

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
                Intent i = new Intent(getBaseContext(), ActivityWorderListView.class);
                i.putExtra("data", suWorders);
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
            jsonObject.put("Supervisor",searchSupervisor);
            jsonObject.put("FromDate", searchFromDate);
            jsonObject.put("ToDate", searchToDate);
            jsonObject.put("Type1", searchDateType);
            jsonObject.put("Type2", searchStatusFlag1);
            jsonObject.put("Type3", searchStatusFlag2);
            jsonObject.put("Type4", searchtype4);

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
        return  result;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
        }
    };


    private SuWorder MakeData(String woNo, String locationName, String workDate,
                              String statusFlag, String customerName, String supervisor, String workTypeName, String supervisorCode){

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

        if(viewId == R.id.textViewDate1)
            new DatePickerDialog(ActivitySales.this, mDateSetListener1, mYear, mMonth, mDay).show();
        else
            new DatePickerDialog(ActivitySales.this, mDateSetListener2, mYear, mMonth, mDay).show();
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

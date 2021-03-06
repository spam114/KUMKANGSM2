package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.kumkangkind.kumkangsm2.sale.WorkType;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * ?????? ??? ????????? ??????
 */
public class RegisterActivity2 extends BaseActivity {

    public static final int RESULT_TEXTVIEW1 = 0;
    public static final int RESULT_TEXTVIEW2 = 1;
    public static final int RESULT_TEXTVIEW3 = 2;
    public static final int RESULT_GALLERY1 = 3;
    public static final int RESULT_GALLERY2 = 4;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textViewImage1;
    TextView textViewImage2;
    TextView textViewTime1;
    TextView textViewTime2;
    Button buttonImageView1;
    Button buttonImageView2;
    String key = "";
    String contractNo = "";
    TextView textViewManageNo;
    TextView textViewCustomer;
    TextView textViewRealDate;
    TextView textViewDong;
    private Spinner spinnerWorkType;
    Button btnNext;
    Button btnDelete;
    SuWorder3 suworder3;
    WoImage image;
    WoItem item;
    ArrayList<WoImage> images;
    ArrayList<WoItem> items;

    RadioButton radioButton1;
    RadioButton radioButton2;

    private ArrayList<WorkType> workTypeList;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour1;
    private int mMinute1;
    private int mHour2;
    private int mMinute2;
    private String mAddress;


    LocationManager lm;
    DispLocListener locListenD;
    Uri uri;
    String type;
    String statusFlag;




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
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_workedit2);

        type = getIntent().getStringExtra("type");//?????? ?????? ?????? ????????? ????????? ?????????????????? ??? ???????????? ???????????? ???????????? ??????
        key = getIntent().getStringExtra("key");//SupervisorWoNo

        mAddress = "X";

        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locListenD = new DispLocListener();

            //CheckLocation();
        } catch (Exception e) {
            Log.i("location error", e.getMessage());
        }

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textViewTime1 = (TextView) findViewById(R.id.textViewTime1);
        textViewTime2 = (TextView) findViewById(R.id.textViewTime2);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        textViewManageNo = (TextView) findViewById(R.id.textViewManageNo);
        textViewManageNo.setText(key);
        this.btnNext.setClickable(false);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        textViewCustomer = (TextView) findViewById(R.id.textViewCustomer);
        textViewRealDate = (TextView) findViewById(R.id.textViewRealDate);
        textViewDong = findViewById(R.id.textViewDong);
        spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
        SetDate();
        SetTime();
        MakeSpinnerWorkTypeAndData();
        progressOFF();

    }

    /**
     * ????????? onpost->mHandler2 ?????? ?????? ??????????????? ??? ????????????, ?????????????????? ????????? ?????????
     */
    private void MakeSpinnerWorkTypeAndData() {
        String restURL = getString(R.string.service_address) + "worktypelistByBusinessClassCode";
        new GetWorkTypeList().execute(restURL);
    }

    //POST
    private class GetWorkTypeList extends AsyncTask<String, Void, String> {
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

                workTypeList = new ArrayList<WorkType>();
                String WorkTypeCode = "";
                String WorkTypeName = "";
                String SeqNo = "";
                int No;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    WorkTypeCode = child.getString("WorkTypeCode");
                    WorkTypeName = child.getString("WorkTypeName");
                    SeqNo = child.getString("SeqNo");
                    No = i;
                    workTypeList.add(new WorkType(WorkTypeCode, WorkTypeName, SeqNo, No));
                }
                mHandler2.sendEmptyMessage(0);

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
                jsonObject.put("BusinessClassCode", Users.BusinessClassCode);//???????????????

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


    /**
     * ??????????????? ????????????.
     */
    private Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String[] workTypes = new String[workTypeList.size()];

            for (int i = 0; i < workTypeList.size(); i++) {

                workTypes[i] = workTypeList.get(i).WorkTypeCode + "-" + workTypeList.get(i).WorkTypeName;
            }
            spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
            ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(RegisterActivity2.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
            spinnerWorkType.setAdapter(workTypeAdapter);

            if (!key.equals("????????????")) {
                SetData(key);//???????????? ????????? ??? ???????????? ?????? ??????
            } else {//???????????? ?????? ?????? ??? ??????
                SetCreateData();
                Toast.makeText(getBaseContext(), "'??????????????????' ????????? ?????????\n????????? ???????????????.", Toast.LENGTH_SHORT).show();
            }

          /*  if(suWorder2 != null)
                spinnerWorkType.setSelection(getIndex(spinnerWorkType, suWorder2.WorkTypeCode + "-" + suWorder2.WorkTypeName));*/
        }
    };

    /**
     * ?????? ??????????????? ????????? ??????
     */
    private void SetCreateData() {
        suworder3 = new SuWorder3();
        suworder3.StartTime = mYear+"-"+(mMonth+1)+"-"+mDay+" "+"09:00";
        suworder3.EndTime = mYear+"-"+(mMonth+1)+"-"+mDay+" "+"17:00";
        suworder3.StayFlag = "0";
        suworder3.WorkDescription1 = "";
        suworder3.WorkDescription2 = "";
        suworder3.WorkDescription3 = "";
        suworder3.Dong = "";
        suworder3.WorkTypeCode = 7;

        textViewRealDate.setText(mYear+"-"+(mMonth+1)+"-"+mDay);
        textViewTime1.setText("?????? 9:00");
        textViewTime2.setText("?????? 5:00");

        textViewManageNo.setText("'??????????????????' ????????? ?????? ????????? ???????????????.");
        //textViewManageNo.setTextColor(Color.rgb(255, 165, 0));// Color:Orange
        textViewManageNo.setTextColor(Color.YELLOW);
        contractNo = getIntent().getStringExtra("contractNo");
        textViewCustomer.setText(getIntent().getStringExtra("customerLocation"));
        btnNext.setText("??????????????????");
        btnNext.setClickable(true);


    }

    private void SetDate() {
        //??????????????? ??? ??? ??? ?????? ????????????.
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);
    }


    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    textViewRealDate.setText(String.format("%d-%d-%d", mYear,
                            mMonth + 1, mDay));

                    suworder3.WorkDate = textViewRealDate.getText().toString();
                }
            };

    /**
     * ??????????????? ????????? ?????????.
     */
    private void SetTime() {

        Calendar cal = new GregorianCalendar();
        mHour1 = cal.get(Calendar.HOUR_OF_DAY);
        mMinute1 = cal.get(Calendar.MINUTE);
        mHour2 = cal.get(Calendar.HOUR_OF_DAY);
        mMinute2 = cal.get(Calendar.MINUTE);
        mDay = cal.get(Calendar.DATE);
    }

    public void mOnClick(View v) {
        startProgress();

        switch (v.getId()) {
            case R.id.btnNext:

                if (key.equals("????????????")) {//???????????? ?????? ????????????

                    if(textView1.getText().toString().equals("")){
                        Toast.makeText(getBaseContext(), "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        progressOFF();
                        return;
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("???????????? ??????")
                            .setMessage("??????????????? ????????????????????????? ")
                            //.setIcon(R.drawable.ninja)
                            .setPositiveButton("??????",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SaveDataFromControl();
                                            new SetSupervisorWorderEumsungByPost().execute(getString(R.string.service_address) + "setSupervisorWorderEumsung");
                                        }
                                    })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    progressOFF();
                                    dialog.cancel();
                                    //mHandler2.sendEmptyMessage(0);

                                }
                            }).show();
                } else {

                    new AlertDialog.Builder(this).setMessage("????????????????").setCancelable(true).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //POST ????????? ??????(??????????????? ????????????)
                            //progressDialog = ProgressDialog.show(RegisterActivity2.this, "Wait", "Loading...");
                            SaveDataFromControl();
                            new HttpAsyncTask().execute(getString(R.string.service_address) + "setworderEumsung");
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }

                break;

            case R.id.textView1:

                CallEditTextActivity("????????????", v, RESULT_TEXTVIEW1);
                Log.i(this.toString(), "???????????? ??????");
                break;

            case R.id.textView2:

                CallEditTextActivity("????????????", v, RESULT_TEXTVIEW2);
                Log.i(this.toString(), "???????????? ??????");
                break;


            case R.id.textView3:

                TextView txView = (TextView) v;

                String content = "";
                if (txView.getTag() != null)
                    content = txView.getTag().toString();

                Intent intent3 = new Intent(this, VehicleRegisterActivity.class);
                intent3.putExtra("title", "????????????");
                intent3.putExtra("content", content);
                startActivityForResult(intent3, RESULT_TEXTVIEW3);

                Log.i(this.toString(), "?????????????????? ??????");
                break;

            case R.id.textViewTime1:
                ShowTimePickDialog(R.id.textViewTime1);
                break;

            case R.id.textViewTime2:
                ShowTimePickDialog(R.id.textViewTime2);
                break;

            case R.id.btnImageControl:
                if(key.equals("????????????")){
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewPhotoControlActivity();
                break;

            case R.id.btnAddItem://????????? ??????
                if(key.equals("????????????")){
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewItemControlActivity();
                break;

            case R.id.textViewRealDate:
                ShowDatePickDialog();
                break;

            case R.id.btnCommon:
                if(key.equals("????????????")){
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewCommonActivity();
                break;

            case R.id.textViewDong://??? ?????? ??????

                if (key.equals("????????????"))
                    new GetDongByContractNo().execute(getString(R.string.service_address) + "getDongByContractNo");
                else
                    new GetDongBySupervisor().execute(getString(R.string.service_address) + "getDongBySupervisorWoNo");

                break;

            case R.id.btnASItem:
                if(key.equals("????????????")){
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                new GetASItemByPost().execute(getString(R.string.service_address) + "getASItem");
                break;


            case R.id.btnDelete://????????????: ????????????, ???????????????, A/S??????, ??????????????? ????????????. ??????????????? ??????????????? ????????????.
                if(key.equals("????????????")){
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                if (Integer.parseInt(suworder3.StatusFlag) >= 2) {
                    Toast.makeText(getBaseContext(), "?????? ??? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = "??????????????? ????????????????????????? \n" +
                        "(??????: ????????? ?????????, A/S, ?????? ????????? ?????? ???????????????. \n" +
                        " *????????????????????? ????????? ????????? ???????????????.)";

                new android.app.AlertDialog.Builder(this)
                        .setTitle("???????????? ??????")
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton
                                ("YES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DeleteSupervisorWorderInfoByPost().execute(getString(R.string.service_address) + "deleteSupervisorWorderInfo");
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressOFF();
                    }
                }).show();


                break;

        }
    }

    private class SetSupervisorWorderEumsungByPost extends AsyncTask<String, Void, String> {//todo


        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {//??????????????? ??????????????? ????????????

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    key = child.getString("SupervisorWoNo");
                }

                progressOFF();
                Toast.makeText(RegisterActivity2.this, "??????????????? ?????????????????????.\n(????????????: " + key + ")", Toast.LENGTH_LONG).show();

                textViewManageNo.setText(key);
                textViewManageNo.setTextColor(Color.WHITE);
                btnNext.setText("??????");
                //?????? ?????? ?????? & TextView ??????

                statusFlag = "1";//???????????? ???????????? ?????????, StatusFlag=1: ??????
                suworder3.StatusFlag="1";
                suworder3.CustomerName=getIntent().getStringExtra("customer");
                suworder3.LocationName=getIntent().getStringExtra("location");
                suworder3.WoNo=key;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String POST2(String url) {

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

            jsonObject.put("ContractMasterNo", contractNo);//masterNo?????? ???????????? ???????????? contractNo??????.
            jsonObject.put("SupervisorCode", Users.USER_ID);
            jsonObject.put("BusinessClassCode", Users.BusinessClassCode);

            jsonObject.put("StartTime", suworder3.StartTime);
            jsonObject.put("EndTime", suworder3.EndTime);
            jsonObject.put("StayFlag", suworder3.StayFlag);
            jsonObject.put("WorkTypeCode", suworder3.WorkTypeCode);
            jsonObject.put("Dong", suworder3.Dong);
            jsonObject.put("WorkDescription1", suworder3.WorkDescription1);
            jsonObject.put("WorkDescription2", suworder3.WorkDescription2);
            jsonObject.put("WorkDescription3", suworder3.WorkDescription3);


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

        }
        return result;
    }


    private class DeleteSupervisorWorderInfoByPost extends AsyncTask<String, Void, String> {//??????: ????????????, ???????????????, A/S??????, ??????????????? ????????????. ??????????????? ??????????????? ????????????.

        @Override
        protected String doInBackground(String... urls) {

            return PostForDeleteSupervisorWorderInfo(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String resultCode = "";
                String message = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode = child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if (resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else {
                    //???????????? ?????? ???
                    setResult(RESULT_OK);
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    finish();


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String PostForDeleteSupervisorWorderInfo(String url) {
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
                jsonObject.put("SupervisorWoNo", suworder3.WoNo);
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

    private class GetASItemByPost extends AsyncTask<String, Void, String> {
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

                String SupervisorCode = "";
                String SupervisorName = "";

                String SupervisorASNo = "";
                String SupervisorWoNo = "";
                String Dong = "";
                String Ho = "";
                String HoLocation = "";
                String ItemType = "";
                String Item = "";
                String ItemSpecs = "";
                String Quantity = "";
                String Reason = "";
                String AsType = "";
                String Remark = "";
                String Actions = "";
                String ActionEmployee = "";

                ArrayList<ASItem> asItemArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    SupervisorCode = child.getString("SupervisorCode");
                    SupervisorName = child.getString("SupervisorName");

                    SupervisorASNo = child.getString("SupervisorASNo");
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    Dong = child.getString("Dong");
                    Ho = child.getString("Ho");
                    HoLocation = child.getString("HoLocation");
                    ItemType = child.getString("ItemType");
                    Item = child.getString("Item");
                    ItemSpecs = child.getString("ItemSpecs");
                    Quantity = child.getString("Quantity");
                    Reason = child.getString("Reason");
                    AsType = child.getString("AsType");
                    Remark = child.getString("Remark");
                    Actions = child.getString("Actions");
                    ActionEmployee = child.getString("ActionEmployee");
                    asItemArrayList.add(new ASItem(SupervisorCode, SupervisorName, SupervisorASNo, SupervisorWoNo, Dong, Ho, HoLocation, ItemType, Item,
                            ItemSpecs, Quantity, Reason, AsType, Remark, Actions, ActionEmployee));
                }

                Intent i = new Intent(getBaseContext(), ASItemListActivity.class);

                i.putExtra("asItemArrayList", asItemArrayList);
                i.putExtra("supervisorWoNo", key);
                i.putExtra("customer", suworder3.CustomerName);
                i.putExtra("location", suworder3.LocationName);
                i.putExtra("type", type);
                i.putExtra("statusFlag", statusFlag);

                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
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
                jsonObject.put("SupervisorWoNo", key);//????????????

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


    private class GetDongBySupervisor extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForGetDong(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                ArrayList<String> dongList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    dongList.add(child.getString("Dong"));
                }

                drawDongDialog(dongList);
                progressOFF();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * ??? ?????????????????? ?????????.
     * */
    private void drawDongDialog(ArrayList<String> dongList) {


        ArrayList<String> inputedDongList = new ArrayList<>();
        inputedDongList = (ArrayList<String>) textViewDong.getTag();

        final AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.create();
        build.setTitle("??? ??????");


        final String[] items = new String[dongList.size()];

        for (int i = 0; i < dongList.size(); i++)
            items[i] = dongList.get(i).toString();

        final boolean[] checkedItems = new boolean[items.length];

        for (int i = 0; i < items.length; i++) {

            if (inputedDongList == null) {
                checkedItems[i] = false;
            } else {
                if (inputedDongList.contains(items[i]))
                    checkedItems[i] = true;
                else
                    checkedItems[i] = false;
            }


        }


        build.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> stringArrayList = new ArrayList<>();
                String dongStr = "";

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    for (int i = 0; i < items.length; i++) {
                        if (checkedItems[i]) {
                            stringArrayList.add(items[i]);
                            dongStr += items[i] + ",";
                        }
                    }

                    if (!dongStr.equals(""))
                        dongStr = dongStr.substring(0, dongStr.length() - 1);
                    textViewDong.setTag(stringArrayList);
                    textViewDong.setText(dongStr);
                } else {
                    Toast.makeText(RegisterActivity2.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        build.setPositiveButton("??????", listener);
        build.setNegativeButton("??????", listener);
        build.create().show();
    }

    public String PostForGetDong(String url) {
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
            jsonObject.put("SupervisorWoNo", key);//??????????????????

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

    private class GetDongByContractNo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForGetDongByContractNo(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                ArrayList<String> dongList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    dongList.add(child.getString("Dong"));
                }

                drawDongDialog(dongList);
                progressOFF();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String PostForGetDongByContractNo(String url) {
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
                jsonObject.put("ContractNo", contractNo);//??????????????????

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


    private void ViewCommonActivity() {
        Intent i = new Intent(getBaseContext(), CommonActivity.class);
        i.putExtra("contractNo", contractNo);
        i.putExtra("key", key);
        i.putExtra("customerLocation", suworder3.CustomerName + "(" + suworder3.LocationName + ")");
        i.putExtra("type", type);
        i.putExtra("statusFlag", statusFlag);

        startActivity(i);
    }


    private void ShowDatePickDialog() {

        new DatePickerDialog(RegisterActivity2.this, mDateSetListener1, mYear, mMonth, mDay).show();
        progressOFF();
    }


    /**
     * ????????? ???????????? ???????????? ????????????.
     */
    private void SetData(String key) {

        String restURL = getString(R.string.service_address) + "getworder2/" + key;
        new ReadJSONFeedTask().execute(restURL);
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("ReadJSONFeedTask", "??????2");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetSupervisorWorder2Result");

                suworder3 = new SuWorder3();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    suworder3.WoNo = child.getString("SupervisorWoNo");
                    suworder3.WorkDate = child.getString("WorkDate");
                    suworder3.StartTime = child.getString("StartTime");
                    suworder3.EndTime = child.getString("EndTime");
                    suworder3.StayFlag = child.getString("StayFlag");
                    suworder3.WorkDescription1 = child.getString("WorkDescription1");
                    suworder3.WorkDescription2 = child.getString("WorkDescription2");
                    suworder3.WorkDescription3 = child.getString("WorkDescription3");
                    suworder3.WorkImage1 = child.getString("WorkImage1");
                    suworder3.WorkImage2 = child.getString("WorkImage2");
                    suworder3.GPSInfo = child.getString("GPSInfo");
                    suworder3.UpdateDate = child.getString("UpdateDate");
                    suworder3.StatusFlag = child.getString("StatusFlag");
                    suworder3.CustomerName = child.getString("CustomerName");
                    suworder3.LocationName = child.getString("LocationName");
                    suworder3.Dong = child.getString("Dong");
                    suworder3.WorkTypeCode = Integer.parseInt(child.getString("WorkTypeCode"));
                    contractNo = child.getString("ContractNo");

                    Log.i("JSON", result);
                }
                SetControlFormData(suworder3);

                statusFlag = suworder3.StatusFlag;
                if (statusFlag == null)
                    statusFlag = "2";

                if (type.equals("??????") || statusFlag.equals("2")) {//?????????????????? ????????? ?????? ?????? ??????
                    textViewRealDate.setClickable(false);
                    textViewTime1.setClickable(false);
                    textViewTime2.setClickable(false);
                    radioButton1.setClickable(false);
                    radioButton2.setClickable(false);
                    textViewDong.setClickable(false);
                    textViewDong.setHint("");
                    textView1.setClickable(false);
                    textView1.setHint("");
                    textView2.setClickable(false);
                    textView2.setHint("");
                    textView3.setClickable(false);
                    textView3.setHint("");
                    btnDelete.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                    spinnerWorkType.setClickable(false);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param suworder3
     */
    private void SetControlFormData(SuWorder3 suworder3) {

        /**
         * null??? ?????? ?????? ?????? ?????????.
         */

        if (!suworder3.StatusFlag.toString().equals("null") && !suworder3.StatusFlag.toString().equals("")) {

            if (suworder3.StatusFlag.equals("0")) {

                //??????
                this.btnNext.setClickable(true);
                this.btnNext.setText("??????");
            } else if (suworder3.StatusFlag.equals("1")) {

                //??????
                this.btnNext.setClickable(true);
                this.btnNext.setText("??????");
            } else {
                //??????
                this.btnNext.setClickable(false);
                this.btnNext.setText("?????? ??????");
            }

        }

        if (!suworder3.StartTime.toString().equals("null") && !suworder3.StartTime.toString().equals("")) {
            textViewRealDate.setText(suworder3.WorkDate);
            textViewTime1.setText(suworder3.StartTime); //????????????
        }

        if (!suworder3.EndTime.toString().equals("null") && !suworder3.EndTime.toString().equals("")) {
            textViewTime2.setText(suworder3.EndTime);  //????????????
        }
        if (!suworder3.StayFlag.toString().equals("null") && !suworder3.StayFlag.toString().equals("")) {
            if (suworder3.StayFlag.equals("1")) {
                this.radioButton1.setChecked(true); //??????
            } else {
                this.radioButton2.setChecked(true);
            }
        }
        if (!suworder3.WorkDescription1.toString().equals("null") && !suworder3.WorkDescription1.toString().equals("")) {
            textView1.setText(suworder3.WorkDescription1);
        }
        if (!suworder3.WorkDescription2.toString().equals("null") && !suworder3.WorkDescription2.toString().equals("")) {

            textView2.setText(suworder3.WorkDescription2);
        }
        if (!suworder3.WorkDescription3.toString().equals("null") && !suworder3.WorkDescription3.toString().equals("")) {

            textView3.setTag(suworder3.WorkDescription3);

            textView3.setText(MakeArraycontent(suworder3.WorkDescription3));

        }

        if (!suworder3.GPSInfo.toString().equals("null") && !suworder3.GPSInfo.toString().equals("")) {

        }

        if (!suworder3.UpdateDate.toString().equals("null") && !suworder3.UpdateDate.toString().equals("")) {

        }

        if (!suworder3.CustomerName.toString().equals("null") && !suworder3.CustomerName.toString().equals("")) {

            textViewCustomer.setText(suworder3.CustomerName + "(" + suworder3.LocationName + ")");
        }
        if (!suworder3.Dong.equals("null") && !suworder3.Dong.equals("")) {
            textViewDong.setText(suworder3.Dong);
        }

        int num = 0;

        for (WorkType workType : workTypeList) {
            if (Integer.parseInt(workType.WorkTypeCode) == suworder3.WorkTypeCode)
                num = workType.No;
        }
        spinnerWorkType.setSelection(num);

    }

    private String MakeArraycontent(String arrayString) {

        String[] arrayContent = arrayString.split("/");
        String result;

        if (arrayContent.length > 0) {
            try {
                result = "";

                for (int i = 0; i < arrayContent.length; i++) {

                    if (i % 4 == 1) {

                        result += " " + arrayContent[i];
                    }

                    if (i % 4 == 2) {
                        result += " " + arrayContent[i];
                        if (!arrayContent[i].trim().equals(""))
                            result += "km";

                    }

                    if (i % 4 == 3) {
                        result += " " + arrayContent[i];
                    }

                    if (i % 4 == 0) {
                        if (i != 0) {
                            result += "\n";
                        }
                        result += " " + arrayContent[i];

                        if (!arrayContent[i].trim().equals(""))
                            result += "???";
                    }
                }
                return result;

            } catch (Exception ex) {

                return "";
            }
        } else

            return "";
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
                Log.d("JSON", "Failed to download file");

            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }


    /**
     *
     */
    private void ViewPhotoControlActivity() {
        images = new ArrayList<WoImage>();


        String restURL = getString(R.string.service_address) + "getimagelist/" + this.key;
        new GetImageFeedTask().execute(restURL);
    }


    /*
     * ????????? ??????
     * */
    private void ViewItemControlActivity() {
        new GetAddItemByPost().execute(getString(R.string.service_address) + "getAddItem");
    }


    /**
     * ????????? ????????? ????????? ????????????.
     */
    private class GetAddItemByPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return postForAddItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String AddItemNo = "";
                String Dong = "";
                String SupervisorName = "";
                String ReceiptEmployeeCode = "";
                String ReceiptEmployeeName = "";
                String RequestDate = "";
                String HoppingDate = "";
                String SupervisorWoNo = "";

                ArrayList<AddItem> addItemArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);


                    AddItemNo = child.getString("AddItemNo");
                    Dong = child.getString("Dong");
                    SupervisorName = child.getString("SupervisorName");
                    ReceiptEmployeeCode = child.getString("ReceiptEmployeeCode");
                    ReceiptEmployeeName = child.getString("ReceiptEmployeeName");
                    RequestDate = child.getString("RequestDate");
                    HoppingDate = child.getString("HoppingDate");
                    SupervisorWoNo = child.getString("SupervisorWoNo");

                    AddItem addItem = new AddItem(AddItemNo, Dong, SupervisorName, ReceiptEmployeeCode, ReceiptEmployeeName, RequestDate, HoppingDate, SupervisorWoNo);

                    addItemArrayList.add(addItem);
                }

                Intent i = new Intent(getBaseContext(), AddItemListActivity.class);
                i.putExtra("addItemArrayList", addItemArrayList);
                i.putExtra("customer", suworder3.CustomerName);
                i.putExtra("location", suworder3.LocationName);
                i.putExtra("contractNo", contractNo);
                i.putExtra("SupervisorWoNo", key);
                i.putExtra("type", type);
                i.putExtra("statusFlag", statusFlag);

                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String postForAddItem(String url) {
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
            jsonObject.put("SupervisorWoNo", key);//??????????????????

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


    /**
     * ????????? ??????
     */
    private class GetImageFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }


        protected void onPostExecute(String result) {
            try {

                Log.i("GetImageFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetImageAllResult");

                image = new WoImage();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    image.WoNo = child.getString("SupervisorWoNo");
                    image.SeqNo = child.getString("SeqNo");
                    image.ImageName = child.getString("ImageName");
                    image.SmallImageFile = child.getString("Imagefile");
                    image.ImageFile = "";

                    images.add(new WoImage(image.WoNo, image.SeqNo, image.ImageName, image.ImageFile, image.SmallImageFile));
                }
                Intent i = new Intent(getBaseContext(), PhotoListActivity.class);
                i.putExtra("data", images);
                i.putExtra("fix", suworder3.StatusFlag);
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");
                i.putExtra("type", type);
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ??????????????? ?????? Preference???
     */
    private void SaveTemp() {

        //?????? ???????????? ????????????.
        SharedPreferences pref = getSharedPreferences(key, 0);
        SharedPreferences.Editor edit = pref.edit();


        edit.putString("time1", this.textView1.getText().toString());        //??????1
        edit.putString("time2", this.textView1.getText().toString());        //??????2
        edit.putString("stay", this.textView1.getText().toString());         //??????
        edit.putString("content1", this.textView1.getText().toString());     //????????????
        edit.putString("content2", this.textView1.getText().toString());     //????????????
        edit.putString("content3", this.textView1.getText().toString());     //????????????
        edit.putString("photo1", this.textView1.getText().toString());       //??????1
        edit.putString("photo2", this.textView1.getText().toString());       //??????2

        edit.commit();
    }

    /**
     * ?????? ????????? ????????? ????????????.
     */
    private void LoadTemp() {

        SharedPreferences pref = getSharedPreferences(key, 0);

        String time1 = pref.getString("time1", "-1");
        String time2 = pref.getString("time2", "-1");
        String stay = pref.getString("stay", "-1");
        String content1 = pref.getString("content1", "-1");
        String content2 = pref.getString("content2", "-1");
        String content3 = pref.getString("content3", "-1");
        String photo1 = pref.getString("photo1", "-1");
        String photo2 = pref.getString("photo2", "-1");
    }


    //inSampleSize ??????
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * ????????? ????????? ????????? ?????????????????? ????????????.
     */
    private void ShowTimePickDialog(int viewId) {

        if (viewId == R.id.textViewTime1)
            new TimePickerDialog(this, mTimeSetListener1, mHour1, mMinute1, true).show();

        else
            new TimePickerDialog(this, mTimeSetListener2, mHour2, mMinute2, true).show();

        progressOFF();
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mHour1 = hourOfDay;
            mMinute1 = minute;

            Date date = new Date();
            date.setHours(mHour1);
            date.setMinutes(mMinute1);
            String data = new SimpleDateFormat("HH:mm").format(date);
            textViewTime1.setText(data);
        }

    };

    TimePickerDialog.OnTimeSetListener mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mHour2 = hourOfDay;
            mMinute2 = minute;

            Date date = new Date();
            date.setHours(mHour2);
            date.setMinutes(mMinute2);
            String data = new SimpleDateFormat("HH:mm").format(date);
            textViewTime2.setText(data);
        }

    };

    /**
     * ????????? ????????? ?????? ????????? ????????? ?????????.
     *
     * @param title
     * @param v
     */
    private void CallEditTextActivity(String title, View v, int resultID) {

        TextView txView = (TextView) v;

        String content = txView.getText().toString();
        Intent intent = new Intent(this, EditTextActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        startActivityForResult(intent, resultID);
    }

    /**
     * ?????? ???????????? ????????????.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_TEXTVIEW1 && resultCode == RESULT_OK && null != data) {
            Log.i("onActivityResult", "?????? ?????????");
            String content = data.getStringExtra("content");
            textView1.setText(content);
        } else if (requestCode == RESULT_TEXTVIEW2 && resultCode == RESULT_OK && null != data) {

            String content = data.getStringExtra("content");
            Log.i("onActivityResult", "?????? ?????????");
            textView2.setText(content);

        } else if (requestCode == RESULT_TEXTVIEW3 && resultCode == RESULT_OK && null != data) {

            String content = data.getStringExtra("content");
            Log.i("onActivityResult", "?????? ?????????");

            textView3.setTag(content);
            String newContent = this.MakeArraycontent(content);
            textView3.setText(newContent);
        } else if (requestCode == RESULT_GALLERY1 && resultCode == RESULT_OK && null != data) {

            buttonImageView1.setTag(data.getData());

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String imgPath = cursor.getString(column_index);
            textViewImage1.setText("O");

            Uri uri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                textViewImage1.setTag(Base64.encodeToString(byteArray, Base64.DEFAULT));

            } catch (Exception ex) {

            }
        } else if (requestCode == RESULT_GALLERY1 && resultCode == RESULT_CANCELED) {

            buttonImageView1.setTag(null); //URI
            textViewImage1.setText("");    //?????????
            textViewImage1.setTag(null);   //byteString
        } else if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_OK && null != data) {

            buttonImageView2.setTag(data.getData());

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(column_index);
            textViewImage2.setText("O");


            Uri uri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                textViewImage2.setTag(Base64.encodeToString(byteArray, Base64.DEFAULT));

            } catch (Exception ex) {

            }
        } else if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_CANCELED) {

            buttonImageView2.setTag(null); //URI
            textViewImage2.setText("");    //?????????
            textViewImage2.setTag(null);   //byteString
        }
    }

    /**
     * ?????? ????????? ????????????.
     * ?????? ?????? : ????????????, ????????? ????????? ??????
     */
    private void CheckLocation() {

        Log.i("CheckLocation", "??????");
        try {
            //???????????? ???????????? ?????????.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("GPS", "Enabled");

            } else {

                Log.i("GPS", "Disabled");
            }

            lm.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);
            String mProvider = lm.getBestProvider(new Criteria(), true);
            Location lastKnownLocation = lm.getLastKnownLocation(mProvider);

            Log.i("??????", Double.toString(lastKnownLocation.getLatitude()));
            Log.i("??????", Double.toString(lastKnownLocation.getLongitude()));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            Log.i("City", city);
            String state = addresses.get(0).getAdminArea();
            Log.i("state", state);
            //String zip = addresses.get(0).getPostalCode();
            // Log.i("zip", zip);
            String country = addresses.get(0).getCountryName();
            Log.i("country", country);
            String address = addresses.get(0).getAddressLine(0);
            Log.i("address", address);
            mAddress = address;
            // Toast.makeText(this,  address +"===>" + String.valueOf(ConvertToTime(lastKnownLocation.getTime())), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "GPS??? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private String ConvertToTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddkkmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.valueOf(date);
    }


    private class DispLocListener implements LocationListener {

        public void onLocationChanged(Location location) {
            // TextView??? ???????????? ??????.
            Log.i("??????", Double.toString(location.getLatitude()));
            Log.i("??????", Double.toString(location.getLongitude()));
            Log.d("location", "location is null");
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            String ResultCode = "";
            String Message = "";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                }
            } catch (Exception e) {
            }
            progressOFF();//??????????????? progress??????
            Toast.makeText(getBaseContext(), Message, Toast.LENGTH_LONG).show();


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

            jsonObject.put("SupervisorWoNo", suworder3.WoNo);
            jsonObject.put("StartTime", suworder3.StartTime);
            jsonObject.put("EndTime", suworder3.EndTime);
            jsonObject.put("StayFlag", suworder3.StayFlag);
            jsonObject.put("WorkDescription1", suworder3.WorkDescription1);
            jsonObject.put("WorkDescription2", suworder3.WorkDescription2);
            jsonObject.put("WorkDescription3", suworder3.WorkDescription3);
            jsonObject.put("WorkImage1", "");
            jsonObject.put("WorkImage2", "");
            jsonObject.put("GPSInfo", suworder3.GPSInfo);
            jsonObject.put("UpdateDate", suworder3.UpdateDate);
            jsonObject.put("Dong", suworder3.Dong);
            jsonObject.put("WorkTypeCode", suworder3.WorkTypeCode);

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
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        Log.i("result", result.toString());
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
        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }


    /**
     * ????????? ???????????? ?????????.
     * * @param suworder3
     */
    private void SaveDataFromControl() {

        String stringArray[];

        String startTime = suworder3.WorkDate + " " + textViewTime1.getText().toString();
        String endTime = suworder3.WorkDate + " " + textViewTime2.getText().toString();

        suworder3.WoNo = key;//????????????????????? "????????????"??? ????????????.


        //suworder3.StartTime = textViewTime1.getText().toString() + "StartTime"
        //suworder3.EndTime =  textViewTime2.getText().toString();

        suworder3.StartTime = startTime;
        suworder3.EndTime = endTime;
        suworder3.StayFlag = radioButton1.isChecked() == true ? "1" : "0";
        suworder3.WorkDescription1 = textView1.getText().toString();
        suworder3.WorkDescription2 = textView2.getText().toString();

        if (textView3.getTag() != null)
            suworder3.WorkDescription3 = textView3.getTag().toString();

        suworder3.GPSInfo = mAddress;
        suworder3.UpdateDate = textViewTime2.getText().toString();
        suworder3.Dong = textViewDong.getText().toString();
        stringArray = spinnerWorkType.getSelectedItem().toString().split("-");
        suworder3.WorkTypeCode = (Integer.parseInt(stringArray[0]));

    }
}

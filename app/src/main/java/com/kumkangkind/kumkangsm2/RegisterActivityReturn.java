package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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

public class RegisterActivityReturn extends BaseActivity {

    public static final int RESULT_TEXTVIEW1 = 0;
    public static final int RESULT_TEXTVIEW2 = 1;
    public static final int RESULT_TEXTVIEW3 = 2;
    public static final int RESULT_GALLERY1 = 3;
    public static final int RESULT_GALLERY2 = 4;

    TextView textView1;
    TextView textView2;
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
    TextView textViewWorkType2;
    //private Spinner spinnerWorkType;
    private Spinner spinnerWorkType2;
    Button btnNext;
    Button btnDelete;
    SuWorder3 suworder3;
    WoImage image;
    WoItem item;
    ArrayList<WoImage> images;
    ArrayList<WoItem> items;

    RadioButton radioButton1;
    RadioButton radioButton2;
    LinearLayout layoutWorkType2;
    LinearLayout totalLayout;

    private ArrayList<WorkType> workTypeList;
    private ArrayList<WorkType> workTypeList2;
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
    String inputUser;

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
        setContentView(R.layout.activity_register_return);
        type = getIntent().getStringExtra("type");//수정 가능 하게 할건지 아니면 일보확인에서 쓸 수정불가 뷰용인지 구분하는 타입
        key = getIntent().getStringExtra("key");//SupervisorWoNo
        inputUser = getIntent().getStringExtra("inputUser");
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
        //spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
        spinnerWorkType2 = (Spinner) findViewById(R.id.spinnerWorkType2);
        textViewWorkType2 = findViewById(R.id.textViewWorkType2);
        totalLayout = findViewById(R.id.totalLayout);
        layoutWorkType2 = findViewById(R.id.layoutWorkType2);
        layoutWorkType2.setVisibility(View.GONE);
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    layoutWorkType2.setVisibility(View.GONE);
                else
                    layoutWorkType2.setVisibility(View.VISIBLE);
            }
        });
        SetDate();
        SetTime();
        MakeSpinnerWorkTypeAndData();
        SetEnableFalse();
        progressOFF();
    }

    private void SetEnableFalse() {
        if(!this.inputUser.equals("")){
            if (!this.inputUser.equals(Users.USER_ID)) {
                btnNext.setEnabled(false);
                btnDelete.setEnabled(false);
                textViewRealDate.setEnabled(false);
                textViewTime1.setEnabled(false);
                textViewTime2.setEnabled(false);
                radioButton1.setEnabled(false);
                radioButton2.setEnabled(false);
                spinnerWorkType2.setEnabled(false);
                textViewWorkType2.setEnabled(false);
                textView1.setEnabled(false);
                textView2.setEnabled(false);
            }
        }
    }

    /**
     * 여기의 onpost->mHandler2 에서 버튼 데이터들을 각 테스트뷰, 스피너리스트 등등에 넣어줌
     */
    private void MakeSpinnerWorkTypeAndData() {
        /*String restURL = getString(R.string.service_address) + "worktypelistByBusinessClassCode";
        new GetWorkTypeList().execute(restURL);*/

        String url = getString(R.string.service_address) + "getSupervisorWorkType2";
        ContentValues values = new ContentValues();
        values.put("Parent", "-1");
        values.put("Type", "2");
        GetSupervisorWorkType gsod = new GetSupervisorWorkType(url, values);
        gsod.execute();
    }

    //POST
    /*private class GetWorkTypeList extends AsyncTask<String, Void, String> {
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
                jsonObject.put("BusinessClassCode", Users.BusinessClassCode);//사업장번호

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
    }*/


    /**
     * 작업구분을 가져온다.
     */
    /*private Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String[] workTypes = new String[workTypeList.size()];
            for (int i = 0; i < workTypeList.size(); i++) {
                workTypes[i] = workTypeList.get(i).WorkTypeCode + "-" + workTypeList.get(i).WorkTypeName;
            }
            spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
            ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(RegisterActivityReturn.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
            spinnerWorkType.setAdapter(workTypeAdapter);

            String url = getString(R.string.service_address) + "getSupervisorWorkType2";
            ContentValues values = new ContentValues();
            values.put("Parent", "2");
            values.put("Type", "");
            GetSupervisorWorkType2 gsod = new GetSupervisorWorkType2(url, values);
            gsod.execute();

          *//*  if(suWorder2 != null)
                spinnerWorkType.setSelection(getIndex(spinnerWorkType, suWorder2.WorkTypeCode + "-" + suWorder2.WorkTypeName));*//*
        }
    };*/


    public class GetSupervisorWorkType extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetSupervisorWorkType(String url, ContentValues values) {
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

                String[] workTypes = new String[workTypeList.size()];
                for (int i = 0; i < workTypeList.size(); i++) {
                    workTypes[i] = workTypeList.get(i).WorkTypeCode + "-" + workTypeList.get(i).WorkTypeName;
                }
                // = (Spinner) findViewById(R.id.spinnerWorkType);
                ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(RegisterActivityReturn.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
                //spinnerWorkType.setAdapter(workTypeAdapter);

                String url = getString(R.string.service_address) + "getSupervisorWorkType2";
                ContentValues values = new ContentValues();
                values.put("Parent", "2");
                values.put("Type", "");
                GetSupervisorWorkType2 gsod = new GetSupervisorWorkType2(url, values);
                gsod.execute();

            } catch (Exception e) {

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    public class GetSupervisorWorkType2 extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetSupervisorWorkType2(String url, ContentValues values) {
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
                workTypeList2 = new ArrayList<WorkType>();
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
                    workTypeList2.add(new WorkType(WorkTypeCode, WorkTypeName, SeqNo, No));
                }

                String[] workTypes = new String[workTypeList2.size()];
                for (int i = 0; i < workTypeList2.size(); i++) {
                    workTypes[i] = workTypeList2.get(i).WorkTypeCode + "-" + workTypeList2.get(i).WorkTypeName;
                }
                spinnerWorkType2 = (Spinner) findViewById(R.id.spinnerWorkType2);
                ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(RegisterActivityReturn.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
                spinnerWorkType2.setAdapter(workTypeAdapter);

                if (!key.equals("생성모드")) {
                   SetData(key);//여기에서 데이터 및 수정가능 여부 구성
                } else {//생성모드 초기 셋팅 값 입력
                    SetCreateData();
                    Toast.makeText(getBaseContext(), "'작업일보생성' 버튼을 눌러야\n일보가 생성됩니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    /**
     * 일보 생성모드시 데이터 셋팅
     */
    private void SetCreateData() {
        suworder3 = new SuWorder3();
        suworder3.StartTime = mYear+"-"+(mMonth+1)+"-"+mDay+" "+"09:00";
        suworder3.EndTime = mYear+"-"+(mMonth+1)+"-"+mDay+" "+"17:00";
        suworder3.StayFlag = "1";
        suworder3.WorkDescription1 = "";
        suworder3.WorkDescription2 = "";
        suworder3.WorkDescription3 = "";
        suworder3.Dong = "";
        suworder3.WorkTypeCode = -1;
        suworder3.WorkTypeCode2="";
        textViewRealDate.setText(mYear+"-"+(mMonth+1)+"-"+mDay);
        textViewTime1.setText("오전 9:00");
        textViewTime2.setText("오후 5:00");

        textViewManageNo.setText("'회수일보생성' 버튼을 눌러 일보를 생성하세요.");
        //textViewManageNo.setTextColor(Color.rgb(255, 165, 0));// Color:Orange
        textViewManageNo.setTextColor(Color.YELLOW);
        contractNo = getIntent().getStringExtra("contractNo");
        textViewCustomer.setText(getIntent().getStringExtra("customerLocation"));
        btnNext.setText("회수일보생성");
        btnNext.setClickable(true);
    }

    private void SetDate() {
        //현재일자를 년 월 일 별로 불러온다.
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
     * 현재일자로 시간을 맞춘다.
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

                if(textViewWorkType2.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "작업구분을 선택하세요.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }

                if (key.equals("생성모드")) {//작업일보 초기 생성일시

                    if(textView1.getText().toString().equals("")){
                        Toast.makeText(getBaseContext(), "작업내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                        progressOFF();
                        return;
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("작업일보 생성")
                            .setMessage("작업일보를 생성하시겠습니까? ")
                            //.setIcon(R.drawable.ninja)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SaveDataFromControl();
                                            new SetSupervisorWorderEumsungByPost().execute(getString(R.string.service_address) + "setSupervisorWorderEumsung");
                                        }
                                    })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    progressOFF();
                                    dialog.cancel();
                                    //mHandler2.sendEmptyMessage(0);

                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(this).setMessage("등록할까요?").setCancelable(true).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //POST 명령어 호출(업데이트를 적용한다)
                            //progressDialog = ProgressDialog.show(RegisterActivityReturn.this, "Wait", "Loading...");
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

                CallEditTextActivity("작업내용", v, RESULT_TEXTVIEW1);
                Log.i(this.toString(), "작업내용 클릭");
                break;

            case R.id.textView2:

                CallEditTextActivity("특이사항", v, RESULT_TEXTVIEW2);
                Log.i(this.toString(), "특이사항 클릭");
                break;

            case R.id.textViewTime1:
                ShowTimePickDialog(R.id.textViewTime1);
                break;

            case R.id.textViewTime2:
                ShowTimePickDialog(R.id.textViewTime2);
                break;

            case R.id.btnImageControl:
                if(key.equals("생성모드")){
                    Toast.makeText(getBaseContext(), "작업일보를 먼저 생성하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewPhotoControlActivity();
                break;

            /*case R.id.btnAddItem://추가분 관리
                if(key.equals("생성모드")){
                    Toast.makeText(getBaseContext(), "작업일보를 먼저 생성하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewItemControlActivity();
                break;*/

            case R.id.textViewRealDate:
                ShowDatePickDialog();
                break;

            /*case R.id.btnCommon:
                if(key.equals("생성모드")){
                    Toast.makeText(getBaseContext(), "작업일보를 먼저 생성하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewCommonActivity();
                break;*/

            case R.id.textViewWorkType2:
                drawWorkType2List();
                break;

            /*case R.id.btnASItem:
                if(key.equals("생성모드")){
                    Toast.makeText(getBaseContext(), "작업일보를 먼저 생성하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                new GetASItemByPost().execute(getString(R.string.service_address) + "getASItem");
                break;*/


            case R.id.btnDelete://삭제버튼: 작업일보, 추가분정보, A/S정보, 사진정보를 삭제한다. 공통작성란 변경사항은 유지된다.
                if(key.equals("생성모드")){
                    Toast.makeText(getBaseContext(), "작업일보를 먼저 생성하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                if (Integer.parseInt(suworder3.StatusFlag) >= 2) {
                    Toast.makeText(getBaseContext(), "확정 된 작업일보는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = "작업일보를 삭제하시겠습니까? \n" +
                        "(주의: 등록한 추가분, A/S, 사진 정보도 함께 삭제됩니다. \n" +
                        " *공통작성란에서 변경한 정보는 유지됩니다.)";

                new android.app.AlertDialog.Builder(this)
                        .setTitle("작업일보 삭제")
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

    private void drawWorkType2List() {
        ArrayList<String> inputedWorkType2List = new ArrayList<>();
        inputedWorkType2List = (ArrayList<String>) textViewWorkType2.getTag();

        final AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.create();
        build.setTitle("작업구분");
        final String[] items = new String[workTypeList.size()];
        for (int i = 0; i < workTypeList.size(); i++)
            items[i] = workTypeList.get(i).WorkTypeCode +"-"+workTypeList.get(i).WorkTypeName;
        final boolean[] checkedItems = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            if (inputedWorkType2List == null) {
                checkedItems[i] = false;
            } else {
                if (inputedWorkType2List.contains(items[i]))
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
                String workType2Str = "";

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    for (int i = 0; i < items.length; i++) {
                        if (checkedItems[i]) {
                            stringArrayList.add(items[i]);
                            workType2Str += items[i] + ", ";
                        }
                    }
                    if (!workType2Str.equals(""))
                        workType2Str = workType2Str.substring(0, workType2Str.length() - 2);
                    textViewWorkType2.setTag(stringArrayList);
                    textViewWorkType2.setText(workType2Str);
                } else {
                    Toast.makeText(RegisterActivityReturn.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        build.setPositiveButton("선택", listener);
        build.setNegativeButton("취소", listener);
        build.create().show();
        progressOFF();
    }

    private class SetSupervisorWorderEumsungByPost extends AsyncTask<String, Void, String> {//todo
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {//작업일보를 생성한후의 행동처리

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    key = child.getString("SupervisorWoNo");
                }

                progressOFF();


                textViewManageNo.setText(key);
                textViewManageNo.setTextColor(Color.WHITE);
                btnNext.setText("저장");
                //새로 버튼 할당 & TextView 변경

                statusFlag = "1";//음성에서 작업일보 생성후, StatusFlag=1: 작성
                suworder3.StatusFlag="1";
                suworder3.CustomerName=getIntent().getStringExtra("customer");
                suworder3.LocationName=getIntent().getStringExtra("location");
                suworder3.WoNo=key;
                String pushMessageResult = "";
                if(suworder3.WorkTypeCode2.length()>0){
                    if(suworder3.WorkTypeCode2.substring(0,1).equals("7")){
                        pushMessageResult = "\n(푸시 알림 전송 완료)";
                    }
                }
                Toast.makeText(RegisterActivityReturn.this, "작업일보가 생성되었습니다."+pushMessageResult, Toast.LENGTH_LONG).show();
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

            jsonObject.put("ContractMasterNo", contractNo);//masterNo라고 써놨지만 실제로는 contractNo이다.
            jsonObject.put("SupervisorCode", Users.USER_ID);
            jsonObject.put("BusinessClassCode", Users.BusinessClassCode);
            jsonObject.put("StartTime", suworder3.StartTime);
            jsonObject.put("EndTime", suworder3.EndTime);
            jsonObject.put("StayFlag", suworder3.StayFlag);
            jsonObject.put("WorkTypeCode", suworder3.WorkTypeCode);
            jsonObject.put("WorkTypeCode2", suworder3.WorkTypeCode2);
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


    private class DeleteSupervisorWorderInfoByPost extends AsyncTask<String, Void, String> {//삭제: 작업일보, 추가분정보, A/S정보, 사진정보를 삭제한다. 공통작성란 변경사항은 유지된다.

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
                    //작업일보 삭제 시
                    setResult(RESULT_OK);
                    Toast.makeText(getBaseContext(), "작업일보가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
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
                String FromDate = "";

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
                    FromDate = child.getString("FromDate");
                    asItemArrayList.add(new ASItem(SupervisorCode, SupervisorName, SupervisorASNo, SupervisorWoNo, Dong, Ho, HoLocation, ItemType, Item,
                            ItemSpecs, Quantity, Reason, AsType, Remark, Actions, ActionEmployee, FromDate));
                }

                Intent i = new Intent(getBaseContext(), ASItemListActivity.class);

                i.putExtra("asItemArrayList", asItemArrayList);
                i.putExtra("supervisorWoNo", key);
                i.putExtra("customer", suworder3.CustomerName);
                i.putExtra("location", suworder3.LocationName);
                i.putExtra("type", type);
                i.putExtra("statusFlag", statusFlag);
                i.putExtra("contractNo", contractNo);

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
                jsonObject.put("SupervisorWoNo", key);//일보번호

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
            jsonObject.put("SupervisorWoNo", key);//작업일보번호
            jsonObject.put("ContractNo", contractNo);//작업일보번호
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

        new DatePickerDialog(RegisterActivityReturn.this, mDateSetListener1, mYear, mMonth, mDay).show();
        progressOFF();
    }


    /**
     * 서버와 통신하여 데이터를 가져온다.
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

                //Log.i("ReadJSONFeedTask", "통신2");
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
                    suworder3.WorkTypeCode2 = child.getString("WorkTypeCode2");
                    contractNo = child.getString("ContractNo");

                    //Log.i("JSON", result);
                }
                SetControlFormData(suworder3);

                statusFlag = suworder3.StatusFlag;
                if (statusFlag == null)
                    statusFlag = "2";

                if (type.equals("확인") || statusFlag.equals("2")) {//일보확인에서 사용할 뷰용 으로 변경
                    textViewRealDate.setClickable(false);
                    textViewTime1.setClickable(false);
                    textViewTime2.setClickable(false);
                    radioButton1.setClickable(false);
                    radioButton2.setClickable(false);
                    textView1.setClickable(false);
                    textView1.setHint("");
                    textView2.setClickable(false);
                    textView2.setHint("");
                    btnDelete.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                    //spinnerWorkType.setClickable(false);
                    spinnerWorkType2.setClickable(false);
                    textViewWorkType2.setClickable(false);
                    textViewWorkType2.setHint("");
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
         * null이 아닐 경우 뷰에 넣는다.
         */

        if (!suworder3.StatusFlag.equals("null") && !suworder3.StatusFlag.equals("")) {

            if (suworder3.StatusFlag.equals("0")) {

                //지시
                this.btnNext.setClickable(true);
                this.btnNext.setText("저장");
            } else if (suworder3.StatusFlag.equals("1")) {

                //작성
                this.btnNext.setClickable(true);
                this.btnNext.setText("저장");
            } else {
                //확정
                this.btnNext.setClickable(false);
                this.btnNext.setText("변경 불가");
            }

        }

        if (!suworder3.StartTime.equals("null") && !suworder3.StartTime.equals("")) {
            textViewRealDate.setText(suworder3.WorkDate);
            textViewTime1.setText(suworder3.StartTime); //시작일자
        }

        if (!suworder3.EndTime.equals("null") && !suworder3.EndTime.equals("")) {
            textViewTime2.setText(suworder3.EndTime);  //종료일자
        }
        if (!suworder3.StayFlag.equals("null") && !suworder3.StayFlag.equals("")) {
            if (suworder3.StayFlag.equals("1")) {
                this.radioButton1.setChecked(true);
            } else {
                this.radioButton2.setChecked(true);
            }
        }
        if (!suworder3.WorkDescription1.equals("null") && !suworder3.WorkDescription1.equals("")) {
            textView1.setText(suworder3.WorkDescription1);
        }
        if (!suworder3.WorkDescription2.equals("null") && !suworder3.WorkDescription2.equals("")) {

            textView2.setText(suworder3.WorkDescription2);
        }

        if (!suworder3.WorkTypeCode2.equals("null") && !suworder3.WorkTypeCode2.equals("")) {
            try{
                String[] first = suworder3.WorkTypeCode2.split("-");
                String[] second = first[1].split("/");
                int num = 0;
                for (WorkType workType : workTypeList2) {
                    if (Integer.parseInt(workType.WorkTypeCode) == Integer.parseInt(first[0]))
                        num = workType.No;
                }
                spinnerWorkType2.setSelection(num);

                ArrayList<String> stringArrayList = new ArrayList<>();
                String workType2Str = "";

                for (WorkType workType : workTypeList) {
                    for(int i=0;i<second.length;i++){
                        if(Integer.parseInt(workType.WorkTypeCode) == Integer.parseInt(second[i])){
                            stringArrayList.add(workType.WorkTypeCode+"-"+workType.WorkTypeName);
                            workType2Str += workType.WorkTypeCode+"-"+workType.WorkTypeName + ", ";
                            continue;
                        }
                    }
                }
                if (!workType2Str.equals(""))
                    workType2Str = workType2Str.substring(0, workType2Str.length() - 2);
                textViewWorkType2.setTag(stringArrayList);
                textViewWorkType2.setText(workType2Str);
            }
            catch (Exception et){
            }
        }

        if (!suworder3.GPSInfo.equals("null") && !suworder3.GPSInfo.equals("")) {

        }

        if (!suworder3.UpdateDate.equals("null") && !suworder3.UpdateDate.equals("")) {

        }

        if (!suworder3.CustomerName.equals("null") && !suworder3.CustomerName.equals("")) {

            textViewCustomer.setText(suworder3.CustomerName + "(" + suworder3.LocationName + ")");
        }
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
                            result += "→";
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


    /**
     * 추가분 마스터 정보를 가져온다.
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
            jsonObject.put("SupervisorWoNo", key);//작업일보번호

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
     * 이미지 목록
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
                boolean enableFlag=true;
                if(!inputUser.equals("")){
                    if (!inputUser.equals(Users.USER_ID)) {
                        enableFlag=false;
                    }
                }
                Intent i = new Intent(getBaseContext(), PhotoListActivity.class);
                i.putExtra("data", images);
                i.putExtra("fix", suworder3.StatusFlag);
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");
                i.putExtra("type", type);
                i.putExtra("enableFlag", enableFlag);
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 임시저장을 한다 Preference에
     */
    private void SaveTemp() {

        //키는 작업지시 번호이다.
        SharedPreferences pref = getSharedPreferences(key, 0);
        SharedPreferences.Editor edit = pref.edit();


        edit.putString("time1", this.textView1.getText().toString());        //시간1
        edit.putString("time2", this.textView1.getText().toString());        //시간2
        edit.putString("stay", this.textView1.getText().toString());         //숙박
        edit.putString("content1", this.textView1.getText().toString());     //작업내용
        edit.putString("content2", this.textView1.getText().toString());     //특이사항
        edit.putString("content3", this.textView1.getText().toString());     //차량사용
        edit.putString("photo1", this.textView1.getText().toString());       //사진1
        edit.putString("photo2", this.textView1.getText().toString());       //사진2

        edit.commit();
    }

    /**
     * 임시 저장된 내역을 불러온다.
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


    //inSampleSize 설정
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
     * 날짜를 선택할 수있는 다이아로그를 호출한다.
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
     * 타이틀 제목과 현재 입력된 내용을 넘긴다.
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
     * 결과 인텐트를 받아온다.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_TEXTVIEW1 && resultCode == RESULT_OK && null != data) {
            Log.i("onActivityResult", "결과 받아옴");
            String content = data.getStringExtra("content");
            textView1.setText(content);
        } else if (requestCode == RESULT_TEXTVIEW2 && resultCode == RESULT_OK && null != data) {

            String content = data.getStringExtra("content");
            Log.i("onActivityResult", "결과 받아옴");
            textView2.setText(content);

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
            textViewImage1.setText("");    //텍스트
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
            textViewImage2.setText("");    //텍스트
            textViewImage2.setTag(null);   //byteString
        }
    }

    /**
     * 현재 위치를 체크한다.
     * 얻을 정보 : 현재위치, 위치를 가져온 시각
     */
    private void CheckLocation() {

        Log.i("CheckLocation", "진입");
        try {
            //업데이트 해달라고 요청함.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("GPS", "Enabled");

            } else {

                Log.i("GPS", "Disabled");
            }

            lm.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);
            String mProvider = lm.getBestProvider(new Criteria(), true);
            Location lastKnownLocation = lm.getLastKnownLocation(mProvider);

            Log.i("경도", Double.toString(lastKnownLocation.getLatitude()));
            Log.i("위도", Double.toString(lastKnownLocation.getLongitude()));

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
            Toast.makeText(this, "GPS를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
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
            // TextView를 업데이트 한다.
            Log.i("경도", Double.toString(location.getLatitude()));
            Log.i("위도", Double.toString(location.getLongitude()));
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
            progressOFF();//저장완료후 progress닫기
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
            jsonObject.put("WorkTypeCode2", suworder3.WorkTypeCode2);
            jsonObject.put("SupervisorCode", Users.USER_ID);
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
     * 서버와 통신하여 넣는다.
     * * @param suworder3
     */
    private void SaveDataFromControl() {
        String startTime = suworder3.WorkDate + " " + textViewTime1.getText().toString();
        String endTime = suworder3.WorkDate + " " + textViewTime2.getText().toString();

        suworder3.WoNo = key;//생성모드시에는 "생성모드"가 들어간다.


        //suworder3.StartTime = textViewTime1.getText().toString() + "StartTime"
        //suworder3.EndTime =  textViewTime2.getText().toString();

        suworder3.StartTime = startTime;
        suworder3.EndTime = endTime;
        suworder3.StayFlag = radioButton1.isChecked() == true ? "1" : "2";
        suworder3.WorkDescription1 = textView1.getText().toString();
        suworder3.WorkDescription2 = textView2.getText().toString();

        /*if (textView3.getTag() != null)
            suworder3.WorkDescription3 = textView3.getTag().toString();*/

        suworder3.GPSInfo = mAddress;
        suworder3.UpdateDate = textViewTime2.getText().toString();

        String workTypeCode2 = spinnerWorkType2.getSelectedItem().toString().split("-")[0]+"-";
        if (textViewWorkType2.getTag() == null)
            Toast.makeText(getBaseContext(), "작업구분을 입력하세요.", Toast.LENGTH_SHORT).show();
        ArrayList<String> arrayList = (ArrayList<String>)textViewWorkType2.getTag();

        for(int i=0;i<arrayList.size();i++){
            workTypeCode2 += arrayList.get(i).split("-")[0]+"/";
        }
        suworder3.WorkTypeCode = -1;
        suworder3.WorkTypeCode2 = workTypeCode2.substring(0, workTypeCode2.length()-1);

    }
}

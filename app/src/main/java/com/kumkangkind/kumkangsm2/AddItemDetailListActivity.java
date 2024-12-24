package com.kumkangkind.kumkangsm2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class AddItemDetailListActivity extends BaseActivity {
    private ListView listView1;
    View tempView;//임시뷰

    TextView tvCustomerLocation;

    ArrayList<String> dongList;//동리스트
    String addItemNo = "";//추가분번호
    String customer = "";//거래처
    String location = "";//현장
    String supervisorWoNo="";
    ArrayList<AddItemStandard> addItemStandardArrayList;//추가분 세부분류항목
    ArrayList<AddItemDetail> addItemDetailArrayList;//추가분detail 정보
    HashMap<String, String> addMasterData;//추가분 마스터 정보
    String type = "";
    String statusFlag="";
    AddItemDetailAdapter adapter;

    //String dong = "";
    String yearMonth = "";
    String floor = "";
    String inputYearMonth = "";
    String inputFloor = "";

    String requestDate = "";
    String hoppingDate = "";
    int clickPosition = -1;
    View clickView = null;
    long clickId = 0;
    String employeeNo;
    Button btnEmployee;
    Button btnRequestDate;
    Button btnHoppingDate;
    Button btnDelete;
    Button btnEdit;
    Button btnAdd;
    Button btnCopy;
    Spinner dongSpinner;
    boolean initDongFlag=true;

    /**
     * return data 셋팅
     * 1. 초기 액티비티 불러올때
     * 2. 저장 버튼을 눌렀을 시
     */
    String returnAddItemNo;
    String returnDong;
    String returnSupervisorName;
    String returnReceiptEmployeeName;
    String returnReceiptEmployeeCode;
    String returnRequestDate;
    String returnHoppingDate;
    String returnSupervisorWoNo;


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
        }
    };

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
        setContentView(R.layout.listview_additem_detail);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);

        type= getIntent().getStringExtra("type");
        statusFlag=getIntent().getStringExtra("statusFlag");

        supervisorWoNo = getIntent().getStringExtra("supervisorWoNo");
        customer = getIntent().getStringExtra("customer");
        location = getIntent().getStringExtra("location");
        dongList = (ArrayList<String>) getIntent().getSerializableExtra("dongList");
        addItemNo = getIntent().getStringExtra("addItemNo");

        addItemStandardArrayList= new ArrayList<>();
        addItemDetailArrayList = (ArrayList<AddItemDetail>) getIntent().getSerializableExtra("addItemDetailArrayList");
        addMasterData = (HashMap<String, String>) getIntent().getSerializableExtra("addMasterData");
        this.employeeNo = addMasterData.get("employeeCode");
        this.requestDate = addMasterData.get("requestDate");
        this.hoppingDate = addMasterData.get("hoppingDate");
        tvCustomerLocation.setText(customer + " - " + location);

        btnEmployee = findViewById(R.id.btnEmployee);
        btnRequestDate = findViewById(R.id.btnRequestDate);
        btnHoppingDate = findViewById(R.id.btnHoppingDate);
        btnDelete= findViewById(R.id.btnDelete);
        btnEdit=findViewById(R.id.btnEdit);
        btnAdd=findViewById(R.id.btnAdd);
        btnCopy=findViewById(R.id.btnCopy);



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickPosition==-1)
                    Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else
                    ShowDeleteDialog();

            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickPosition==-1)
                    Toast.makeText(getBaseContext(), "수정할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else
                    ShowEditDialog();

            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickPosition==-1)
                    Toast.makeText(getBaseContext(), "복사할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else
                    ShowCopyDialog();
            }
        });

        listView1 = (ListView) findViewById(R.id.listView1);
        new GetAddItemStandard().execute(Users.ServiceAddress+"getAddItemStandard");//추가분 세부분류 가져옴
        adapter = new AddItemDetailAdapter(AddItemDetailListActivity.this, R.layout.listview_additem_detail_row, addItemDetailArrayList);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);


        SetButtonText();
        setReturnData(addItemNo, dongSpinner.getSelectedItem().toString(), Users.UserName, employeeNo, addMasterData.get("employeeName"),requestDate, hoppingDate, supervisorWoNo);

        if(type.equals("확인") || statusFlag.equals("2")){
            HorizontalScrollView scrollView=findViewById(R.id.scroll);
            LinearLayout layout=findViewById(R.id.layout);

            dongSpinner.setClickable(false);
            dongSpinner.setEnabled(false);
            btnEmployee.setClickable(false);
            btnRequestDate.setClickable(false);
            btnHoppingDate.setClickable(false);
            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

            LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.weight=8;
            LinearLayout.LayoutParams params2 =  (LinearLayout.LayoutParams) layout.getLayoutParams();
            params2.weight=0;

        }

        progressOFF();

    }

    public void mOnClick(View v) {
        startProgress();

        switch (v.getId()) {

            case R.id.btnEmployee://사원 search창
                ShowSearchEmployeeDialog();
                break;

            case R.id.btnRequestDate://요청일 설정
                ShowDatePickDialog(v.getId());
                break;

            case R.id.btnHoppingDate://희망일 설정
                ShowDatePickDialog(v.getId());
                break;


            case R.id.btnAdd://추가
                ShowAddDialog();
                break;



        }
    }

    /**
     * 날짜를 선택할 수있는 다이아로그를 호출한다.
     */
    private void ShowDatePickDialog(int viewId) {

        String[] dateArray;
        int mYear;
        int mMonth;
        int mDay;
        GregorianCalendar today = new GregorianCalendar();

        if (viewId == R.id.btnRequestDate) {
            if (!requestDate.equals("")) {
                dateArray = requestDate.split("-");
                mYear = Integer.parseInt(dateArray[0]);
                mMonth = Integer.parseInt(dateArray[1]) - 1;
                mDay = Integer.parseInt(dateArray[2]);
            } else {
                mYear = today.get(today.YEAR);
                mMonth = today.get(today.MONTH);
                mDay = today.get(today.DAY_OF_MONTH);
            }

            DatePickerDialog dtp = new DatePickerDialog(AddItemDetailListActivity.this, mDateSetListener1, mYear, mMonth, mDay);
            dtp.show();
        } else {
            if (!hoppingDate.equals("")) {
                dateArray = hoppingDate.split("-");
                mYear = Integer.parseInt(dateArray[0]);
                mMonth = Integer.parseInt(dateArray[1]) - 1;
                mDay = Integer.parseInt(dateArray[2]);
            } else {
                mYear = today.get(today.YEAR);
                mMonth = today.get(today.MONTH);
                mDay = today.get(today.DAY_OF_MONTH);
            }

            DatePickerDialog dtp = new DatePickerDialog(AddItemDetailListActivity.this, mDateSetListener2, mYear, mMonth, mDay);
            dtp.show();
        }
        progressOFF();

    }


    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {//requestDate: 요청일
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    new UpdateAddItemMasterRequestDateByPost(year,monthOfYear+1,dayOfMonth).execute(Users.ServiceAddress+"updateAddItemMasterRequestDate");
                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    new UpdateAddItemMasterHoppingDateByPost(year,monthOfYear+1,dayOfMonth).execute(Users.ServiceAddress+"updateAddItemMasterHoppingDate");
                }
            };

    private class UpdateAddItemMasterRequestDateByPost extends AsyncTask<String, Void, String> {

        int year;
        int month;
        int day;

        public UpdateAddItemMasterRequestDateByPost(int year, int month, int day){
            this.year=year;
            this.month=month;
            this.day=day;
        }

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

                String resultCode = "";
                String message="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode=child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if(resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else{
                    requestDate = year + "-" + month  + "-" + day;
                    btnRequestDate.setText("요청일\n\"" + requestDate.toString() + "\"");
                    returnRequestDate= requestDate;
                    Toast.makeText(getBaseContext(),"요청일 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }


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
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("RequestDate", year + "-" + month  + "-" + day);//수신자사원번호
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
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
        }
    }


    private class UpdateAddItemMasterHoppingDateByPost extends AsyncTask<String, Void, String> {

        int year;
        int month;
        int day;

        public UpdateAddItemMasterHoppingDateByPost(int year, int month, int day){
            this.year=year;
            this.month=month;
            this.day=day;
        }

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

                String resultCode = "";
                String message="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode=child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if(resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else{

                    hoppingDate = year + "-" + month  + "-" + day;
                    btnHoppingDate.setText("희망일\n\"" + hoppingDate.toString() + "\"");
                    returnHoppingDate=hoppingDate;

                    Toast.makeText(getBaseContext(),"희망일 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }


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
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("HoppingDate", year + "-" + month  + "-" + day);//수신자사원번호
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
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
        }
    }


    /**
     * 사원 검색 다이얼로그
     */
    private void ShowSearchEmployeeDialog() {

        SearchEmployeeDialog searchEmployeeDialog = new SearchEmployeeDialog(this, addItemNo);
        searchEmployeeDialog.show();
        searchEmployeeDialog.setDialogResult(new SearchEmployeeDialog.OnDialogResult() {
            @Override
            public void finish(String eNo, String employeeName) {//다이얼로그에서 반환한 값
                employeeNo = eNo;
                btnEmployee.setText("수신인\n\"" + employeeName + "\"");
                returnReceiptEmployeeCode=eNo;
                returnReceiptEmployeeName=employeeName;


            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = searchEmployeeDialog.getWindow();
        int x = (int) (size.x * 0.9f);

        window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);

        progressOFF();

    }

    private void SetButtonText() {
        dongSpinner = findViewById(R.id.dongSpinner);


        int dongNum = 0;

        if (addMasterData.get("dong").toString().equals(""))
            dongList.add("동선택");
        for (String dong : dongList) {

            if (dong.equals(addMasterData.get("dong").toString()))
                break;
            dongNum++;
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                dongList);

        dongSpinner.setAdapter(adapter);
        if (dongList.contains("동선택"))
            dongNum = dongList.size() - 1;

        dongSpinner.setSelection(dongNum);


        dongSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!initDongFlag)
                    new UpdateAddItemMasterDongByPost(dongSpinner.getSelectedItem().toString()).execute(Users.ServiceAddress+"updateAddItemMasterDong");
                else
                    initDongFlag=false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if (addMasterData.get("employeeName").toString().equals(""))
            btnEmployee.setText("수신인\n\"Click\"");
        else
            btnEmployee.setText("수신인\n\"" + addMasterData.get("employeeName").toString() + "\"");

        if (requestDate.equals(""))
            btnRequestDate.setText("요청일\n\"Click\"");
        else
            btnRequestDate.setText("요청일\n\"" + requestDate.toString() + "\"");

        if (hoppingDate.equals(""))
            btnHoppingDate.setText("희망일\n\"Click\"");
        else
            btnHoppingDate.setText("희망일\n\"" + hoppingDate.toString() + "\"");


    }


    private class UpdateAddItemMasterDongByPost extends AsyncTask<String, Void, String> {
        String dong;

        public UpdateAddItemMasterDongByPost(String dong){
            this.dong=dong;
        }

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

                String resultCode = "";
                String message="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode=child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if(resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else{
                    returnDong= dong;
                    Toast.makeText(getBaseContext(),"동 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }


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
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("Dong", dong);//동
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
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
        }
    }


    private void ShowDeleteDialog() {
        final AddItemDetail addItemDetail;
        try {
            addItemDetail = addItemDetailArrayList.get(clickPosition);

            String message = "세대: " + addItemDetail.Ho + "\n" +
                    "위치: " + addItemDetail.HoLocation + "\n" +
                    "품명: " + addItemDetail.Part + "\n" +
                    "수량: " + addItemDetail.Qty + "\n" + "항목 정보를 삭제하시겠습니까?";

            new android.app.AlertDialog.Builder(tempView.getContext())
                    .setTitle("추가분 세부항목 삭제")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton
                            ("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DeleteAddItemDetail(addItemDetail.AddItemNo, addItemDetail.SeqNo, clickPosition).execute(Users.ServiceAddress+"deleteAddItemDetail");
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        catch (IndexOutOfBoundsException e){
            clickPosition=-1;
            Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "삭제를 하려면, 항목을 길게 눌러주시기 바랍니다.", Toast.LENGTH_SHORT).show();

    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            tempView=view;
            clickPosition=position;

            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 /*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*/

        }
    };


    /**
     * 추가분 세부 항목을 등록한다.
     * */
    private void ShowAddDialog() {

        AddItemDetailDialog addItemDetailDialog = new AddItemDetailDialog(this, addItemStandardArrayList,addItemNo);
        addItemDetailDialog.show();
        addItemDetailDialog.setDialogResult(new AddItemDetailDialog.OnDialogResult() {
            @Override
            public void finish(AddItemDetail addItemDetail) {//다이얼로그에서 반환한 값
                addItemDetailArrayList.add(addItemDetail);
                adapter.notifyDataSetChanged();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = addItemDetailDialog.getWindow();
        int x = (int) (size.x * 0.95f);

        window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);
        progressOFF();

    }


    /**
     * 추가분 세부 수정 다이얼로그를 부른다.
     */
    private void ShowEditDialog() {
        try {
            AddItemDetail tempDetailItem = addItemDetailArrayList.get(clickPosition);

            AddItemDetailDialog addItemDetailDialog = new AddItemDetailDialog(this, addItemStandardArrayList, addItemNo, tempDetailItem, true);
            addItemDetailDialog.show();
            addItemDetailDialog.setDialogResult(new AddItemDetailDialog.OnDialogResult() {
                @Override
                public void finish(AddItemDetail addItemDetail) {//다이얼로그에서 반환한 값

                    addItemDetailArrayList.set(clickPosition, addItemDetail);
                    adapter.notifyDataSetChanged();
                }
            });

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = addItemDetailDialog.getWindow();
            int x = (int) (size.x * 0.9f);

            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);
            progressOFF();

        }
        catch (IndexOutOfBoundsException e){
            Toast.makeText(getBaseContext(), "수정 할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 추가분 세부 복사 다이얼로그를 부른다.
     */
    private void ShowCopyDialog() {
        final AddItemDetail addItemDetail;
        try {
            addItemDetail = addItemDetailArrayList.get(clickPosition);

            String message = "세대: " + addItemDetail.Ho + "\n" +
                    "위치: " + addItemDetail.HoLocation + "\n" +
                    "품명: " + addItemDetail.Part + "\n" +
                    "수량: " + addItemDetail.Qty + "\n" + "항목 정보를 복사 하시겠습니까?";

            new android.app.AlertDialog.Builder(tempView.getContext())
                    .setTitle("추가분 세부항목 복사")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton
                            ("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new SetAddItemDetailByPost(addItemDetail).execute(Users.ServiceAddress+"setAddItemDetail");
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        catch (IndexOutOfBoundsException e){
            clickPosition=-1;
            Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "삭제를 하려면, 항목을 길게 눌러주시기 바랍니다.", Toast.LENGTH_SHORT).show();
    }



    private class SetAddItemDetailByPost extends AsyncTask<String, Void, String> {
        AddItemDetail addItemDetail;

        public SetAddItemDetailByPost(AddItemDetail addItemDetail){
            this.addItemDetail=addItemDetail;
        }

        @Override
        protected String doInBackground(String... urls) {

            return PostForSetAddItemDetail(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getContext(), "등록 되었습니다.", Toast.LENGTH_SHORT).show();

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ResultCode="true";
                String Message="";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                }

                if(ResultCode.equals("false")) {
                    Toast.makeText(getBaseContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {//저장성공
                    Toast.makeText(getBaseContext(), "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    AddItemDetail itemDetail= new AddItemDetail(addItemNo,addItemDetail.SeqNo,addItemDetail.Ho,addItemDetail.HoLocation,
                            addItemDetail.AddType,addItemDetail.Qty,
                         addItemDetail.Remark,addItemDetail.Part);

                    addItemDetailArrayList.add(itemDetail);
                    adapter.notifyDataSetChanged();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        public String PostForSetAddItemDetail(String url) {
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


                int addType=-1;
                for (AddItemStandard addItemStandard:addItemStandardArrayList) {
                    if(addItemDetail.AddType.equals(addItemStandard.Name))
                        addType=Integer.parseInt(addItemStandard.StandardNo);
                }

                //Delete & Insert
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("Ho", addItemDetail.Ho);//세대
                jsonObject.put("HoLocation", addItemDetail.HoLocation);//위치
                jsonObject.put("AddType", addType);//추가분구분
                jsonObject.put("Qty", addItemDetail.Qty);//수량
                jsonObject.put("Remark", addItemDetail.Remark);//현상
                jsonObject.put("Part", addItemDetail.Part);//추가분구분
                jsonObject.put("SupervisorCode", Users.USER_ID);//슈퍼바이저코드

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
     * 세부사항 구분값을 가져온다.
     */
    private class GetAddItemStandard extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForAddItemStandard(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {

                JSONArray jsonArray = new JSONArray(result);
                String StandardNo="";
                String Name="";
                AddItemStandard addItemStandard;

                for (int i = 0; i < jsonArray.length(); i++) {



                    JSONObject child = jsonArray.getJSONObject(i);
                    StandardNo = child.getString("StandardNo");
                    Name=child.getString("Name");

                    addItemStandard= new AddItemStandard(StandardNo,Name);
                    addItemStandardArrayList.add(addItemStandard);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 세부항목 구분 가져오기
     */
    public String PostForAddItemStandard(String url) {
        InputStream inputStream = null;
        String result = "";
        String AddItemNo = "";

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            //Delete & Insert

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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {//문제없을시, 추가분 번호를 return 한다.
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
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            message = jsonArray.getJSONObject(0).getString("Message");

        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }


    /*AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String mes = items[position -1].WorkNo;
            //이동한다
            //String key = items[position - 1].WorkNo;
          *//*  dong = dongArrayList.get(position).Dong;

            yearMonth = dongArrayList.get(position).ProgressYearMonth;//입력 년월로 수정
            floor = dongArrayList.get(position).ProgressFloor;//입력 층으로 수정*//*

            clickPosition = position;
            clickView = view;
            clickId = id;
            view.setBackgroundColor(Color.parseColor("#DEB887"));

 *//*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*//*

        }
    };*/


    private class DeleteAddItemDetail extends AsyncTask<String, Void, String> {

        String addItemNo;//추가분 번호
        String seqNo;//순번
        int position;//리스트상에서의 위치

        public DeleteAddItemDetail(String addItemNo, String seqNo, int position){
            this.addItemNo=addItemNo;
            this.seqNo=seqNo;
            this.position=position;
        }

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

                String resultCode = "";
                String message="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode=child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if(resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else{

                    addItemDetailArrayList.remove(position);
                    adapter.notifyDataSetChanged();

                    if(addItemDetailArrayList.size()!=0){//지우고난후 리스트에 자료가 있을시!

                    }
                    else{//지우고난후 없을시
                        clickPosition=-1;
                    }

                    Toast.makeText(getBaseContext(),"항목이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                }


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
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("SeqNo", seqNo);//순번
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
     * 결과 인텐트를 받아온다.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            //int seqNo = data.getIntExtra("seqNo", -1);
            //   suWorders.get(seqNo).Supervisor  = data.getStringExtra("SupervisorName");
            //data.getStringExtra("SupervisorCode");
            this.adapter.notifyDataSetChanged();
        }
    }


    /**
     * return data 셋팅
     * 1. 초기 액티비티 불러올때
     *
     * 뒤로가기로 복귀시 내보낼 데이터들을 셋팅한다.
     */
    public void setReturnData(String AddItemNo, String Dong, String SupervisorName,
                               String ReceiptEmployeeCode, String ReceiptEmployeeName,String RequestDate,
                              String HoppingDate, String SupervisorWoNo) {
        returnAddItemNo = AddItemNo;
        returnDong = Dong;
        returnSupervisorName = SupervisorName;
        returnReceiptEmployeeCode = ReceiptEmployeeCode;
        returnReceiptEmployeeName = ReceiptEmployeeName;
        returnRequestDate = RequestDate;
        returnHoppingDate = HoppingDate;
        returnSupervisorWoNo=SupervisorWoNo;
    }

    @Override
    public void onBackPressed() {

        Intent i = getIntent();

        AddItem addItem= new AddItem(returnAddItemNo,returnDong,returnSupervisorName,
                returnReceiptEmployeeCode,returnReceiptEmployeeName,returnRequestDate,returnHoppingDate, returnSupervisorWoNo);

        i.putExtra("addItem",addItem);
        setResult(RESULT_OK,i);

        super.onBackPressed();//맨윗줄에 쓰면 reslutcode, data가 제대로 넘어가지 않는 현상 발생 2018-10-26
        finish();


    }

}

package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 1. 해당 클래스는 작업지시 목록을 보인다.
 * 2. 항목을 클릭할 경우, 서버와 통신하여 해당 데이터를 가져온다.
 */
public class SuListViewActivity2 extends BaseActivity {//음성 용

    final int REQUEST_CODE_VIEW_ILBO = 2;
    final int REQUEST_CODE_CREATE_ILBO = 3;
    private ListView listView1;
    //private SuWorder[] items;
    TextView textViewUserName;
    String type = "";
    String restURL = "";
    String arrayName = "";
    String programType = "";
    ArrayList<SuWorder> suWorders;
    ProgressDialog mProgressDialog;
    SwListVIewAdapter2 adapter;
    Button btnIlbo;
    Button btnHelp;

    int clickPosition = -1;


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
        setContentView(R.layout.listview2);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        btnIlbo = findViewById(R.id.btnIlbo);
        btnHelp = findViewById(R.id.btnHelp);
        textViewUserName.setText(Users.UserName);
        type = getIntent().getStringExtra("type");
        restURL = getIntent().getStringExtra("url");
        arrayName = getIntent().getStringExtra("arrayName");
        programType = getIntent().getStringExtra("programType");
        if (Users.BusinessClassCode == 9)//음성이면 일보작성 버튼 생성
            btnIlbo.setVisibility(View.VISIBLE);
        makeSWList();
        listView1 = (ListView) findViewById(R.id.listView1);
        adapter = new SwListVIewAdapter2(SuListViewActivity2.this, R.layout.listview_row2, suWorders);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);
        progressOFF();
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnIlbo:
                startProgress();
                MakeDailyReport();
                break;
            case R.id.btnHelp:
                new AlertDialog.Builder(this).setMessage("작업일보가 보이지 않을시, \n메인화면의 날짜를 확인한 후,\n변경하시기 바랍니다.").setCancelable(true).
                        setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
        }
    }

    private void MakeDailyReport() {
        new GetCustomerLocationByGet("내현장").execute(Users.ServiceAddress + "getCustomerLocation/" + Users.USER_ID);
    }


    private class GetCustomerLocationByGet extends AsyncTask<String, Void, String> {
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
                jsonArray = jsonObject.optJSONArray("GetCustomerLocationResult");

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
                    customer.addData(child.getString("LocationNo"), child.getString("LocationName"), child.getString("ContractNo"), child.getString("LocationName2"));
                }
                Intent i;
                i = new Intent(getBaseContext(), LocationTreeViewActivity.class);//todo
                i.putExtra("programType", programType);
                i.putExtra("hashMap", customerHashMap);
                startActivityForResult(i, REQUEST_CODE_CREATE_ILBO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {//리스트뷰 클릭
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startProgress();

            clickPosition = position;

            String key = suWorders.get(position).WorkNo;


            Intent intent;
            if (suWorders.get(position).Status.equals("1") || suWorders.get(position).Status.equals("2")) {// 상태: 작성 일시 -> 작업요청서 액티비티 생략하고 바로 작업일보 작성으로~
                if(programType.equals("일보작성")){
                    intent = new Intent(SuListViewActivity2.this, RegisterActivity2.class);
                    intent.putExtra("type", "작업");
                    intent.putExtra("key", key);
                    startActivityForResult(intent, REQUEST_CODE_VIEW_ILBO);
                }
                else{//programType: 회수일보작성
                    intent = new Intent(SuListViewActivity2.this, RegisterActivityReturn.class);
                    intent.putExtra("type", "작업");
                    intent.putExtra("key", key);
                    intent.putExtra("inputUser",suWorders.get(position).SupervisorCode);
                    startActivityForResult(intent, REQUEST_CODE_VIEW_ILBO);
                }
            } else {//작업요청서 거친다.
                intent = new Intent(getBaseContext(), ViewActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "작업");
                intent.putExtra("seqNo", position);
                startActivityForResult(intent, REQUEST_CODE_VIEW_ILBO);
            }
        }
    };

    private void makeSWList() {

        suWorders = (ArrayList<SuWorder>) getIntent().getSerializableExtra("data");
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
        new ReadJSONFeedTask(this.adapter).execute(restURL);
    }


    /**
     * 작업일보 내용 가져오기.
     */
    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        SwListVIewAdapter2 adapter;

        public ReadJSONFeedTask(SwListVIewAdapter2 adapter) {
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                //Log.i("ReadJSONFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray(arrayName);
                String LocationName = "";
                String SupervisorWoNo = "";
                String WorkDate = "";
                String StartTime = "";
                String StatusFlag = "";
                String CustomerName = "";
                String Supervisor = "";
                String WorkTypeName = "";
                String Dong = "";
                String SupervisorCode="";

                suWorders.clear();
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
                    SupervisorCode = child.getString("SupervisorCode");
                    suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StartTime, StatusFlag, CustomerName, Supervisor, WorkTypeName, Dong, SupervisorCode));
                }
                this.adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private SuWorder MakeData(String woNo, String locationName, String workDate, String startTime,
                                  String statusFlag, String customerName,
                                  String supervisor, String workTypeName, String dong, String supervisorCode) {
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
            suWorder.SupervisorCode = supervisorCode;
            return suWorder;
        }
    }
}

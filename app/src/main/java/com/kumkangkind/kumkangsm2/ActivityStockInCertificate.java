package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.kumkangkind.kumkangsm2.Adapter.StockInCertificateAdapter;
import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;
import com.kumkangkind.kumkangsm2.Object.StockInCertificate;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class ActivityStockInCertificate extends BaseActivity {

    private RecyclerView recyclerView;
    TextView tvDate;
    String programType = "";
    ProgressDialog mProgressDialog;
    boolean firstToken = true;

    Button btnIlbo;
    BackPressEditText edtInput;
    //Button btnHelp;
    String fromDate;
    String toDate;
    String locationName;
    StockInCertificateAdapter adapter;

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
        setContentView(R.layout.activity_stock_in_certificate);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btnIlbo = findViewById(R.id.btnIlbo);
        edtInput = findViewById(R.id.edtInput);
        fromDate = getIntent().getStringExtra("fromDate");
        toDate = getIntent().getStringExtra("toDate");
        locationName = getIntent().getStringExtra("locationName");
        tvDate.setText(fromDate+" ~ "+toDate);
        edtInput.setText(locationName);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new StockInCertificateAdapter(new ArrayList<>(),ActivityStockInCertificate.this);
        LinearLayoutManager  layoutManager=
                new LinearLayoutManager(ActivityStockInCertificate.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = new GregorianCalendar();
                Calendar calendar2 = new GregorianCalendar();
                calendar.set(Integer.parseInt(fromDate.split("-")[0]),Integer.parseInt(fromDate.split("-")[1])-1,
                        Integer.parseInt(fromDate.split("-")[2]));
                calendar2.set(Integer.parseInt(toDate.split("-")[0]),Integer.parseInt(toDate.split("-")[1])-1,
                        Integer.parseInt(toDate.split("-")[2]));

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setSelection(new Pair(calendar.getTimeInMillis(),calendar2.getTimeInMillis()));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {

                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Pair selectedDates = (Pair) materialDatePicker.getSelection();
//              then obtain the startDate & endDate from the range
                        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
//              assigned variables
                        Date startDate = rangeDate.first;
                        Date endDate = rangeDate.second;

                        fromDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
                        toDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);

                        tvDate.setText(fromDate+" ~ "+toDate);
                        getStockInCertificateMain2();
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "Tag");
            }
        });

        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getStockInCertificateMain2();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //progressOFF2();
    }

    @Override
    protected void onPause() {
        super.onPause();
        firstToken=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!firstToken)
            getStockInCertificateMain2();
    }

    private void getStockInCertificateMain2() {
        String url = getString(R.string.service_address) + "getStockInCertificateMain2";
        ContentValues values = new ContentValues();

        //검색조건
        String supervisorCode = Users.USER_ID;

        values.put("FromDate", fromDate);
        values.put("ToDate", toDate);
        values.put("SupervisorCode", supervisorCode);
        GetStockInCertificateMain2 gsod = new GetStockInCertificateMain2(url, values);
        gsod.execute();
    }


    public class GetStockInCertificateMain2 extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockInCertificateMain2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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

                ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificate.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockInCertificate = new StockInCertificate();
                    stockInCertificate.StartTime = child.getString("StartTime");
                    stockInCertificate.CertificateNo = child.getString("CertificateNo");
                    stockInCertificate.CustomerCode = child.getString("CustomerCode");
                    stockInCertificate.CustomerName = child.getString("CustomerName");
                    stockInCertificate.LocationNo = child.getString("LocationNo");
                    stockInCertificate.LocationName = child.getString("LocationName");
                    stockInCertificate.CarNo = child.getString("CarNo");
                    stockInCertificate.InsertUser = child.getString("InsertUser");
                    stockInCertificate.ActualWeight = child.getString("ActualWeight");
                    stockInCertificate.SupervisorCode = child.getString("SupervisorCode");
                    stockInCertificateArrayList.add(stockInCertificate);

                }
                adapter.updateAdapter(stockInCertificateArrayList);
                adapter.getFilter().filter(edtInput.getText().toString());

                // adapter = new ProgressFloorReturnViewAdapter(, R.layout.progress_floor_return_row, dongArrayList, );
                // recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                //Log.i("순서확인", "미납/재고종료");
                progressOFF2(this.getClass().getName());
            }
        }
    }


    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnIlbo:
                programType = "반출송장등록";
                startProgress();
                MakeStockInCertificate();
                break;

            case R.id.btnHelp:
                new AlertDialog.Builder(this).setMessage("송장이 보이지 않을시, \n메인화면의 날짜를 확인한 후,\n변경하시기 바랍니다.").setCancelable(true).
                        setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;

        }
    }

    private void MakeStockInCertificate() {
        new GetCustomerLocationByGet("내현장").execute(getString(R.string.service_address) + "getCustomerLocation/" + Users.USER_ID);
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
                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();


                Intent i;
                i = new Intent(getBaseContext(), LocationTreeViewActivity.class);//todo

                i.putExtra("programType", programType);
                i.putExtra("hashMap", customerHashMap);
                startActivity(i);


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
}

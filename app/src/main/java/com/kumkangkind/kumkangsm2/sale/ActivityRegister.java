package com.kumkangkind.kumkangsm2.sale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.SuWorder2;
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


public class ActivityRegister extends BaseActivity {

    private TextView textViewWoNo;
    private TextView textViewLocation;
    private TextView textViewProjectNo;
    private TextView textViewWorkDate;
    private Spinner spinnerSupervisor;
    private Spinner spinnerWorkType;
    private TextView textViewRemark;
    private TextView textViewSearch;
    private Dialog customerDialog;
    private ArrayList<Customers> customerList;
    private ArrayList<WorkType> workTypeList;
    private int mYear;
    private int mDay;
    private int mMonth;
    private SuWorder2 suWorder2;
    private ProgressDialog mProgressDialog;

    private String woNo = "";
    private String resultCode ="";
    private String message = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_register);


        Intent intent = getIntent();
        woNo = getIntent().getStringExtra("key");
        FindControl();
        SetDate();
        MakeSpinnerSupervisor();
        MakeSpinnerWorkType();

        if (woNo.equals("")) {
            //새롭게 등록
        } else {

            suWorder2 = (SuWorder2)getIntent().getSerializableExtra("data");
            textViewWoNo.setText(suWorder2.SupervisorWoNo);
            textViewLocation.setText(suWorder2.CustomerName + "(" + suWorder2.LocationName + ")");
            textViewProjectNo.setText(suWorder2.ContractMasterNo);
            textViewWorkDate.setText(suWorder2.WorkDate);
            spinnerSupervisor.setSelection(getIndex(spinnerSupervisor, suWorder2.SupervisorCode + "-" + suWorder2.SupervisorName));
            textViewRemark.setText(suWorder2.Remark);
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(textViewRemark.getWindowToken(), 0);//가상키보드를 숨기는 메소드인데, 여기에서 쓰든말든 굳이 필요가 없는듯하다.
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }


    private void MakeSpinnerWorkType(){
        String restURL = Users.ServiceAddress+"worktypelistByBusinessClassCode";
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
                String WorkTypeCode ="";
                String WorkTypeName = "";
                String SeqNo = "";
                int No;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    WorkTypeCode = child.getString("WorkTypeCode");
                    WorkTypeName  = child.getString("WorkTypeName");
                    SeqNo = child.getString("SeqNo");
                    No=i;
                    workTypeList.add(new WorkType( WorkTypeCode, WorkTypeName, SeqNo, No));
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
    }






    private void FindControl(){

        textViewLocation = (TextView)findViewById(R.id.textViewCustomer);
        textViewProjectNo = (TextView)findViewById(R.id.textViewRealDate);
        textViewWorkDate = (TextView)findViewById(R.id.textViewWorkDate);
        spinnerSupervisor = (Spinner)findViewById(R.id.spinnerSupervisor);
        spinnerWorkType = (Spinner)findViewById(R.id.spinnerWorkType);
        textViewRemark = (EditText)findViewById(R.id.textViewRemark);
        textViewWoNo = (TextView)findViewById(R.id.textViewWorderNo);
    }

    private void SetDate() {
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);
    }

    DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            textViewWorkDate.setText(String.format("%d-%d-%d", mYear,
                    mMonth + 1, mDay));
        }
    };


    /**
     * 수퍼바이저 스피너를 세팅한다.
     */
    private void MakeSpinnerSupervisor() {

        spinnerSupervisor = (Spinner) findViewById(R.id.spinnerSupervisor);
        ArrayAdapter<String> supervisorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Users.supervisorList);
        spinnerSupervisor.setAdapter(supervisorAdapter);
    }

    public void mOnClick(View v) {
        switch (v.getId())
        {
            case R.id.textViewCustomer : {
                SearchCustomer();
                break;
            }
            case R.id.textViewWorkDate : {
                new DatePickerDialog(ActivityRegister.this, mDateSetListener1, mYear, mMonth, mDay).show();
                break;
            }
            case R.id.btnOK : {
                SaveData();
                break;
            }


            case R.id.btnCancel :{
                finish();
                break;
            }
        }
    }

    /**
     * 작업지시를 저장한다
     */
    private void SaveData(){

        SetDataFromControl();

        if(suWorder2.ContractMasterNo.equals("")) {
            Toast.makeText(this, "거래처 현장을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(suWorder2.WorkDate.equals("")){
            Toast.makeText(this, "작업희망일자를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        mProgressDialog = ProgressDialog.show(ActivityRegister.this, "",
                "잠시만 기다려 주세요.", true);
        mHandler3.postDelayed(new Runnable() {
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


        String restURL = "";

        if(textViewWoNo.getText().toString().equals(""))
            restURL = Users.ServiceAddress+"insertsupervisorworder";
        else
            restURL = Users.ServiceAddress+"updatesupervisorworder";
        new HttpAsyncTask().execute(restURL);
    }

    /**
     * 뷰에 있는 내용을 Suworder2에 저장한다.
     */
    private void SetDataFromControl(){

        //수퍼바이저코드
        //영업사원코드
        //프로젝트번호
        //작업일자
        //작업코드
        //특이사항

        suWorder2 = new SuWorder2();
        suWorder2.SupervisorWoNo = woNo;
        suWorder2.SupervisorCode =  spinnerSupervisor.getSelectedItem().toString().split("-")[0];
        suWorder2.SaleEmployeeCode = Users.USER_ID;
        suWorder2.ContractMasterNo = textViewProjectNo.getText().toString();
        suWorder2.WorkDate = textViewWorkDate.getText().toString();
        suWorder2.WorkTypeCode =  spinnerWorkType.getSelectedItem().toString().split("-")[0];
        suWorder2.Remark = textViewRemark.getText().toString();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            mHandler3.sendEmptyMessage(0);
        }
    }


    public String POST(String url){
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

            //Save
            jsonObject.put("SupervisorWoNo", suWorder2.SupervisorWoNo);
            jsonObject.put("WorkDate", suWorder2.WorkDate);
            jsonObject.put("SupervisorCode", suWorder2.SupervisorCode);
            jsonObject.put("SaleEmployeeCode", suWorder2.SaleEmployeeCode);
            jsonObject.put("ContractMasterNo", suWorder2.ContractMasterNo);
            jsonObject.put("WorkTypeCode", suWorder2.WorkTypeCode);
            jsonObject.put("Remark", suWorder2.Remark);
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

            if(inputStream != null)
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
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        woNo ="";
        message= "";
        resultCode = "";

        try {
            Log.i("JSON", result );

            JSONArray jsonArray = new JSONArray(result);


            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

            if(resultCode.equals("true"))
                woNo = message;
        }
        catch (Exception ex){
        }

        inputStream.close();
        return message + "("+ resultCode  +")";
    }

    private void SearchCustomer(){

        customerDialog = new Dialog(this);
        customerDialog.setContentView(R.layout.dialog_customer_search);
        customerDialog.setTitle("거래처 검색");
        textViewSearch = (TextView)customerDialog.findViewById(R.id.textViewSearch);

        Button buttonOk = (Button)customerDialog.findViewById(R.id.btnOK);
        Button buttonCancel = (Button)customerDialog.findViewById(R.id.btnCancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //거래처 검색
                customerDialog.dismiss();

                String restURL = Users.ServiceAddress+"customerlist/" + textViewSearch.getText().toString();
                new ReadJSONFeedTask().execute(restURL);


            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //다얄로그 닫는다.
                customerDialog.dismiss();


            }
        });

        customerDialog.show();
    }

    /**
     * 거래처 리스트
     */
    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("CustomerListResult");

                customerList = new ArrayList<Customers>();
                String LocationName ="";
                String CustomerName = "";
                String ContractMasterNo = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    LocationName = child.getString("LocationName");
                    CustomerName  = child.getString("CustomerName");
                    ContractMasterNo = child.getString("ContractMasterNo");
                    customerList.add(new Customers( CustomerName, LocationName, ContractMasterNo));
                }
                mHandler.sendEmptyMessage(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 거래처 찾기
     */
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(ActivityRegister.this, CustomerListActivity.class);
            intent.putExtra("customers", customerList);
            startActivityForResult(intent, 1);
        }
    };

    /**
     * 작업구분을 가져온다.
     */
    private Handler mHandler2 = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String[] workTypes = new String[workTypeList.size()];

            for(int i = 0 ; i < workTypeList.size(); i++){

                workTypes[i] = workTypeList.get(i).WorkTypeCode + "-"+  workTypeList.get(i).WorkTypeName;
            }
            spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
            ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(ActivityRegister.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
            spinnerWorkType.setAdapter(workTypeAdapter);

            if(suWorder2 != null)
                spinnerWorkType.setSelection(getIndex(spinnerWorkType, suWorder2.WorkTypeCode + "-" + suWorder2.WorkTypeName));
        }
    };

    /**
     * 작업지시 서버등록 이후의 작업을 한다.
     */
    private Handler mHandler3 = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if(resultCode.equals("true")){

                if(suWorder2.SupervisorWoNo.equals(""))
                    suWorder2.SupervisorWoNo = woNo;
                Intent intent = new Intent(ActivityRegister.this, ActivitySaleView.class);
                intent.putExtra("key", suWorder2.SupervisorWoNo);
                startActivity(intent);
                finish();
                //해당 액티비티를 종료하고 현황 액티비로 이동한다.
            }
            else{

                //에러메시지를 띄운다.
                Toast.makeText(getBaseContext(), message + "("+ resultCode  +")", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //거래처 값을 가져온다
        if(requestCode == 1 && resultCode == RESULT_OK){

            Customers customer = (Customers)data.getSerializableExtra("customer");
            textViewLocation.setText(customer.CustomerName +"(" + customer.LocationName + ")");
            textViewProjectNo.setText(customer.ContractMasterNo);
        }
    }
}

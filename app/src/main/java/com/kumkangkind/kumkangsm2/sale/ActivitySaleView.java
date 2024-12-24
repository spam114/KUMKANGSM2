package com.kumkangkind.kumkangsm2.sale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.SuWorder2;
import com.kumkangkind.kumkangsm2.Users;
import com.kumkangkind.kumkangsm2.ViewActivity2;

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

/**
 * 1. 해당 클래스는 작업지시를 보는 화면을 정의한다.
 * 2. 화면을 보고 작업 지시를 입력 할 경우, 입력하기 버튼을 클릭하여 입력 화면으로 전환한다.
 */
public class ActivitySaleView extends BaseActivity {

    SuWorder2 suWorder2;
    String type = "";

    TextView textViewLocation;
    TextView textViewWorkDate;
    TextView textViewDeptName;
    TextView textViewEmployeeName;
    TextView textViewSupervisorName;
    TextView textViewWorkTypeName;
    TextView textViewRemark;
    TextView textViewManageNo;
    TextView textViewCustomer;
    Button buttonUpdate;
    Button buttonWork;
    Button buttonDelete;
    ProgressDialog mProgressDialog;
    private int seqNo = 0;

    private String woNo= "";
    private String message = "";
    private String resultCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_view);

        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewWorkDate = (TextView) findViewById(R.id.textViewWorkDate);
        textViewDeptName = (TextView) findViewById(R.id.textViewDeptName);
        textViewEmployeeName = (TextView) findViewById(R.id.textViewEmployeeName);
        textViewSupervisorName = (TextView) findViewById(R.id.textViewSupervisor);
        textViewWorkTypeName = (TextView) findViewById(R.id.textViewWorkTypeName);
        textViewRemark = (TextView) findViewById(R.id.textViewRemark);
        textViewManageNo = (TextView) findViewById(R.id.textViewManageNo);
        textViewCustomer = (TextView) findViewById(R.id.textViewCustomer);

        buttonWork = (Button) findViewById(R.id.btnWork);
        buttonUpdate = (Button) findViewById(R.id.btnUpdate);
        buttonDelete = (Button) findViewById(R.id.btnDelete);

        this.buttonUpdate.setVisibility(View.INVISIBLE);
        this.buttonDelete.setVisibility(View.INVISIBLE);

        String key = getIntent().getStringExtra("key");
        seqNo = getIntent().getIntExtra("seqNo", 0);
        String restURL = Users.ServiceAddress+"getworderconfirm/" + key + "/" + Users.USER_ID;
        new ReadJSONFeedTask().execute(restURL);
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetSupervisorWorderForConFirmResult");

                suWorder2 = new SuWorder2();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    suWorder2.SupervisorWoNo = child.getString("SupervisorWoNo");
                    suWorder2.ContractMasterNo = child.getString("ContractMasterNo");
                    suWorder2.DeptCode = child.getString("DeptCode");
                    suWorder2.DeptName = child.getString("DeptName");
                    suWorder2.LocationName = child.getString("LocationName");
                    suWorder2.CustomerName = child.getString("CustomerName");
                    suWorder2.EmployeeName = child.getString("EmployeeName");
                    suWorder2.Remark = child.getString("Remark");
                    suWorder2.RequestDate = child.getString("RequestDate");
                    suWorder2.SaleEmployeeCode = child.getString("SaleEmployeeCode");
                    suWorder2.StatusFlag = child.getString("StatusFlag");
                    suWorder2.SupervisorCode = child.getString("SupervisorCode");
                    suWorder2.SupervisorCompany = child.getString("SupervisorCompany");
                    suWorder2.SupervisorName = child.getString("SupervisorName");
                    suWorder2.WorkDate = child.getString("WorkDate");
                    suWorder2.WorkTypeCode = child.getString("WorkTypeCode");
                    suWorder2.WorkTypeName = child.getString("WorkTypeName");
                }
                SetControlFormData(suWorder2);

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
                Log.d("JSON", "Failed to download file");

            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }

        return stringBuilder.toString();
    }

    private void SetControlFormData(SuWorder2 suWorder2) {

        if(Integer.valueOf(suWorder2.StatusFlag) < 1 ) {
            this.buttonUpdate.setVisibility(View.VISIBLE);
            this.buttonDelete.setVisibility(View.VISIBLE);

        }
        textViewCustomer.setText(suWorder2.CustomerName);
        textViewManageNo.setText(suWorder2.SupervisorWoNo);
        textViewLocation.setText(suWorder2.LocationName);
        textViewWorkDate.setText(suWorder2.WorkDate);
        textViewDeptName.setText(suWorder2.DeptName);
        textViewEmployeeName.setText(suWorder2.EmployeeName);
        textViewSupervisorName.setText(suWorder2.SupervisorName);
        textViewWorkTypeName.setText(suWorder2.WorkTypeName);
        textViewRemark.setText(suWorder2.Remark);
    }

    public void mOnClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.btnUpdate:
                try {
                    intent = new Intent(this, ActivityRegister.class);
                    intent.putExtra("key", suWorder2.SupervisorWoNo);
                    intent.putExtra("data", suWorder2);
                    startActivity(intent);
                    finish();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;

            case R.id.btnWork:
                try {

                    intent = new Intent(this, ViewActivity2.class);
                    intent.putExtra("key", suWorder2.SupervisorWoNo);
                    startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;

            case R.id.btnDelete : {
                try {

                    DeleteData();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 작업지시를 저장한다
     */
    private void DeleteData(){

        new AlertDialog.Builder(this).setMessage("삭제할까요?").setCancelable(false).
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //POST 명령어 호출(업데이트를 적용한다)

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(ActivitySaleView.this, "",
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

                String restURL = Users.ServiceAddress+"deletesupervisorworder";
                new HttpAsyncTask().execute(restURL);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
    }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            mHandler.sendEmptyMessage(0);
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
            jsonObject.put("SaleEmployeeCode", Users.USER_ID);
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
            woNo = message;
        }
        catch (Exception ex){
        }

        inputStream.close();
        return message + "("+ resultCode  +")";
    }

    /**
     * 작업지시 서버등록 이후의 작업을 한다.
     */
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if(resultCode.equals("true")){

                Intent intent = getIntent();
                intent.putExtra("seqNo", seqNo);
                setResult(RESULT_OK, intent);
                finish();
                //해당 액티비티를 종료하고 현황 액티비로 이동한다.
            }
            else{
                //에러메시지를 띄운다.
                Toast.makeText(getBaseContext(), message + "(" + resultCode + ")", Toast.LENGTH_SHORT).show();
            }
        }
    };

}

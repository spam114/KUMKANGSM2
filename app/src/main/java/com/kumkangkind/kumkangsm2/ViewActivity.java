package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


/**
 * 1. 해당 클래스는 작업지시를 보는 화면을 정의한다.
 * 2. 화면을 보고 작업 지시를 입력 할 경우, 입력하기 버튼을 클릭하여 입력 화면으로 전환한다.
 */
public class ViewActivity extends BaseActivity {

    SuWorder2 suWorder2;
    String type = "";
    String contractNo = "";

    TextView textViewLocation;
    TextView textViewWorkDate;
    TextView textViewDeptName;
    TextView textViewEmployeeName;
    TextView textViewSupervisorName;
    TextView textViewWorkTypeName;
    TextView textViewRemark;
    TextView textViewManageNo;
    TextView textViewCustomer;
    Button btnnext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workview);

        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewWorkDate = (TextView) findViewById(R.id.textViewWorkDate);
        textViewDeptName = (TextView) findViewById(R.id.textViewDeptName);
        textViewEmployeeName = (TextView) findViewById(R.id.textViewEmployeeName);
        textViewSupervisorName = (TextView) findViewById(R.id.textViewSupervisor);
        textViewWorkTypeName = (TextView) findViewById(R.id.textViewWorkTypeName);
        textViewRemark = (TextView) findViewById(R.id.textViewRemark);
        btnnext = (Button) findViewById(R.id.btnNext);
        btnnext.setClickable(false);
        textViewManageNo = (TextView) findViewById(R.id.textViewManageNo);
        textViewCustomer = (TextView) findViewById(R.id.textViewCustomer);

        type = getIntent().getStringExtra("type");
        if (type.equals("확인"))
            btnnext.setText("작업일보");


        String key = getIntent().getStringExtra("key");

        String restURL = Users.ServiceAddress+"getworderconfirm/" + key + "/" + Users.USER_ID;
        new ReadJSONFeedTask().execute(restURL);

        progressOFF();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("ReadJSONFeedTask", result);
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
                    contractNo = child.getString("ContractNo");
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

        textViewCustomer.setText(suWorder2.CustomerName);
        textViewManageNo.setText(suWorder2.SupervisorWoNo);
        textViewLocation.setText(suWorder2.LocationName);
        textViewWorkDate.setText(suWorder2.WorkDate);
        textViewDeptName.setText(suWorder2.DeptName);
        textViewEmployeeName.setText(suWorder2.EmployeeName);
        textViewSupervisorName.setText(suWorder2.SupervisorName);
        textViewWorkTypeName.setText(suWorder2.WorkTypeName);
        textViewRemark.setText(suWorder2.Remark);
        btnnext.setClickable(true);
    }

    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnPrev:

                finish();
                break;

            case R.id.btnNext:

                if(Users.BusinessClassCode==11){//창녕
                    if (type.equals("확인")) {
                        Intent intent = new Intent(this, ViewActivity2.class);
                        intent.putExtra("key", suWorder2.SupervisorWoNo);
                        startActivity(intent);
                        break;
                    } else {//작업일보작성
                        Intent intent = new Intent(this, RegisterActivity.class);
                        intent.putExtra("key", suWorder2.SupervisorWoNo);
                        startActivity(intent);
                        break;
                    }
                }
                else{//음성
                    if (type.equals("확인")) {//추후 음성꺼에 맞게 수정바람 현재 맞지않음
                        Intent intent = new Intent(this, ViewActivity2.class);
                        intent.putExtra("type", type);
                        intent.putExtra("key", suWorder2.SupervisorWoNo);
                        startActivity(intent);
                        break;
                    } else {//작업일보작성
                        Intent intent = new Intent(this, RegisterActivity2.class);
                        intent.putExtra("type", type);
                        intent.putExtra("key", suWorder2.SupervisorWoNo);
                        startActivity(intent);
                        break;
                    }
                }


        }
    }
}

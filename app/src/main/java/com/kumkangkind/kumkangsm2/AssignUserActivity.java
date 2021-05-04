package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;*/

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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * 1. 해당 클래스는 작업지시를 보는 화면을 정의한다.
 * 2. 화면을 보고 작업 지시를 입력 할 경우, 입력하기 버튼을 클릭하여 입력 화면으로 전환한다.
 */
public class AssignUserActivity extends BaseActivity {

    SuWorder2 suWorder2;

    TextView textViewLocation;
    TextView textViewWorkDate;
    TextView textViewDeptName;
    TextView textViewEmployeeName;
    // TextView textViewSupervisorName;
    TextView textViewWorkTypeName;
    TextView textViewRemark;
    TextView textViewManageNo;
    TextView textViewCustomer;
    Spinner spinnerSupervisor;
    Button btnnext;
    Handler mHandler;
    String key;
    int seqNo;
    String assignedUser = "";

    ProgressDialog mProgressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewWorkDate = (TextView) findViewById(R.id.textViewWorkDate);
        textViewDeptName = (TextView) findViewById(R.id.textViewDeptName);
        textViewEmployeeName = (TextView) findViewById(R.id.textViewEmployeeName);
        //textViewSupervisorName = (TextView) findViewById(R.id.textViewSupervisor);
        textViewWorkTypeName = (TextView) findViewById(R.id.textViewWorkTypeName);
        textViewRemark = (TextView) findViewById(R.id.textViewRemark);
        btnnext = (Button) findViewById(R.id.btnNext);
        btnnext.setClickable(false);
        textViewManageNo = (TextView) findViewById(R.id.textViewManageNo);
        textViewCustomer = (TextView) findViewById(R.id.textViewCustomer);
        spinnerSupervisor = (Spinner) findViewById(R.id.spinnerSupervisor);

        key = getIntent().getStringExtra("key");
        seqNo = getIntent().getIntExtra("seqNo", -1);

        String restURL = getString(R.string.service_address)+"getworder/" + key;
        new ReadJSONFeedTask().execute(restURL);

        MakeSpinnerSupervisor();

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
                JSONArray jsonArray = jsonObject.optJSONArray("GetSupervisorWorderResult");

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

        textViewCustomer.setText(suWorder2.CustomerName);
        textViewManageNo.setText(suWorder2.SupervisorWoNo);
        textViewLocation.setText(suWorder2.LocationName);
        textViewWorkDate.setText(suWorder2.WorkDate);
        textViewDeptName.setText(suWorder2.DeptName);
        textViewEmployeeName.setText(suWorder2.EmployeeName);
        textViewWorkTypeName.setText(suWorder2.WorkTypeName);
        textViewRemark.setText(suWorder2.Remark);
        spinnerSupervisor.setSelection(getIndex(spinnerSupervisor, suWorder2.SupervisorCode + "-" + suWorder2.SupervisorName));
        btnnext.setClickable(true);
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


    private void MakeSpinnerSupervisor() {

        //스피너 세팅
        spinnerSupervisor = (Spinner) findViewById(R.id.spinnerSupervisor);
        ArrayAdapter<String> supervisorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Users.supervisorList);
        spinnerSupervisor.setAdapter(supervisorAdapter);
    }


    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnPrev:
                onBackPressed();
                break;

            case R.id.btnNext:
                AssignProcess();
                break;
        }
    }

    /**
     * 배정
     */
    private void AssignProcess() {


        new AlertDialog.Builder(this).setMessage("담당자를 배정할까요?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //POST 명령어 호출(업데이트를 적용한다)

                mHandler = new Handler();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog = ProgressDialog.show(AssignUserActivity.this, "",
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

                //progressDialog = ProgressDialog.show(RegisterActivity.this, "Wait", "Loading...");


                new HttpAsyncTask().execute(getString(R.string.service_address)+"setassign");
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
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
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

            jsonObject.put("SupervisorWoNo", key);
            jsonObject.put("SupervisorCode", spinnerSupervisor.getSelectedItem().toString().split("-")[0]);
            jsonObject.put("SupervisorName", spinnerSupervisor.getSelectedItem().toString().split("-")[1]);
            jsonObject.put("Remark", this.textViewRemark.getText().toString());

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
        mProgressDialog.dismiss();
        return result;
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        String message = "";
        String resultCode = "";

        try {
            Log.i("JSON", result);

            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

        } catch (Exception ex) {

        }

        inputStream.close();
        return message + "(" + resultCode + ")";
    }


    /*public void MakeKaKaKoLink() {
        try {

            KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoTalkLinkMessageBuilder.addText("후훗");

            kakaoTalkLinkMessageBuilder.addAppButton("앱으로 이동",
                    new AppActionBuilder()
                            .addActionInfo(AppActionInfoBuilder
                                    .createAndroidActionInfoBuilder()
                                    .setExecuteParam("execparamkey1=1111")
                                    .setMarketParam("referrer=kakaotalklink")
                                    .build())
                            .addActionInfo(AppActionInfoBuilder
                                    .createiOSActionInfoBuilder()
                                    .setExecuteParam("execparamkey1=1111")
                                    .build())
                            .setUrl("your-website url") // PC 카카오톡 에서 사용하게 될 웹사이트 주소
                            .build());

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (Exception ex) {
            Log.i("err", ex.getMessage());
        }
    }*/

    void sendKakaoImage(String imgName) {

        Uri dataUri = Uri.parse(imgName);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, dataUri);
        intent.setPackage("com.kakao.talk");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        Intent intent  = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("seqNo", seqNo);
        intent.putExtra("SupervisorCode", spinnerSupervisor.getSelectedItem().toString().split("-")[0]);
        intent.putExtra("SupervisorName", spinnerSupervisor.getSelectedItem().toString().split("-")[1]);

        super.onBackPressed();
    }
}

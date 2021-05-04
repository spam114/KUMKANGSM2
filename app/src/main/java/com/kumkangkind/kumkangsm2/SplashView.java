package com.kumkangkind.kumkangsm2;

import android.Manifest;
import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 *
 */
public class SplashView extends BaseActivity {

    EditText editID;
    EditText editPW;
    Button btnLogIn;
    public String output = "";
    Handler handelr;
    DownloadManager mDm;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startProgress();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//M은 마쉬멜로
            //GetPermission1();
            CheckPermission();

        } else {
            CheckUser();
        }



        //handelr.sendEmptyMessageDelayed(0, 3000);

        progressOFF();

    }

    private void CheckPermission(){
        if (PermissionUtil.haveAllpermission(SplashView.this,PermissionUtil.permissionList)){
            // 모든 퍼미션 허용
            CheckUser();
        }
        else{
            // 퍼미션 하나로도 허용 안함
            ActivityCompat.requestPermissions(SplashView.this,PermissionUtil.permissionList,PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }


    /*private void GetPermission1() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)//권한이 있는지 없는지 검사
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {// 이전에 권한 요청 거절을 했는지 안했는지 검사: 이전에도 했으면 true
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);//MY_PERMISSIONS_REQUEST_READ_PHONE_STATE 를 리턴
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);//MY_PERMISSIONS_REQUEST_READ_PHONE_STATE 를 리턴
                }
            } else {

                GetPermission2();
            }
        }
    }*/

    /*private void GetPermission2() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_DATA);

                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_DATA);
                }

            } else {
                CheckUser();//권한이 있다면 CheckUser실행
            }
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        /*switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //다음권한으로 넘어간다.
                    GetPermission2();

                } else {

                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();


                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_DATA: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CheckUser();//권한이 있다면 CheckUser실행

                } else {
                    Toast.makeText(this, "사진을 업로드하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }*/

        //region 요청 코드가 PERMISSIONS_REQUEST_CODE 이고,
        // 요청한 퍼미션 개수만큼 수신되었다면
        if ( requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE &&
                grantResults.length == PermissionUtil.permissionList.length) {

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            //허용
            if ( check_result ) {
                //허용했을때 로직
                CheckUser();
            }
            //거부 ,재거부
            else {
                if (PermissionUtil.recheckPermission(this,PermissionUtil.permissionList)){
                    //거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();

                }
                else{
                    //재거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        }
        //end region

    }

    /**
     * 사용자 번호를 확인하여, 서버에 등록되어 있는지 확인한다.
     */
    private void CheckUser() {

        try {
            Log.i("ReadJSONFeedTask", "통신");
            TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String PhoneNumber = "";
            try {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }//

               // PhoneNumber=Settings.Secure.getString (getContentResolver(), Settings.Secure.ANDROID_ID);
                //PhoneNumber=systemService.getLine1Number();
                PhoneNumber=systemService.getLine1Number();
                if(PhoneNumber==null) {
                    PhoneNumber = Settings.Secure.getString (getContentResolver(), Settings.Secure.ANDROID_ID);// 나중에는 안드로이드 아이디로 셋팅하여야한다.
                    /*BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                    if(myDevice==null){
                        // device does not support bluetooth
                    }
                    String deviceName = myDevice.getName();
                    PhoneNumber=deviceName;*/
                    //Toast.makeText(this, PhoneNumber, Toast.LENGTH_LONG).show();
                }
                else if(PhoneNumber.equals("")){
                    PhoneNumber = Settings.Secure.getString (getContentResolver(), Settings.Secure.ANDROID_ID);
                }

                Users.AndroidID= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if(Users.AndroidID==null)
                    Users.AndroidID="";
                Users.Model=Build.MODEL;
                if(Users.Model==null)
                    Users.Model="";
                Users.PhoneNumber=systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                if(Users.PhoneNumber==null)
                    Users.PhoneNumber="";
                /*else
                    Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");*/
                Users.DeviceOS=Build.VERSION.RELEASE;
                if(Users.DeviceOS==null)
                    Users.DeviceOS="";
                Users.Remark="";
                Users.DeviceName=BluetoothAdapter.getDefaultAdapter().getName();//블루투스 권한 필요 manifest확인: 블루투스가 없으면 에러남
                //PhoneNumber = "01067375288";//테스트용

            } catch (Exception ex) {
            }
            finally {
                String restURL = getString(R.string.service_address)+"checkemployee/" + PhoneNumber;
                //String restURL = getString(R.string.service_address)+"checkemployeebyInput";
                new ReadJSONFeedTask().execute(restURL);
                InsertAppLoginHistory();
                Log.i("사용자전화번호", PhoneNumber);
            }



        } catch (Exception ex) {


            Log.i("사용자정보", ex.getMessage().toString());
        }

    }



    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {//Users.USER_ID 이런식으로 갖고와도 쓰레드여서 다른곳에서 그대로 쓰기에 괜찮은 걸까?

        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("CheckEmployeeResult");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Users.USER_ID = child.getString("SupervisorCode");
                    Users.UserName = child.getString("SupervisorName");
                    Users.LeaderType = child.getString("LeaderType");
                    Users.DeptName = child.getString("DeptName");
                    Users.BusinessClassCode = child.getInt("BusinessClassCode");
                }

                if (Users.LeaderType.equals("1") || Users.LeaderType.equals("2") || Users.BusinessClassCode==9) {//음성은 슈퍼바이저 리스트 구분상관없이 받음
                    String restURL = getString(R.string.service_address)+"getsupervisorlist/" + Users.USER_ID;
                    new ReadJSONFeedTask2().execute(restURL);
                }

                if (!Users.USER_ID.equals("")) {
                    String restURL = getString(R.string.service_address)+"getcardlist/" + Users.USER_ID;
                    new ReadJSONFeedTask3().execute(restURL);
                }

                //USER_ID를 받았다면, 다음으로 넘어간다.
                if (!Users.USER_ID.equals("")) {

                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();

                }


            } catch (JSONException e) {

                Toast.makeText(SplashView.this, "등록되어 있는 휴대폰 정보가 없습니다. 관리자에게 문의 하세요. ", Toast.LENGTH_LONG).show();
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



    /**
     * 슈퍼바이저 리스트
     */
    private class ReadJSONFeedTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("Splash", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetSupervisorListResult");

                Users.supervisorList = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Users.supervisorList[i] = child.getString("SupervisorCode") + "-" + child.getString("SupervisorName");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 신용카드 리스트
     */
    private class ReadJSONFeedTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("Splash", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetCreditCardListResult");

                Users.CardList = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Users.CardList[i] = child.getString("CreditCardCode");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void InsertAppLoginHistory(){
        new InsertAppLoginHistory().execute(getString(R.string.service_address)+"insertAppLoginHistory");
    }

    public class InsertAppLoginHistory extends AsyncTask<String, Void, String> {

        public InsertAppLoginHistory(){
        }

        @Override
        protected String doInBackground(String... urls) {
            return postForInsertAppLoginHistory(urls[0]);
        }

        public String postForInsertAppLoginHistory(String url) {
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
                jsonObject.put("AppCode", getString(R.string.app_code));//AppCode
                jsonObject.put("AndroidID", Users.AndroidID);//AndroidID
                jsonObject.put("Model", Users.Model);//Model
                jsonObject.put("PhoneNumber", Users.PhoneNumber);//PhoneNumber
                jsonObject.put("DeviceName", Users.DeviceName);//DeviceName
                jsonObject.put("DeviceOS", Users.DeviceOS);//DeviceOS
                jsonObject.put("AppVersion", getCurrentVersion());//AppVersion
                jsonObject.put("Remark", "");//Remark

                json = jsonObject.toString();
                // ** Alternative way to convert Person object to JSON string using Jackson Lib
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
                    result = "통신오류: 인터넷 혹은 스캐너의 연결 상태를 확인하시기 바랍니다.";

            } catch (Exception e) {
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {

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
            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }
    }

    public int getCurrentVersion() {

        int version;

        try {
            mDm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = i.versionCode;
            //Users.CurrentVersion = version;

            return version;

        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}


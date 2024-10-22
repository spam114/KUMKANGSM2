package com.kumkangkind.kumkangsm2;

import android.Manifest;
import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kumkangkind.kumkangsm2.Object.BusinessClass;

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
import java.util.ArrayList;


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
        setContentView(R.layout.activity_splash);
        //startProgress();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//M은 마쉬멜로
            //GetPermission1();
            CheckPermission();

        } else {
            CheckUser();
        }
        //handelr.sendEmptyMessageDelayed(0, 3000);
        //progressOFF();
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {//33이상
            PermissionUtil.permissionList = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {//29이상, 33미만
            PermissionUtil.permissionList = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            PermissionUtil.permissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA
            };
        }

        if (PermissionUtil.haveAllpermission(SplashView.this, PermissionUtil.permissionList)) {
            // 모든 퍼미션 허용
            CheckUser();
        } else {
            // 퍼미션 하나로도 허용 안함
            ActivityCompat.requestPermissions(SplashView.this, PermissionUtil.permissionList, PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //region 요청 코드가 PERMISSIONS_REQUEST_CODE 이고,
        // 요청한 퍼미션 개수만큼 수신되었다면
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE &&
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
            if (check_result) {
                //허용했을때 로직
                CheckUser();
            }
            //거부 ,재거부
            else {
                //거부 눌렀을 때 로직
                Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
                /*if (PermissionUtil.recheckPermission(this, PermissionUtil.permissionList)) {
                    //거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();

                } else {
                    //재거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }*/
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
                PhoneNumber = systemService.getLine1Number();
                if (PhoneNumber == null) {
                    PhoneNumber = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);// 나중에는 안드로이드 아이디로 셋팅하여야한다.
                    /*BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                    if(myDevice==null){
                        // device does not support bluetooth
                    }
                    String deviceName = myDevice.getName();
                    PhoneNumber=deviceName;*/
                    //Toast.makeText(this, PhoneNumber, Toast.LENGTH_LONG).show();
                } else if (PhoneNumber.equals("")) {
                    PhoneNumber = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                }

                Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (Users.AndroidID == null)
                    Users.AndroidID = "";
                Users.Model = Build.MODEL;
                if (Users.Model == null)
                    Users.Model = "";
                Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                if (Users.PhoneNumber == null)
                    Users.PhoneNumber = "";
                /*else
                    Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");*/
                Users.DeviceOS = Build.VERSION.RELEASE;
                if (Users.DeviceOS == null)
                    Users.DeviceOS = "";
                Users.Remark = "";
                //PhoneNumber = "01032321179";//테스트용
                Users.DeviceName = BluetoothAdapter.getDefaultAdapter().getName();//블루투스 권한 필요 manifest확인: 블루투스가 없으면 에러남
            } catch (Exception ex) {
            } finally {
                getBusinessClass(Users.PhoneNumber);
                //String restURL = getString(R.string.service_address) + "checkemployee/" + PhoneNumber;
                //String restURL = getString(R.string.service_address)+"checkemployeebyInput";
                //new ReadJSONFeedTask().execute(restURL);
                InsertAppLoginHistory();
                Log.i("사용자전화번호", PhoneNumber);
            }
        } catch (Exception ex) {
            Log.i("사용자정보", ex.getMessage().toString());
        }
    }

    private void getBusinessClass(String str) {
        String str2 = getString(R.string.service_address) + "getBusinessClass";
        ContentValues contentValues = new ContentValues();
        contentValues.put("PhoneNumber", str);
        new GetBusinessClass(str2, contentValues, str).execute(new Void[0]);
    }

    public class GetBusinessClass extends AsyncTask<Void, Void, String> {
        String phoneNumber;
        String url;
        ContentValues values;

        GetBusinessClass(String str, ContentValues contentValues, String str2) {
            this.url = str;
            this.values = contentValues;
            this.phoneNumber = str2;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            return new RequestHttpURLConnection().request(this.url, this.values);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            try {
                try {
                    ArrayList<BusinessClass> arrayList = new ArrayList();
                    JSONArray jSONArray = new JSONArray(str);
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i);
                        if (!jSONObject.getString("ErrorCheck").equals("null")) {
                            Toast.makeText(SplashView.this, jSONObject.getString("ErrorCheck"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        BusinessClass businessClass = new BusinessClass();
                        businessClass.BusinessClassCode = jSONObject.getString("BusinessClassCode");
                        businessClass.BusinessClassName = jSONObject.getString("BusinessClassName");
                        arrayList.add(businessClass);
                    }
                    if (arrayList.size() == 0) {
                        Toast.makeText(SplashView.this, "등록되어 있지 않은 사용자입니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (arrayList.size() == 1) {
                        checkEmployeeBusinessClassCode(-1, phoneNumber);
                    } else {
                        CharSequence[] charSequenceArr = new CharSequence[jSONArray.length()];
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            charSequenceArr[i2] = ((BusinessClass) arrayList.get(i2)).BusinessClassCode + "-" + ((BusinessClass) arrayList.get(i2)).BusinessClassName;
                        }
                        final int[] iArr = {0};
                        new MaterialAlertDialogBuilder(SplashView.this).setTitle((CharSequence) "사업장을 선택하세요").setCancelable(false).setSingleChoiceItems(charSequenceArr, 0, new DialogInterface.OnClickListener() { // from class: com.kumkangkind.kumkangsm2.SplashView.GetBusinessClass.3
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i3) {
                                iArr[0] = i3;
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.kumkangkind.kumkangsm2.SplashView.GetBusinessClass.2
                            @Override // android.content.DialogInterface.OnDismissListener
                            public void onDismiss(DialogInterface dialogInterface) {
                            }
                        }).setPositiveButton((CharSequence) "확인", new DialogInterface.OnClickListener() { // from class: com.kumkangkind.kumkangsm2.SplashView.GetBusinessClass.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i3) {
                                checkEmployeeBusinessClassCode(Integer.parseInt(((BusinessClass) arrayList.get(iArr[0])).BusinessClassCode), GetBusinessClass.this.phoneNumber);
                            }
                        }).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                SplashView.this.progressOFF2(getClass().getName());
            }
        }
    }

    public void checkEmployeeBusinessClassCode(int i, String str) {
        String str2 = getString(R.string.service_address) + "checkEmployeeBusinessClassCode";
        ContentValues contentValues = new ContentValues();
        contentValues.put("PhoneNumber", str);
        contentValues.put("InputBusinessClassCode", i);
        new CheckEmployeeBusinessClassCode(str2, contentValues).execute(new Void[0]);
    }

    public class CheckEmployeeBusinessClassCode extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        CheckEmployeeBusinessClassCode(String str, ContentValues contentValues) {
            this.url = str;
            this.values = contentValues;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            SplashView.this.startProgress();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            return new RequestHttpURLConnection().request(this.url, this.values);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            try {
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i);
                        if (!jSONObject.getString("ErrorCheck").equals("null")) {
                            Toast.makeText(SplashView.this, jSONObject.getString("ErrorCheck"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Users.USER_ID = jSONObject.getString("SupervisorCode");
                        Users.UserName = jSONObject.getString("SupervisorName");
                        Users.LeaderType = jSONObject.getString("LeaderType");
                        Users.DeptName = jSONObject.getString("DeptName");
                        Users.BusinessClassCode = jSONObject.getInt("BusinessClassCode");
                        Users.EmployeeNo = jSONObject.getString("EmployeeNo");
                    }
                    //Object[] objArr = 0;
                    if (Users.LeaderType.equals("1") || Users.LeaderType.equals("2") || Users.BusinessClassCode == 9) {
                        new ReadJSONFeedTask2().execute(SplashView.this.getString(R.string.service_address) + "getsupervisorlist/" + Users.USER_ID);
                    }
                    if (!Users.USER_ID.equals("")) {
                        new ReadJSONFeedTask3().execute(SplashView.this.getString(R.string.service_address) + "getcardlist/" + Users.USER_ID);
                    }
                    if (!Users.USER_ID.equals("")) {
                        SplashView.this.setResult(-1, SplashView.this.getIntent());
                        SplashView.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                SplashView.this.progressOFF2(getClass().getName());
            }
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
                    Users.EmployeeNo = child.getString("EmployeeNo");
                }

                if (Users.LeaderType.equals("1") || Users.LeaderType.equals("2") || Users.BusinessClassCode == 9) {//음성은 슈퍼바이저 리스트 구분상관없이 받음
                    String restURL = getString(R.string.service_address) + "getsupervisorlist/" + Users.USER_ID;
                    new ReadJSONFeedTask2().execute(restURL);
                }

                if (!Users.USER_ID.equals("")) {
                    String restURL = getString(R.string.service_address) + "getcardlist/" + Users.USER_ID;
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

    private void InsertAppLoginHistory() {
        new InsertAppLoginHistory().execute(getString(R.string.service_address) + "insertAppLoginHistory");
    }

    public class InsertAppLoginHistory extends AsyncTask<String, Void, String> {

        public InsertAppLoginHistory() {
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


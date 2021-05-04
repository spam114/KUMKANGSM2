package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityDownload extends BaseActivity {

    DownloadManager mDm;
    long mId = 0;
    Handler mHandler;
    ProgressDialog mProgressDialog;
    String serverVersion;
    String downloadUrl;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = getIntent();
        try {
          checkServerVersion();
        } catch (Exception ex) {
            finish();
        }
    }

    private void newVersionDownload() {
        new AlertDialog.Builder(ActivityDownload.this).setMessage("새로운 버전이 있습니다. 다운로드 할까요?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgressDialog = ProgressDialog.show(ActivityDownload.this, "다운로드", "잠시만 기다려주세요");

                Uri uri = Uri.parse(downloadUrl);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setTitle(getString(R.string.app_name));
                req.setDestinationInExternalFilesDir(ActivityDownload.this, Environment.DIRECTORY_DOWNLOADS, "KUMKANG.apk");

                //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size() - 1));
                //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

                req.setDescription("어플리케이션 설치파일을 다운로드 합니다.");
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                mId = mDm.enqueue(req);
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                registerReceiver(mDownComplete2, filter);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ActivityDownload.this, "최신버전으로 업데이트 하시기 바랍니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.finishAffinity(ActivityDownload.this);
            }
        }).show();
    }

    private int getCurrentVersion() {

        int version;

        try {
            mDm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = i.versionCode;
            Users.CurrentVersion = version;

            return version;

        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private void checkServerVersion() {

        mHandler = new Handler();
        //기본 생성자를 통해 Handler를 생성하면, 생성되는 Handler는 해당 Handler를 호출한 스레드(메인쓰레드)의 MessageQueue와 Looper에 자동 연결된다.

        //메인쓰레드: 통신
        //runOnUiThread: 다이얼로그의 생성
        //그속 쓰레드(핸들러): 일정시간뒤 Dialog가 꺼져있는지 켜져 있는지 검사하고 켜져있으면, Dialog 끈다.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mProgressDialog = ProgressDialog.show(ActivityDownload.this, "",
                        "잠시만 기다려 주세요.", true);
                mHandler.postDelayed(new Runnable() {//Handler 를 사용하는 이유 그림을 그리는 행위는 메인스레드에서만 가능, 하지만 Handler객체를 사용하면 이관 가능
                    //postDelayed: 현재시간을 기준으로 지연시간 후에 실행 한다.
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
                }, 5000);// 일정시간뒤 Dialog가 꺼져있는지 켜져 있는지 검사하고 켜져있으면, Dialog 끈다.
            }
        });
        new HttpAsyncTask().execute(getString(R.string.service_address)+"checkappversion");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {//인자 세개: (params): ex) doInBackground의 매개변수, progress: , result
        @Override
        protected String doInBackground(String... urls) {//오래걸리는 주된 작업은 여기서! 서버와 통신!

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if(result.equals(""))
                finish();
            else {
                if (Double.parseDouble(serverVersion) > getCurrentVersion()) {//좌측이 DB에 있는 버전
                    newVersionDownload();
                } else {
                    finish();
                }
            }
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
            jsonObject.put("AppCode", getString(R.string.app_code));

            json = jsonObject.toString();
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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        try {
            Log.i("JSON", result);

            JSONArray jsonArray = new JSONArray(result);
            downloadUrl = jsonArray.getJSONObject(0).getString("Message");
            serverVersion = jsonArray.getJSONObject(0).getString("ResultCode");
        } catch (Exception ex) {
        }

        inputStream.close();
        return serverVersion;
    }


    /**
     * 다운로드 완료 이후의 작업을 처리한다.(다운로드 파일 열기)
     */
    BroadcastReceiver mDownComplete2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Toast.makeText(context, "다운로드 완료", Toast.LENGTH_SHORT).show();

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            Cursor cursor = mDm.query(query);
            if(cursor.moveToFirst()){

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);

                //String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                if(status == DownloadManager.STATUS_SUCCESSFUL){
                    String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    openFile(fileUri);
                }
            }
        }
    };

    protected void openFile(String uri) {

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri)).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent open = new Intent(Intent.ACTION_VIEW);
        open.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//누가 버전 이상이라면 FileProvider를 사용한다.
            uri =  uri.substring(7);
            File file= new File(uri);
            Uri u= FileProvider.getUriForFile(this, getApplication().getPackageName() + ".provider", file);
            open.setDataAndType(u,mimetype);
        }
        else{
            open.setDataAndType(Uri.parse(uri), mimetype);
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        //Toast.makeText(getBaseContext(), "설치 완료 후, 어플리케이션을 다시 시작하여 주십시요.", Toast.LENGTH_LONG).show();
        startActivity(open);

    }
}

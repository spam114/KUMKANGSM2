package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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



public class ViewActivity2 extends Activity {

    TextView textView0;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    String key = "";
    Handler mHandler;

    SuWorder3 suworder3;
    WoImage image;
    WoItem item;
    ArrayList<WoImage> images;
    ArrayList<WoItem> items;

    ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workeview2);

        key = getIntent().getStringExtra("key");

        textView1 = (TextView) findViewById(R.id.textViewLocation);
        textView2 = (TextView) findViewById(R.id.textViewWorkDate);
        textView3 = (TextView) findViewById(R.id.textViewWorkTime);
        textView4 = (TextView) findViewById(R.id.textViewStay);
        textView5 = (TextView) findViewById(R.id.textViewWork1);
        textView6 = (TextView) findViewById(R.id.textViewWork2);
        textView7 = (TextView) findViewById(R.id.textViewWork3);
        textView0 = (TextView) findViewById(R.id.textViewManageNo);
        textView0.setText(key);
        SetData(key);
    }

    public void mOnClick(View v) {

        switch (v.getId()){

            case R.id.btnImageControl:

                ViewPhotoControlActivity();
                break;

            case R.id.btnItemControl:
                ViewItemControlActivity();
                break;
        }
    }

    /**
     * 서버와 통신하여 데이터를 가져온다.
     */
    private void SetData(String key) {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(ViewActivity2.this, "",
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

        String restURL = Users.ServiceAddress+"getworder2/" + key;
        new ReadJSONFeedTask().execute(restURL);
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("ReadJSONFeedTask", "통신2");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetSupervisorWorder2Result");

                suworder3 = new SuWorder3();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    suworder3.WoNo = child.getString("SupervisorWoNo");
                    suworder3.WorkDate = child.getString("WorkDate");
                    suworder3.StartTime = child.getString("StartTime");
                    suworder3.EndTime = child.getString("EndTime");
                    suworder3.StayFlag = child.getString("StayFlag");
                    suworder3.WorkDescription1 = child.getString("WorkDescription1");
                    suworder3.WorkDescription2 = child.getString("WorkDescription2");
                    suworder3.WorkDescription3 = child.getString("WorkDescription3");
                    suworder3.WorkImage1 = child.getString("WorkImage1");
                    suworder3.WorkImage2 = child.getString("WorkImage2");
                    suworder3.GPSInfo = child.getString("GPSInfo");
                    suworder3.UpdateDate = child.getString("UpdateDate");
                    suworder3.StatusFlag = child.getString("StatusFlag");
                    suworder3.CustomerName = child.getString("CustomerName");
                    suworder3.LocationName = child.getString("LocationName");

                    Log.i("JSON", result);
                }
                SetControlFormData(suworder3);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param suworder3
     */
    private void SetControlFormData(SuWorder3 suworder3) {

        mProgressDialog.dismiss();

        if (!suworder3.StartTime.toString().equals("null") && !suworder3.StartTime.toString().equals("")) {
            textView1.setText(suworder3.CustomerName + "(" + suworder3.LocationName + ")"); //시작일자
        }

        if (!suworder3.StartTime.toString().equals("null") && !suworder3.StartTime.toString().equals("")) {
            textView2.setText(suworder3.WorkDate); //시작일자
            textView3.setText(suworder3.StartTime + " ~ " + suworder3.EndTime); //시작일자
        }

        if (!suworder3.StayFlag.toString().equals("null") && !suworder3.StayFlag.toString().equals("")) {
            if (suworder3.StayFlag.equals("1")) {

                textView4.setText("Yes");
            } else {
                textView4.setText("No");
            }
        }
        if (!suworder3.WorkDescription1.toString().equals("null") && !suworder3.WorkDescription1.toString().equals("")) {
            textView5.setText(suworder3.WorkDescription1);
        }

        if (!suworder3.WorkDescription2.toString().equals("null") && !suworder3.WorkDescription2.toString().equals("")) {

            textView6.setText(suworder3.WorkDescription2);
        }

        if (!suworder3.WorkDescription3.toString().equals("null") && !suworder3.WorkDescription3.toString().equals("")) {

            textView7.setTag(suworder3.WorkDescription3);
            textView7.setText(MakeArrayContent(suworder3.WorkDescription3));
        }
    }

    private String MakeArrayContent(String arrayString) {

        String[] arrayContent = arrayString.split("/");
        String result;

        if (arrayContent.length > 0) {
            try {
                result = "";

                for (int i = 0; i < arrayContent.length; i++) {

                    if (i % 4 == 1) {

                        result += " " + arrayContent[i];
                    }

                    if (i % 4 == 2) {
                        result += " " + arrayContent[i];
                        if (!arrayContent[i].trim().equals(""))
                            result += "km";
                    }

                    if (i % 4 == 3) {
                        result += " " + arrayContent[i];
                    }

                    if (i % 4 == 0) {
                        if (i != 0) {
                            result += "\n";
                        }
                        result += "" + arrayContent[i];

                        if (!arrayContent[i].trim().equals(""))
                            result += "→";
                    }
                }
                return result;

            } catch (Exception ex) {

                return "";
            }
        } else

            return "";
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


    private void ViewPhotoControlActivity() {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(ViewActivity2.this, "",
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
        images = new ArrayList<WoImage>();

        String restURL = Users.ServiceAddress+"getimagelist/" + this.key;
        new GetImageFeedTask().execute(restURL);
    }

    private void ViewItemControlActivity() {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(ViewActivity2.this, "",
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
        items = new ArrayList<WoItem>();

        String restURL = Users.ServiceAddress+"getitemlist/" + this.key;
        new GetItemFeedTask().execute(restURL);
    }


    /**
     * 이미지 목록
     */
    private class GetImageFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }


        protected void onPostExecute(String result) {
            try {

                Log.i("GetImageFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetImageAllResult");

                image = new WoImage();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    image.WoNo = child.getString("SupervisorWoNo");
                    image.SeqNo = child.getString("SeqNo");
                    image.ImageName = child.getString("ImageName");
                    image.SmallImageFile = child.getString("Imagefile");
                    image.ImageFile = "";

                    images.add(new WoImage(image.WoNo, image.SeqNo, image.ImageName, image.ImageFile, image.SmallImageFile));
                }
                Intent i = new Intent(getBaseContext(), PhotoListActivity.class);
                i.putExtra("data", images);
                i.putExtra("fix", "2");
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");
                i.putExtra("enableFlag", true);
                mProgressDialog.dismiss();
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 이미지 목록
     */
    private class GetItemFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }


        protected void onPostExecute(String result) {
            try {

                Log.i("GetItemFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetItemAllResult");

                item = new WoItem();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                    item.WoNo = child.getString("SupervisorWoNo");
                    item.SeqNo = child.getString("SeqNo");
                    item.ItemName = child.getString("ItemName");
                    item.PersonCount = child.getString("PersonCount");
                    item.Quantity = child.getString("Quantity");

                    items.add(new WoItem(item.WoNo, item.SeqNo, item.ItemName, item.PersonCount, item.Quantity));
                }
                Intent i = new Intent(getBaseContext(), PItemListActivity.class);
                i.putExtra("data", items);
                i.putExtra("fix", "2");
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");

                mProgressDialog.dismiss();
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
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

            jsonObject.put("SupervisorWoNo", suworder3.WoNo);
            jsonObject.put("StartTime", suworder3.StartTime);
            jsonObject.put("EndTime", suworder3.EndTime);
            jsonObject.put("StayFlag", suworder3.StayFlag);
            jsonObject.put("WorkDescription1", suworder3.WorkDescription1);
            jsonObject.put("WorkDescription2", suworder3.WorkDescription2);
            jsonObject.put("WorkDescription3", suworder3.WorkDescription3);
            jsonObject.put("WorkImage1", "");
            jsonObject.put("WorkImage2", "");
            jsonObject.put("GPSInfo", suworder3.GPSInfo);
            jsonObject.put("UpdateDate", suworder3.UpdateDate);

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

}

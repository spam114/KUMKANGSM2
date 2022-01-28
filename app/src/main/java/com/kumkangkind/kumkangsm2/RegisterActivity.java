package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.location.LocationListener;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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



public class RegisterActivity extends Activity {

    public static final int RESULT_TEXTVIEW1 = 0;
    public static final int RESULT_TEXTVIEW2 = 1;
    public static final int RESULT_TEXTVIEW3 = 2;
    public static final int RESULT_GALLERY1 = 3;
    public static final int RESULT_GALLERY2 = 4;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textViewImage1;
    TextView textViewImage2;
    TextView textViewTime1;
    TextView textViewTime2;
    Button buttonImageView1;
    Button buttonImageView2;
    String key = "";
    Handler mHandler;
    TextView textViewManageNo;
    TextView textViewCustomer;
    TextView textViewRealDate;

    Button btnNext;
    Button btnPre;
    SuWorder3 suworder3;
    WoImage image;
    WoItem item;
    ArrayList<WoImage> images;
    ArrayList<WoItem> items;

    RadioButton radioButton1;
    RadioButton radioButton2;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour1;
    private int mMinute1;
    private int mHour2;
    private int mMinute2;
    private String mAddress;


    ProgressDialog mProgressDialog;
    LocationManager lm;
    DispLocListener locListenD;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workedit);


        key = getIntent().getStringExtra("key");

        mAddress = "X";

        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locListenD = new DispLocListener();

            //CheckLocation();
        } catch (Exception e) {
            Log.i("location error", e.getMessage());
        }

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textViewTime1 = (TextView) findViewById(R.id.textViewTime1);
        textViewTime2 = (TextView) findViewById(R.id.textViewTime2);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPre = (Button) findViewById(R.id.btnPrev);
        textViewManageNo = (TextView) findViewById(R.id.textViewManageNo);
        textViewManageNo.setText(key);
        this.btnNext.setClickable(false);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        textViewCustomer = (TextView) findViewById(R.id.textViewCustomer);
        textViewRealDate = (TextView) findViewById(R.id.textViewRealDate);

        SetDate();
        SetTime();
        SetData(key);
    }


    private void SetDate() {
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DATE);
    }


    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    textViewRealDate.setText(String.format("%d-%d-%d", mYear,
                            mMonth + 1, mDay));

                    suworder3.WorkDate = textViewRealDate.getText().toString();
                }
            };

    /**
     * 현재일자로 시간을 맞춘다.
     */
    private void SetTime() {

        Calendar cal = new GregorianCalendar();
        mHour1 = cal.get(Calendar.HOUR_OF_DAY);
        mMinute1 = cal.get(Calendar.MINUTE);
        mHour2 = cal.get(Calendar.HOUR_OF_DAY);
        mMinute2 = cal.get(Calendar.MINUTE);
        mDay = cal.get(Calendar.DATE);
    }

    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnNext:

                new AlertDialog.Builder(this).setMessage("등록할까요?").setCancelable(true).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //POST 명령어 호출(업데이트를 적용한다)

                        mHandler = new Handler();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "",
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
                        SaveDataFromControl();
                        new HttpAsyncTask().execute(getString(R.string.service_address)+"setworder");
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;


            case R.id.btnPrev:

                finish();

                break;
            case R.id.textView1:

                CallEditTextActivity("작업내용", v, RESULT_TEXTVIEW1);
                Log.i(this.toString(), "작업내용 클릭");
                break;

            case R.id.textView2:

                CallEditTextActivity("특이사항", v, RESULT_TEXTVIEW2);
                Log.i(this.toString(), "특이사항 클릭");
                break;


            case R.id.textView3:

                TextView txView = (TextView) v;

                String content = "";
                if (txView.getTag() != null)
                    content = txView.getTag().toString();

                Intent intent3 = new Intent(this, VehicleRegisterActivity.class);
                intent3.putExtra("title", "차량운행");
                intent3.putExtra("content", content);
                startActivityForResult(intent3, RESULT_TEXTVIEW3);

                Log.i(this.toString(), "차량운행내역 클릭");
                break;

            case R.id.textViewTime1:
                ShowTimePickDialog(R.id.textViewTime1);
                break;

            case R.id.textViewTime2:
                ShowTimePickDialog(R.id.textViewTime2);
                break;

            case R.id.btnImageControl:

                ViewPhotoControlActivity();
                break;

            case R.id.btnItemControl:
                ViewItemControlActivity();
                break;

            case R.id.textViewRealDate:
                ShowDatePickDialog();
                break;
        }
    }

    private void ShowDatePickDialog() {

        new DatePickerDialog(RegisterActivity.this, mDateSetListener1, mYear, mMonth, mDay).show();
    }


    /**
     * 서버와 통신하여 데이터를 가져온다.
     */
    private void SetData(String key) {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "",
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

        String restURL = getString(R.string.service_address)+"getworder2/" + key;
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

        /**
         * null이 아닐 경우 뷰에 넣는다.
         */

        if (!suworder3.StatusFlag.toString().equals("null") && !suworder3.StatusFlag.toString().equals("")) {

            if (suworder3.StatusFlag.equals("0")) {

                //지시
                this.btnNext.setClickable(true);
                this.btnNext.setText("저장");
            } else if (suworder3.StatusFlag.equals("1")) {

                //작성
                this.btnNext.setClickable(true);
                this.btnNext.setText("저장");
            } else {
                //확정
                this.btnNext.setClickable(false);
                this.btnNext.setText("변경 불가");
            }

            mProgressDialog.dismiss();
        }

        if (!suworder3.StartTime.toString().equals("null") && !suworder3.StartTime.toString().equals("")) {
            textViewRealDate.setText(suworder3.WorkDate);
            textViewTime1.setText(suworder3.StartTime); //시작일자
        }

        if (!suworder3.EndTime.toString().equals("null") && !suworder3.EndTime.toString().equals("")) {
            textViewTime2.setText(suworder3.EndTime);  //종료일자
        }
        if (!suworder3.StayFlag.toString().equals("null") && !suworder3.StayFlag.toString().equals("")) {
            if (suworder3.StayFlag.equals("1")) {
                this.radioButton1.setChecked(true); //숙박
            } else {
                this.radioButton2.setChecked(true);
            }
        }
        if (!suworder3.WorkDescription1.toString().equals("null") && !suworder3.WorkDescription1.toString().equals("")) {
            textView1.setText(suworder3.WorkDescription1);
        }
        if (!suworder3.WorkDescription2.toString().equals("null") && !suworder3.WorkDescription2.toString().equals("")) {

            textView2.setText(suworder3.WorkDescription2);
        }
        if (!suworder3.WorkDescription3.toString().equals("null") && !suworder3.WorkDescription3.toString().equals("")) {

            textView3.setTag(suworder3.WorkDescription3);

            textView3.setText(MakeArraycontent(suworder3.WorkDescription3));

        }

        if (!suworder3.GPSInfo.toString().equals("null") && !suworder3.GPSInfo.toString().equals("")) {

        }

        if (!suworder3.UpdateDate.toString().equals("null") && !suworder3.UpdateDate.toString().equals("")) {

        }

        if (!suworder3.CustomerName.toString().equals("null") && !suworder3.CustomerName.toString().equals("")) {

            textViewCustomer.setText(suworder3.CustomerName + "(" + suworder3.LocationName + ")");
        }

    }

    private String MakeArraycontent(String arrayString) {

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
                        ;
                    }

                    if (i % 4 == 3) {
                        result += " " + arrayContent[i];
                    }

                    if (i % 4 == 0) {
                        if (i != 0) {
                            result += "\n";
                        }
                        result += " " + arrayContent[i];

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


    /**
     *
     */
    private void ViewPhotoControlActivity() {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "",
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

        String restURL = getString(R.string.service_address)+"getimagelist/" + this.key;
        new GetImageFeedTask().execute(restURL);
    }


    private void ViewItemControlActivity() {

        mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "",
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

        String restURL = getString(R.string.service_address)+"getitemlist/" + this.key;
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
                i.putExtra("fix", suworder3.StatusFlag);
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");

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
                i.putExtra("fix", suworder3.StatusFlag);
                i.putExtra("key", key);
                i.putExtra("customer", suworder3.CustomerName + "(" + suworder3.LocationName + ")");

                mProgressDialog.dismiss();
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 임시저장을 한다 Preference에
     */
    private void SaveTemp() {

        //키는 작업지시 번호이다.
        SharedPreferences pref = getSharedPreferences(key, 0);
        SharedPreferences.Editor edit = pref.edit();


        edit.putString("time1", this.textView1.getText().toString());        //시간1
        edit.putString("time2", this.textView1.getText().toString());        //시간2
        edit.putString("stay", this.textView1.getText().toString());         //숙박
        edit.putString("content1", this.textView1.getText().toString());     //작업내용
        edit.putString("content2", this.textView1.getText().toString());     //특이사항
        edit.putString("content3", this.textView1.getText().toString());     //차량사용
        edit.putString("photo1", this.textView1.getText().toString());       //사진1
        edit.putString("photo2", this.textView1.getText().toString());       //사진2

        edit.commit();
    }

    /**
     * 임시 저장된 내역을 불러온다.
     */
    private void LoadTemp() {

        SharedPreferences pref = getSharedPreferences(key, 0);

        String time1 = pref.getString("time1", "-1");
        String time2 = pref.getString("time2", "-1");
        String stay = pref.getString("stay", "-1");
        String content1 = pref.getString("content1", "-1");
        String content2 = pref.getString("content2", "-1");
        String content3 = pref.getString("content3", "-1");
        String photo1 = pref.getString("photo1", "-1");
        String photo2 = pref.getString("photo2", "-1");
    }


    //inSampleSize 설정
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * 날짜를 선택할 수있는 다이아로그를 호출한다.
     */
    private void ShowTimePickDialog(int viewId) {

        if (viewId == R.id.textViewTime1)
            new TimePickerDialog(this, mTimeSetListener1, mHour1, mMinute1, true).show();

        else
            new TimePickerDialog(this, mTimeSetListener2, mHour2, mMinute2, true).show();
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mHour1 = hourOfDay;
            mMinute1 = minute;

            Date date = new Date();
            date.setHours(mHour1);
            date.setMinutes(mMinute1);
            String data = new SimpleDateFormat("HH:mm").format(date);
            textViewTime1.setText(data);
        }

    };

    TimePickerDialog.OnTimeSetListener mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mHour2 = hourOfDay;
            mMinute2 = minute;

            Date date = new Date();
            date.setHours(mHour2);
            date.setMinutes(mMinute2);
            String data = new SimpleDateFormat("HH:mm").format(date);
            textViewTime2.setText(data);
        }

    };

    /**
     * 타이틀 제목과 현재 입력된 내용을 넘긴다.
     *
     * @param title
     * @param v
     */
    private void CallEditTextActivity(String title, View v, int resultID) {

        TextView txView = (TextView) v;

        String content = txView.getText().toString();
        Intent intent = new Intent(this, EditTextActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        startActivityForResult(intent, resultID);
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

        if (requestCode == RESULT_TEXTVIEW1 && resultCode == RESULT_OK && null != data) {
            Log.i("onActivityResult", "결과 받아옴");
            String content = data.getStringExtra("content");
            textView1.setText(content);
        } else if (requestCode == RESULT_TEXTVIEW2 && resultCode == RESULT_OK && null != data) {

            String content = data.getStringExtra("content");
            Log.i("onActivityResult", "결과 받아옴");
            textView2.setText(content);

        } else if (requestCode == RESULT_TEXTVIEW3 && resultCode == RESULT_OK && null != data) {

            String content = data.getStringExtra("content");
            Log.i("onActivityResult", "결과 받아옴");

            textView3.setTag(content);
            String newContent = this.MakeArraycontent(content);
            textView3.setText(newContent);
        } else if (requestCode == RESULT_GALLERY1 && resultCode == RESULT_OK && null != data) {

            buttonImageView1.setTag(data.getData());

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String imgPath = cursor.getString(column_index);
            textViewImage1.setText("O");

            Uri uri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                textViewImage1.setTag(Base64.encodeToString(byteArray, Base64.DEFAULT));

            } catch (Exception ex) {

            }
        } else if (requestCode == RESULT_GALLERY1 && resultCode == RESULT_CANCELED) {

            buttonImageView1.setTag(null); //URI
            textViewImage1.setText("");    //텍스트
            textViewImage1.setTag(null);   //byteString
        } else if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_OK && null != data) {

            buttonImageView2.setTag(data.getData());

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(column_index);
            textViewImage2.setText("O");


            Uri uri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                textViewImage2.setTag(Base64.encodeToString(byteArray, Base64.DEFAULT));

            } catch (Exception ex) {

            }
        } else if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_CANCELED) {

            buttonImageView2.setTag(null); //URI
            textViewImage2.setText("");    //텍스트
            textViewImage2.setTag(null);   //byteString
        }
    }

    /**
     * 현재 위치를 체크한다.
     * 얻을 정보 : 현재위치, 위치를 가져온 시각
     */
    private void CheckLocation() {

        Log.i("CheckLocation", "진입");
        try {
            //업데이트 해달라고 요청함.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("GPS", "Enabled");

            } else {

                Log.i("GPS", "Disabled");
            }

            lm.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);
            String mProvider = lm.getBestProvider(new Criteria(), true);
            Location lastKnownLocation = lm.getLastKnownLocation(mProvider);

            Log.i("경도", Double.toString(lastKnownLocation.getLatitude()));
            Log.i("위도", Double.toString(lastKnownLocation.getLongitude()));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            Log.i("City", city);
            String state = addresses.get(0).getAdminArea();
            Log.i("state", state);
            //String zip = addresses.get(0).getPostalCode();
            // Log.i("zip", zip);
            String country = addresses.get(0).getCountryName();
            Log.i("country", country);
            String address = addresses.get(0).getAddressLine(0);
            Log.i("address", address);
            mAddress = address;
            // Toast.makeText(this,  address +"===>" + String.valueOf(ConvertToTime(lastKnownLocation.getTime())), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "GPS를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private String ConvertToTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddkkmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.valueOf(date);
    }


    private class DispLocListener implements LocationListener {

        public void onLocationChanged(Location location) {
            // TextView를 업데이트 한다.
            Log.i("경도", Double.toString(location.getLatitude()));
            Log.i("위도", Double.toString(location.getLongitude()));
            Log.d("location", "location is null");
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
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


    /**
     * 서버와 통신하여 넣는다.
     * * @param suworder3
     */
    private void SaveDataFromControl() {

        String startTime = suworder3.WorkDate + " " + textViewTime1.getText().toString();
        String endTime = suworder3.WorkDate + " " + textViewTime2.getText().toString();

        suworder3.WoNo = key;
        //suworder3.StartTime = textViewTime1.getText().toString() + "StartTime"
        //suworder3.EndTime =  textViewTime2.getText().toString();

        suworder3.StartTime = startTime;
        suworder3.EndTime = endTime;
        suworder3.StayFlag = radioButton1.isChecked() == true ? "1" : "0";
        suworder3.WorkDescription1 = textView1.getText().toString();
        suworder3.WorkDescription2 = textView2.getText().toString();

        if (textView3.getTag() != null)
            suworder3.WorkDescription3 = textView3.getTag().toString();

        suworder3.GPSInfo = mAddress;
        suworder3.UpdateDate = textViewTime2.getText().toString();
    }
}

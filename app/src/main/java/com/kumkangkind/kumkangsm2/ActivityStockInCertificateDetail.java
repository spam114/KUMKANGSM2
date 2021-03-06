package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kumkangkind.kumkangsm2.Object.StockInCertificate;
import com.kumkangkind.kumkangsm2.Object.StockInCertificateImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;


public class ActivityStockInCertificateDetail extends BaseActivity {

    Dialog dialog;

    ArrayList<StockInCertificateImage> stockInCertificateImageArrayList;
    String certificateNo;
    String customerLocationName;

    boolean firstFlag = true;

    TextView textViewManageNo;
    TextView textViewManageNo2;
    TextView textViewManageNo3;
    TextView textViewManageNo4;
    TextView textViewManageNo5;
    TextView textViewCustomer;
    TextView tvStartTime;
    TextView tvEndTime;
    TextView textView2;
    EditText edtBundleQty;
    EditText edtFloor;
    EditText edtCarNo;
    Spinner spinnerDestination;

    private int mYear;
    private int mMonth;
    private int mDay;

    private int mYear2;
    private int mMonth2;
    private int mDay2;

    private int maxCount;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;

    Button btnDelete;
    Button btnImageAll;

    File filePath;

    String imageNo;
    android.widget.Button btnNext;
    private ActivityResultLauncher<Intent> resultLauncher;
    String locationNo;
    String seqNo;
    String supervisorCode;//??????????????? ????????? ????????? supervisorCode

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
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_certificate_detail);
        setFilePath();


        this.certificateNo = getIntent().getStringExtra("certificateNo");
        this.customerLocationName = getIntent().getStringExtra("customerLocationName");
        this.locationNo = getIntent().getStringExtra("locationNo");
        this.supervisorCode = getIntent().getStringExtra("supervisorCode");

        textViewManageNo = findViewById(R.id.textViewManageNo);
        textViewManageNo2 = findViewById(R.id.textViewManageNo2);
        textViewManageNo3 = findViewById(R.id.textViewManageNo3);
        textViewManageNo4 = findViewById(R.id.textViewManageNo4);
        textViewManageNo5 = findViewById(R.id.textViewManageNo5);
        textViewCustomer = findViewById(R.id.textViewCustomer);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        edtBundleQty = findViewById(R.id.edtBundleQty);
        edtFloor = findViewById(R.id.edtFloor);
        edtCarNo = findViewById(R.id.edtCarNo);
        btnNext = findViewById(R.id.btnNext);
        btnDelete = findViewById(R.id.btnDelete);
        btnImageAll = findViewById(R.id.btnImageAll);
        spinnerDestination = findViewById(R.id.spinnerDestination);
        imageView1 = findViewById(R.id.imageView1);
        imageView1.setTag("");
        //imageView1.setOnTouchListener(mScaleGestureDetector);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setTag("");
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setTag("");
        imageView4 = findViewById(R.id.imageView4);
        imageView4.setTag("");
        imageView5 = findViewById(R.id.imageView5);
        imageView5.setTag("");
        imageView6 = findViewById(R.id.imageView6);
        imageView6.setTag("");
        textView2 = findViewById(R.id.textView2);


        if (!this.supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ???????????? ????????????
            this.tvStartTime.setEnabled(false);
            this.tvEndTime.setEnabled(false);
            this.edtCarNo.setEnabled(false);
            this.edtBundleQty.setEnabled(false);
            this.edtFloor.setEnabled(false);
            this.spinnerDestination.setEnabled(false);
            /*this.imageView1.setEnabled(false);
            this.imageView2.setEnabled(false);
            this.imageView3.setEnabled(false);
            this.imageView4.setEnabled(false);
            this.imageView5.setEnabled(false);
            this.imageView6.setEnabled(false);*/
            this.btnNext.setEnabled(false);
            this.btnImageAll.setEnabled(false);
            this.btnDelete.setEnabled(false);
        }

        String[] workTypes = new String[3];
        workTypes[0] = "??????1??????";
        workTypes[1] = "??????3??????";
        workTypes[2] = "??????1,3??????";
        ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(ActivityStockInCertificateDetail.this, android.R.layout.simple_spinner_dropdown_item, workTypes);
        spinnerDestination.setAdapter(workTypeAdapter);

        textViewCustomer.setText(this.customerLocationName);

        if (this.certificateNo.equals("")) {//??????
            //this.btnNext.setClickable(false);

            //?????? ????????? ??????
            Calendar cal = new GregorianCalendar();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DATE);

            mYear2 = cal.get(Calendar.YEAR);
            mMonth2 = cal.get(Calendar.MONTH);
            mDay2 = cal.get(Calendar.DATE);

            this.tvStartTime.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
            this.tvEndTime.setText(mYear2 + "-" + (mMonth2 + 1) + "-" + mDay2);
            this.btnNext.setText("????????????");
            textViewManageNo.setText("'????????????' ????????? ?????? ????????? ???????????????.");
            /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewManageNo.setLayoutParams(layoutParams);*/
            textViewManageNo.setTextColor(Color.YELLOW);
            textViewManageNo.setTextSize(16);
            textViewManageNo2.setVisibility(View.GONE);
            textViewManageNo3.setVisibility(View.GONE);
            textViewManageNo4.setVisibility(View.GONE);
            textViewManageNo5.setVisibility(View.GONE);
        } else {
            getStockInCertificateDetail();
            this.btnNext.setText("????????????");
        }

        //???????????? ?????? ??????
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //Intent intent = result.getData();
                        if (filePath != null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            try {
                                InputStream in = new FileInputStream(filePath);
                                BitmapFactory.decodeStream(in, null, options);
                                in.close();
                                in = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //final int width = options.outWidth;
                            //final int height = options.outHeight;
                            // width, height ?????? ?????? inSaampleSize ??? ??????

                            BitmapFactory.Options imgOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());


                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(filePath.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap bmRotated = rotateBitmap(bitmap, orientation);

                            byte[] byteArray = null;
                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                            bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                            byteArray = bStream.toByteArray();

                            StockInCertificateImage currentImage;
                            currentImage = new StockInCertificateImage();
                            currentImage.LocationNo = locationNo;
                            currentImage.SeqNo = seqNo;
                            currentImage.ImageNo = imageNo;
                            currentImage.ImageName = locationNo + "_" + seqNo + "_" + imageNo;
                            currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));

                            InsertOrUpdateStockInCertificateImage(currentImage);
                        }
                    }
                });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "1";
                if (imageView1.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView1.getTag());
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "2";
                if (imageView2.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView2.getTag());
                }
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "3";
                if (imageView3.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView3.getTag());
                }
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "4";
                if (imageView4.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView4.getTag());
                }
            }
        });
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "5";
                if (imageView5.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView5.getTag());
                }
            }
        });
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = "6";
                if (imageView6.getTag().toString().equals("")) {
                    //???????????????
                    if (certificateNo.equals("")) {
                        Toast.makeText(ActivityStockInCertificateDetail.this, "'????????????' ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ?????? ????????????
                        RegisterPicture();
                    }
                } else {
                    ViewData((byte[]) imageView6.getTag());
                }
            }
        });

        edtCarNo.addTextChangedListener(new MyWatcher2(btnNext));
        edtBundleQty.addTextChangedListener(new MyWatcher2(btnNext));
        edtFloor.addTextChangedListener(new MyWatcher2(btnNext));
        tvStartTime.addTextChangedListener(new MyWatcher2(btnNext));
        tvEndTime.addTextChangedListener(new MyWatcher2(btnNext));

        spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstFlag)
                    btnNext.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDestination.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                firstFlag = false;
                return false;
            }
        });
        //SetDate();
        //SetTime();
        //MakeSpinnerWorkTypeAndData();

    }

    private void setFilePath() {
        try {
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            filePath = File.createTempFile("IMG", ".jpg", dir);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (Exception et) {
            et.getMessage();
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public static Bitmap rotateBitmap2(Bitmap bitmap) {

    }*/

    private void StartCamera() {
        try {
            Uri photoUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", filePath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));

            resultLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String compressImage2(String jsonString) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // 1/4????????? ???????????? ?????? ??????

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        int targetWidth = 1000; // your arbitrary fixed limit
        int targetHeight = (int) (decodedByte.getHeight() * targetWidth / (double) decodedByte.getWidth());
        /*
        options = new BitmapFactory.Options();
        options.outHeight = targetHeight;
        options.outWidth = 480;
        */
        //decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        Bitmap resized = Bitmap.createScaledBitmap(decodedByte, targetWidth, targetHeight, true);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, bStream);
        byte[] byteArray = bStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void deleteStockInCertificateImage(String locationNo, String seqNo, String imageNo) {
        String url = getString(R.string.service_address) + "deleteStockInCertificateImage";
        ContentValues values = new ContentValues();

        values.put("LocationNo", locationNo);
        values.put("SeqNo", seqNo);
        values.put("ImageNo", imageNo);

        DeleteStockInCertificateImage gsod = new DeleteStockInCertificateImage(url, values);
        gsod.execute();
    }


    public class DeleteStockInCertificateImage extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteStockInCertificateImage(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stockInCertificateArrayList.add(stockInCertificate);
                }

                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                getStockInCertificateDetail();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private void insertStockInCertificate() {
        String url = getString(R.string.service_address) + "insertStockInCertificate";
        ContentValues values = new ContentValues();

        values.put("LocationNo", locationNo);
        //values.put("SeqNo", currentImage.SeqNo);//???????????? SeqNo ????????? ?????????
        values.put("SupervisorCode", Users.USER_ID);
        values.put("StartTime", tvStartTime.getText().toString());
        values.put("EndTime", tvEndTime.getText().toString());
        values.put("BundleQty", edtBundleQty.getText().toString());
        values.put("Floor", edtFloor.getText().toString());
        values.put("CarNo", edtCarNo.getText().toString());
        String destination = Integer.toString(spinnerDestination.getSelectedItemPosition() + 1);
        values.put("Destination", destination);
        values.put("InsertUser", Users.PhoneNumber);

        InsertStockInCertificate gsod = new InsertStockInCertificate(url, values);
        gsod.execute();
    }


    public class InsertStockInCertificate extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        InsertStockInCertificate(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    certificateNo = child.getString("CertificateNo");
                    //stockInCertificateArrayList.add(stockInCertificate);
                }
                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                getStockInCertificateDetail();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private void deleteStockInCertificate() {
        String url = getString(R.string.service_address) + "deleteStockInCertificate";
        ContentValues values = new ContentValues();

        values.put("LocationNo", locationNo);
        values.put("SeqNo", seqNo);

        DeleteStockInCertificate gsod = new DeleteStockInCertificate(url, values);
        gsod.execute();
    }


    public class DeleteStockInCertificate extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteStockInCertificate(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stockInCertificateArrayList.add(stockInCertificate);
                }
                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                progressOFF2(this.getClass().getName());
                ActivityStockInCertificateDetail.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private void updateStockInCertificate() {
        String url = getString(R.string.service_address) + "updateStockInCertificate";
        ContentValues values = new ContentValues();

        values.put("LocationNo", locationNo);
        values.put("SeqNo", seqNo);
        values.put("SupervisorCode", Users.USER_ID);
        values.put("StartTime", tvStartTime.getText().toString());
        values.put("EndTime", tvEndTime.getText().toString());
        values.put("BundleQty", edtBundleQty.getText().toString());
        values.put("Floor", edtFloor.getText().toString());
        values.put("CarNo", edtCarNo.getText().toString());
        String destination = Integer.toString(spinnerDestination.getSelectedItemPosition() + 1);
        values.put("Destination", destination);
        values.put("InsertUser", Users.PhoneNumber);

        UpdateStockInCertificate gsod = new UpdateStockInCertificate(url, values);
        gsod.execute();
    }


    public class UpdateStockInCertificate extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        UpdateStockInCertificate(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stockInCertificateArrayList.add(stockInCertificate);
                }
                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                getStockInCertificateDetail();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private void InsertOrUpdateStockInCertificateImage(StockInCertificateImage currentImage) {
        String url = getString(R.string.service_address) + "insertOrUpdateStockInCertificateImage";
        ContentValues values = new ContentValues();

        values.put("LocationNo", currentImage.LocationNo);
        values.put("SeqNo", currentImage.SeqNo);
        values.put("ImageNo", currentImage.ImageNo);
        values.put("ImageName", currentImage.ImageName);
        values.put("ImageFile", currentImage.ImageFile);

        InsertOrUpdateStockInCertificateImage gsod = new InsertOrUpdateStockInCertificateImage(url, values);
        gsod.execute();
    }


    public class InsertOrUpdateStockInCertificateImage extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        InsertOrUpdateStockInCertificateImage(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stockInCertificateArrayList.add(stockInCertificate);
                }
                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                getStockInCertificateDetail();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private void getStockInCertificateDetail() {
        String url = getString(R.string.service_address) + "getStockInCertificateDetail";
        ContentValues values = new ContentValues();
        values.put("CertificateNo", certificateNo);
        GetStockInCertificateDetail gsod = new GetStockInCertificateDetail(url, values);
        gsod.execute();
    }

    public class GetStockInCertificateDetail extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockInCertificateDetail(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("????????????", "??????/????????????");
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {

                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificate stockInCertificate = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockInCertificate = new StockInCertificate();
                    stockInCertificate.LocationNo = child.getString("LocationNo");
                    stockInCertificate.SeqNo = child.getString("SeqNo");
                    stockInCertificate.SupervisorCode = child.getString("SupervisorCode");
                    stockInCertificate.StartTime = child.getString("StartTime");
                    stockInCertificate.EndTime = child.getString("EndTime");
                    stockInCertificate.BundleQty = child.getString("BundleQty");
                    stockInCertificate.Floor = child.getString("Floor");
                    stockInCertificate.CarNo = child.getString("CarNo");
                    stockInCertificate.Destination = child.getString("Destination");
                    stockInCertificate.cust_code = child.getString("cust_code");

                    locationNo = stockInCertificate.LocationNo;
                    seqNo = stockInCertificate.SeqNo;

                    if (stockInCertificate.Destination.equals("1") || stockInCertificate.Destination.equals("2")) {
                        textView2.setVisibility(View.INVISIBLE);
                        imageView2.setVisibility(View.INVISIBLE);
                        maxCount = 5;
                    } else {
                        textView2.setVisibility(View.VISIBLE);
                        imageView2.setVisibility(View.VISIBLE);
                        maxCount = 6;
                    }
                }


                if (!stockInCertificate.StartTime.equals("null") && !stockInCertificate.StartTime.equals("")) {
                    tvStartTime.setText(stockInCertificate.StartTime);
                    mYear = Integer.parseInt(stockInCertificate.StartTime.split("-")[0]);
                    mMonth = Integer.parseInt(stockInCertificate.StartTime.split("-")[1]) - 1;
                    mDay = Integer.parseInt(stockInCertificate.StartTime.split("-")[2]);
                }

                if (!stockInCertificate.EndTime.equals("null") && !stockInCertificate.EndTime.equals("")) {
                    tvEndTime.setText(stockInCertificate.EndTime);
                    mYear2 = Integer.parseInt(stockInCertificate.EndTime.split("-")[0]);
                    mMonth2 = Integer.parseInt(stockInCertificate.EndTime.split("-")[1]) - 1;
                    mDay2 = Integer.parseInt(stockInCertificate.EndTime.split("-")[2]);
                }

                btnNext.setText("????????????");
                textViewManageNo.setText("?????? No: ");
                textViewManageNo2.setText(stockInCertificate.cust_code);
                textViewManageNo5.setText(stockInCertificate.LocationNo + "-" + stockInCertificate.SeqNo);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewManageNo.setLayoutParams(layoutParams);
                textViewManageNo.setTextColor(Color.WHITE);
                textViewManageNo.setTextSize(16);
                textViewManageNo2.setVisibility(View.VISIBLE);
                textViewManageNo3.setVisibility(View.VISIBLE);
                textViewManageNo4.setVisibility(View.VISIBLE);
                textViewManageNo5.setVisibility(View.VISIBLE);
                certificateNo = stockInCertificate.LocationNo + "-" + stockInCertificate.SeqNo;

                if (!stockInCertificate.BundleQty.equals("null") && !stockInCertificate.BundleQty.equals("")) {
                    edtBundleQty.setText(stockInCertificate.BundleQty);

                }

                if (!stockInCertificate.Floor.equals("null") && !stockInCertificate.Floor.equals("")) {
                    edtFloor.setText(stockInCertificate.Floor);

                }

                if (!stockInCertificate.CarNo.equals("null") && !stockInCertificate.CarNo.equals("")) {
                    edtCarNo.setText(stockInCertificate.CarNo);

                }

                if (!stockInCertificate.Destination.equals("null") && !stockInCertificate.Destination.equals("")) {

                    if (stockInCertificate.Destination.equals("1")) {
                        spinnerDestination.setSelection(0);
                    } else if (stockInCertificate.Destination.equals("2")) {
                        spinnerDestination.setSelection(1);
                    } else if (stockInCertificate.Destination.equals("3")) {
                        spinnerDestination.setSelection(2);
                    }
                }
                btnNext.setBackgroundColor(Color.TRANSPARENT);

                //????????? ????????? ??????
                getStockInCertificateImage(stockInCertificate.LocationNo, stockInCertificate.SeqNo);
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }

    private void getStockInCertificateImage(String locationNo, String seqNo) {
        String url = getString(R.string.service_address) + "getStockInCertificateImage";
        ContentValues values = new ContentValues();
        values.put("LocationNo", locationNo);
        values.put("SeqNo", seqNo);
        GetStockInCertificateImage gsod = new GetStockInCertificateImage(url, values);
        gsod.execute();
    }

    public class GetStockInCertificateImage extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockInCertificateImage(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("????????????", "??????/????????????");
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //?????? ?????????
                imageView1.setTag("");
                imageView1.setImageResource(R.drawable.add_48px);
                imageView2.setTag("");
                imageView2.setImageResource(R.drawable.add_48px);
                imageView3.setTag("");
                imageView3.setImageResource(R.drawable.add_48px);
                imageView4.setTag("");
                imageView4.setImageResource(R.drawable.add_48px);
                imageView5.setTag("");
                imageView5.setImageResource(R.drawable.add_48px);
                imageView6.setTag("");
                imageView6.setImageResource(R.drawable.add_48px);

                stockInCertificateImageArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                StockInCertificateImage stockInCertificateImage = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockInCertificateImage = new StockInCertificateImage();
                    stockInCertificateImage.LocationNo = child.getString("LocationNo");
                    stockInCertificateImage.SeqNo = child.getString("SeqNo");
                    stockInCertificateImage.ImageNo = child.getString("ImageNo");
                    stockInCertificateImage.ImageName = child.getString("ImageName");
                    stockInCertificateImage.ImageFile = child.getString("ImageFile");
                    stockInCertificateImageArrayList.add(stockInCertificateImage);
                }
                SetImage();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    private void ViewData(byte[] array) {

        try {
            dialog = new Dialog(this);
            //dialog.setTitle(imageName);
            dialog.setContentView(R.layout.dialog_image2);
            final Bitmap[] bm = new Bitmap[1];

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            WindowManager wm = ((WindowManager) ActivityStockInCertificateDetail.this.getApplicationContext().getSystemService(ActivityStockInCertificateDetail.this.getApplicationContext().WINDOW_SERVICE));
            lp.width = (int) (wm.getDefaultDisplay().getWidth() * 1.0);
            lp.height = (int) (wm.getDefaultDisplay().getHeight() * 1.0);

            dialog.getWindow().setAttributes(lp);

            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView imageView = dialog.findViewById(R.id.imageView1);
            //imageView.setImageBitmap(rotateBitmap2(BitmapFactory.decodeByteArray(array, 0, array.length)));
            bm[0] = BitmapFactory.decodeByteArray(array, 0, array.length);
            imageView.setImage(ImageSource.bitmap(bm[0]));

            TextView tvDelete = dialog.findViewById(R.id.tvDelete);
            TextView tvCamera = dialog.findViewById(R.id.tvCamera);
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            TextView tvRotate = dialog.findViewById(R.id.tvRotate);

            if (!this.supervisorCode.equals(Users.USER_ID)) {//????????? ????????? ????????? ???????????? ????????????
                tvDelete.setEnabled(false);
                tvCamera.setEnabled(false);
                tvRotate.setEnabled(false);
            }

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ActivityStockInCertificateDetail.this).setMessage("????????? ?????????????????????????").setCancelable(false).setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog2, int which) {
                            deleteStockInCertificateImage(locationNo, seqNo, imageNo);
                            dialog.cancel();
                        }
                    }).setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            });

            tvCamera.setOnClickListener(cameraClickListener);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            tvRotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] byteArray = null;
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                    //??????
                    bm[0] = rotateBitmap(bm[0], ExifInterface.ORIENTATION_ROTATE_90);
                    Bitmap bmRotated = bm[0];
                    bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byteArray = bStream.toByteArray();

                    StockInCertificateImage currentImage;
                    currentImage = new StockInCertificateImage();
                    currentImage.LocationNo = locationNo;
                    currentImage.SeqNo = seqNo;
                    currentImage.ImageNo = imageNo;
                    currentImage.ImageName = locationNo + "_" + seqNo + "_" + imageNo;
                    currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));

                    InsertOrUpdateStockInCertificateImage(currentImage);
                    imageView.setImage(ImageSource.bitmap(bmRotated));
                }
            });

            /*
            Button saveButton = (Button)dialog.findViewById(R.id.buttonImageSave);
            saveButton.setText("????????? ??????");
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    byte[] byteArray = Base64.decode(currentImage.ImageFile, Base64.DEFAULT);
                    saveBitmaptoJpeg( BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length), "KUMKANG", currentImage.ImageName);
                }

            });
            */

            dialog.show();

        } catch (Exception ex) {

            Log.e("??????", "????????? ?????? " + ex.getMessage().toString());
        }
    }


    TextView.OnClickListener cameraClickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RegisterPicture();
                }
            };

    private void RegisterPicture() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(ActivityStockInCertificateDetail.this);
        materialAlertDialogBuilder.setTitle("?????? ??????");
        CharSequence[] sequences = new CharSequence[2];
        sequences[0] = "?????? ??????";
        sequences[1] = "???????????? ??????";
        materialAlertDialogBuilder.setItems(sequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {//????????????
                    StartCamera();
                } else if (which == 1) {//???????????? ??????
                    TedImagePicker.with(ActivityStockInCertificateDetail.this)
                            .start(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NotNull Uri uri) {

                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(ActivityStockInCertificateDetail.this.getContentResolver(), uri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    byte[] byteArray = null;
                                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                                    ExifInterface exif = null;
                                    try {
                                        exif = new ExifInterface(getRealPathFromURI(uri));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);

                                    Bitmap bmRotated = rotateBitmap(bitmap, orientation);
                                    bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                                    byteArray = bStream.toByteArray();

                                    StockInCertificateImage currentImage;
                                    currentImage = new StockInCertificateImage();
                                    currentImage.LocationNo = locationNo;
                                    currentImage.SeqNo = seqNo;
                                    currentImage.ImageNo = imageNo;
                                    currentImage.ImageName = locationNo + "_" + seqNo + "_" + imageNo;
                                    currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));

                                    InsertOrUpdateStockInCertificateImage(currentImage);

                                }
                            });
                }
            }
        });
        materialAlertDialogBuilder.setCancelable(true);
        materialAlertDialogBuilder.show();
        if (dialog != null)
            dialog.cancel();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void SetImage() {
        for (int i = 0; i < stockInCertificateImageArrayList.size(); i++) {
            byte[] array = Base64.decode(stockInCertificateImageArrayList.get(i).ImageFile, Base64.DEFAULT);
            if (stockInCertificateImageArrayList.get(i).ImageNo.equals("1")) {
                imageView1.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView1.setTag(array);
            } else if (stockInCertificateImageArrayList.get(i).ImageNo.equals("2")) {
                imageView2.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView2.setTag(array);
            } else if (stockInCertificateImageArrayList.get(i).ImageNo.equals("3")) {
                imageView3.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView3.setTag(array);
            } else if (stockInCertificateImageArrayList.get(i).ImageNo.equals("4")) {
                imageView4.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView4.setTag(array);
            } else if (stockInCertificateImageArrayList.get(i).ImageNo.equals("5")) {
                imageView5.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView5.setTag(array);
            } else if (stockInCertificateImageArrayList.get(i).ImageNo.equals("6")) {
                imageView6.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
                imageView6.setTag(array);
            }
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    tvStartTime.setText(String.format("%d-%d-%d", mYear,
                            mMonth + 1, mDay));
                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear2 = year;
                    mMonth2 = monthOfYear;
                    mDay2 = dayOfMonth;

                    tvEndTime.setText(String.format("%d-%d-%d", mYear2,
                            mMonth2 + 1, mDay2));
                }
            };


    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnNext:

                if (!CheckInputData()) {
                    return;
                }

                if (certificateNo.equals("")) {//?????? ?????? ????????????

                    new AlertDialog.Builder(this)
                            .setTitle("???????????? ??????")
                            .setMessage("??????????????? ????????????????????????? ")
                            //.setIcon(R.drawable.ninja)
                            .setPositiveButton("??????",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            insertStockInCertificate();
                                        }
                                    })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {//Update ?????? ??????

                    new AlertDialog.Builder(this).setMessage("?????????????????????????").setCancelable(true).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateStockInCertificate();
                        }
                    }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }

                break;

            case R.id.tvStartTime:
                ShowDatePickDialog();
                break;

            case R.id.tvEndTime:
                ShowDatePickDialog2();
                break;

           /* case R.id.btnImageControl:
                if (key.equals("????????????")) {
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    progressOFF();
                    return;
                }
                ViewPhotoControlActivity();
                break;*/

            case R.id.btnImageAll:
                if (certificateNo.equals("")) {
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                TedImagePicker.with(this)
                        .max(maxCount, "?????? " + maxCount + "?????? ????????? ?????? ??? ????????????.")
                        .min(1, "1??? ????????? ????????? ????????? ?????????.")
                        .dropDownAlbum()
                        .startMultiImage(new OnMultiSelectedListener() {
                            @Override
                            public void onSelected(@NotNull List<? extends Uri> uriList) {
                                InsertStockInCertificateImageMulti((ArrayList<Uri>) uriList);
                                //showMultiImage(uriList);
                            }
                        });
                break;

            case R.id.btnDelete://????????????: ????????????, ???????????????, A/S??????, ??????????????? ????????????. ??????????????? ??????????????? ????????????.
                if (certificateNo.equals("")) {
                    Toast.makeText(getBaseContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if (Integer.parseInt(suworder3.StatusFlag) >= 2) {
                    Toast.makeText(getBaseContext(), "?????? ??? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                String message = "??????????????? ????????????????????????? \n" +
                        "(??????: ????????? ?????? ????????? ?????? ???????????????.)";

                new android.app.AlertDialog.Builder(this)
                        .setTitle("???????????? ??????")
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton
                                ("??????", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteStockInCertificate();
                                    }
                                }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;

        }
    }

    private void InsertStockInCertificateImageMulti(ArrayList<Uri> uriList) {

        String url = getString(R.string.service_address) + "insertStockInCertificateImageMulti";
        ContentValues values = new ContentValues();
        values.put("LocationNo", locationNo);
        values.put("SeqNo", seqNo);
        String destination = Integer.toString(spinnerDestination.getSelectedItemPosition() + 1);
        values.put("Destination", destination);

        InsertStockInCertificateImageMulti gsod = new InsertStockInCertificateImageMulti(url, values, uriList);
        gsod.execute();

    }


    public class InsertStockInCertificateImageMulti extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        ArrayList<Uri> list;

        InsertStockInCertificateImageMulti(String url, ContentValues values, ArrayList<Uri> list) {
            this.url = url;
            this.values = values;
            this.list = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request2(url, values, list, ActivityStockInCertificateDetail.this);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {
                //ArrayList<StockInCertificate> stockInCertificateArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityStockInCertificateDetail.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stockInCertificateArrayList.add(stockInCertificate);
                }
                Toast.makeText(ActivityStockInCertificateDetail.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                getStockInCertificateDetail();
            } catch (Exception e) {
                e.printStackTrace();
                progressOFF2(this.getClass().getName());
            } finally {

            }
        }
    }


    private boolean CheckInputData() {
        if (edtCarNo.getText().toString().equals("") || edtCarNo.getText().toString().equals("0")) {
            Toast.makeText(getBaseContext(), "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtBundleQty.getText().toString().equals("") || edtBundleQty.getText().toString().equals("0")) {
            Toast.makeText(getBaseContext(), "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtFloor.getText().toString().equals("") || edtFloor.getText().toString().equals("0")) {
            Toast.makeText(getBaseContext(), "????????? ???????????????.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void ShowDatePickDialog() {

        new DatePickerDialog(ActivityStockInCertificateDetail.this, mDateSetListener1, mYear, mMonth, mDay).show();
    }

    private void ShowDatePickDialog2() {

        new DatePickerDialog(ActivityStockInCertificateDetail.this, mDateSetListener2, mYear2, mMonth2, mDay2).show();
    }


}

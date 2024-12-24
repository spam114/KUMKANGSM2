package com.kumkangkind.kumkangsm2;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

public class ComplainDialog extends Dialog implements BaseActivityInterface{

    String locationNo="";
    String customer="";
    String location="";//locationName
    String contractNo="";

    ComplainDialog.OnDialogResult mDialogResult;
    ComplainDialog.OnDialogResult mDialogResult2;
    RadioButton radioLeft;
    RadioButton radioRight;

    TextView txtDate;
    TextView txtDateLeft;
    TextView txtDateRight;

    Button btnCancel;
    Button btnOK;
    Context context;

    info.hoang8f.android.segmented.SegmentedGroup segmentedGroup;

    int leftComplainDateArr[];
    int rightComplainDateArr[];


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


    public void setDialogResult(ComplainDialog.OnDialogResult dialogResult) {

        mDialogResult = dialogResult;
    }

    public void setDialogResult2(ComplainDialog.OnDialogResult dialogResult2) {

        mDialogResult2 = dialogResult2;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) getOwnerActivity(), message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }


    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }

    public interface OnDialogResult {
        void finish(int[] dateArr);
    }


    public ComplainDialog(Context context, String locationNo, String customer, String location,int leftComplainDateArr[], int rightComplainDateArr[], String contractNo) {
        super(context);
        this.context=context;

        this.locationNo=locationNo;
        this.customer=customer;
        this.location=location;
        this.leftComplainDateArr=leftComplainDateArr;
        this.rightComplainDateArr=rightComplainDateArr;
        this.contractNo=contractNo;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_complain);

        WindowManager.LayoutParams lp = getWindow().getAttributes( ) ;
        WindowManager wm = ((WindowManager)context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE)) ;
        lp.width =  (int)( wm.getDefaultDisplay().getWidth( ) * 0.9 );
        getWindow().setAttributes( lp ) ;

        radioLeft=findViewById(R.id.toggleLeft);
        radioRight=findViewById(R.id.toggleRight);
        txtDate=findViewById(R.id.txtDate);
        txtDateLeft=findViewById(R.id.txtDateLeft);
        txtDateRight=findViewById(R.id.txtDateRight);
        btnCancel=findViewById(R.id.btnCancel);
        btnOK=findViewById(R.id.btnOK);
        segmentedGroup=findViewById(R.id.segmented);
        txtDateLeft.setText(leftComplainDateArr[0]+"-"+(leftComplainDateArr[1]+1)+"-"+leftComplainDateArr[2]);
        txtDateRight.setText(rightComplainDateArr[0]+"-"+(rightComplainDateArr[1]+1)+"-"+rightComplainDateArr[2]);



        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioLeft.isChecked()){//추가분
                    new GetAddItemToLocationNoAndDate().execute(Users.ServiceAddress+"getAddItemToLocationNoAndDate");
                }
                else if(radioRight.isChecked()){//A/S
                    new GetASItemToLocationNoAndDate().execute(Users.ServiceAddress+"getASItemToLocationNoAndDate");
                }
                else{
                    Toast.makeText(getContext(), "추가분 또는 A/S를 선택하여 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComplainDialog.this.dismiss();
            }
        });

        txtDateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                new DatePickerDialog(getContext(), mDateSetListener1, leftComplainDateArr[0], leftComplainDateArr[1], leftComplainDateArr[2]).show();
                progressOFF();
            }
        });

        txtDateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                new DatePickerDialog(getContext(), mDateSetListener2, rightComplainDateArr[0], rightComplainDateArr[1], rightComplainDateArr[2]).show();
                progressOFF();
            }
        });


        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioLeft.isChecked())
                    txtDate.setText("추가분 요청일");
                else if(radioRight.isChecked()){
                    txtDate.setText("작업일(작업일보의 작업일자)");
                }
            }
        });
    }


    /**
     * A/S정보를 가져온다.
     */
    private class GetASItemToLocationNoAndDate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForASItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String SupervisorCode="";
                String SupervisorName="";

                String SupervisorASNo = "";
                String SupervisorWoNo = "";
                String Dong = "";
                String Ho = "";
                String HoLocation = "";
                String ItemType = "";
                String Item = "";
                String ItemSpecs = "";
                String Quantity = "";
                String Reason = "";
                String AsType = "";
                String Remark = "";
                String Actions = "";
                String ActionEmployee = "";
                String FromDate = "";

                ArrayList<ASItem> asItemArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    SupervisorCode = child.getString("SupervisorCode");
                    SupervisorName = child.getString("SupervisorName");

                    SupervisorASNo = child.getString("SupervisorASNo");
                    SupervisorWoNo = child.getString("SupervisorWoNo");
                    Dong = child.getString("Dong");
                    Ho = child.getString("Ho");
                    HoLocation = child.getString("HoLocation");
                    ItemType = child.getString("ItemType");
                    Item = child.getString("Item");
                    ItemSpecs = child.getString("ItemSpecs");
                    Quantity = child.getString("Quantity");
                    Reason = child.getString("Reason");
                    AsType = child.getString("AsType");
                    Remark = child.getString("Remark");
                    Actions = child.getString("Actions");
                    ActionEmployee = child.getString("ActionEmployee");
                    FromDate = child.getString("FromDate");
                    asItemArrayList.add(new ASItem(SupervisorCode, SupervisorName,SupervisorASNo, SupervisorWoNo, Dong, Ho, HoLocation, ItemType, Item,
                            ItemSpecs, Quantity, Reason, AsType, Remark, Actions, ActionEmployee, FromDate));
                }

                Intent i = new Intent(getContext(), ASItemListActivity.class);

                i.putExtra("asItemArrayList", asItemArrayList);
                i.putExtra("supervisorWoNo", "");
                i.putExtra("customer", customer);
                i.putExtra("location", location);
                i.putExtra("type","현장별");
                i.putExtra("statusFlag","작성");
                i.putExtra("contractNo", contractNo);

                context.startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String PostForASItem(String url) {
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
                jsonObject.put("LocationNo", locationNo);//현장번호
                jsonObject.put("FromDate", txtDateLeft.getText().toString());//from RequestDate
                jsonObject.put("ToDate", txtDateRight.getText().toString());//to RequestDate

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

    /**
     * 추가분 마스터 정보를 가져온다.
     */
    private class GetAddItemToLocationNoAndDate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return postForAddItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String AddItemNo = "";
                String Dong = "";
                String SupervisorName = "";
                String ReceiptEmployeeCode = "";
                String ReceiptEmployeeName = "";
                String RequestDate = "";
                String HoppingDate = "";
                String SupervisorWoNo="";

                ArrayList<AddItem> addItemArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);


                    AddItemNo = child.getString("AddItemNo");
                    Dong = child.getString("Dong");
                    SupervisorName = child.getString("SupervisorName");
                    ReceiptEmployeeCode = child.getString("ReceiptEmployeeCode");
                    ReceiptEmployeeName = child.getString("ReceiptEmployeeName");
                    RequestDate = child.getString("RequestDate");
                    HoppingDate = child.getString("HoppingDate");
                    SupervisorWoNo=child.getString("SupervisorWoNo");

                    AddItem addItem = new AddItem(AddItemNo, Dong, SupervisorName, ReceiptEmployeeCode, ReceiptEmployeeName, RequestDate, HoppingDate, SupervisorWoNo);

                    addItemArrayList.add(addItem);
                }

                Intent i = new Intent(getContext(), AddItemListActivity.class);
                i.putExtra("addItemArrayList", addItemArrayList);
                i.putExtra("customer", customer);
                i.putExtra("location", location);
                i.putExtra("contractNo", "");
                i.putExtra("SupervisorWoNo", "");
                i.putExtra("type", "현장별");
                i.putExtra("statusFlag", "작성");

                context.startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String postForAddItem(String url) {//locationNo, 날짜로 추가분을 가져온다.
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
                jsonObject.put("LocationNo", locationNo);//현장번호
                jsonObject.put("FromDate", txtDateLeft.getText().toString());//from RequestDate
                jsonObject.put("ToDate", txtDateRight.getText().toString());//to RequestDate

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
                    result = "Did not work!";

            } catch (Exception e) {
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
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

    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    leftComplainDateArr[0] = year;
                    leftComplainDateArr[1] = monthOfYear;
                    leftComplainDateArr[2] = dayOfMonth;
                    ComplainDialog.this.mDialogResult.finish(leftComplainDateArr);
                    txtDateLeft.setText(leftComplainDateArr[0]+"-"+(leftComplainDateArr[1]+1)+"-"+leftComplainDateArr[2]);

                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    rightComplainDateArr[0] = year;
                    rightComplainDateArr[1] = monthOfYear;
                    rightComplainDateArr[2] = dayOfMonth;
                    ComplainDialog.this.mDialogResult2.finish(rightComplainDateArr);
                    txtDateRight.setText(rightComplainDateArr[0]+"-"+(rightComplainDateArr[1]+1)+"-"+rightComplainDateArr[2]);
                }
            };
}

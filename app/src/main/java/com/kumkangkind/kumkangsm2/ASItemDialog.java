package com.kumkangkind.kumkangsm2;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ASItemDialog extends Dialog implements BaseActivityInterface{

    OnDialogResult mDialogResult;
    HashMap<Integer, String> typeHashMap;
    HashMap<Integer, String> partNameHashMap;
    HashMap<Integer, String> causeHashMap;
    HashMap<Integer, String> asTypeHashMap;

    ArrayList<String> dongList;
    ArrayList<String> typeArrayList;
    ArrayList<String> partNameArrayList;
    ArrayList<String> causeArrayList;
    ArrayList<String> asTypeArrayList;

    private int mYear;
    private int mMonth;
    private int mDay;

    Spinner spinnerDong;
    EditText edtHo;
    EditText edtLocation;
    Spinner spinnerType;
    Spinner spinnerPartName;
    EditText edtPartSpec;
    EditText edtQty;
    Spinner spinnerCause;
    Spinner spinnerASType;
    EditText edtComplain;
    EditText edtAction;
    EditText edtActionEmployee;
    ScrollView scroll;
    String SeqNo;
    Button btnOK;
    Button btnPicture;
    Button btnCancel;
    boolean editFlag=false;

    String supervisorWoNo;
    String supervisorASNo;
    ASItem tempASItem;

    String supervisorCode;
    String supervisorName;
    String contractNo;

    ArrayList<ComplaintImage> images;
    ComplaintImage image;
    TextView textViewRealDate;


    @Override
    public void onBackPressed() {
        Temp.tempList=new ArrayList<>();
    }

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


    public void setDialogResult(OnDialogResult dialogResult) {

        mDialogResult = dialogResult;
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
                }
            };

    private void ShowDatePickDialog() {
        new DatePickerDialog(getContext(), mDateSetListener1, mYear, mMonth, mDay).show();
        progressOFF();
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
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) getOwnerActivity(), message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }

    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }

    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }


    public interface OnDialogResult {
        void finish(ASItem asItem);
    }



    public ASItemDialog(Context context, String supervisorWoNo, String supervisorCode, String supervisorName, String contractNo) {
        super(context);
        this.supervisorWoNo=supervisorWoNo;
        this.supervisorCode=supervisorCode;
        this.supervisorName=supervisorName;
        this.contractNo= contractNo;
    }

    public ASItemDialog(Context context, String supervisorWoNo, ASItem tempASItem,boolean editFlag, String supervisorCode, String supervisorName, String contractNo) {
        super(context);
        this.supervisorWoNo=supervisorWoNo;
        this.tempASItem=tempASItem;
        this.editFlag=editFlag;
        this.supervisorCode=supervisorCode;
        this.supervisorName=supervisorName;
        this.contractNo=contractNo;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        startProgress();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//소프트키 생성시, 화면 가리는것을 해결한다.

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_asitem);

        Temp.tempList=new ArrayList<>();
        spinnerDong=findViewById(R.id.spinnerDong);
        edtHo=findViewById(R.id.edtHo);
        edtLocation=findViewById(R.id.edtLocation);
        spinnerType=findViewById(R.id.spinnerType);
        spinnerPartName=findViewById(R.id.spinnerPartName);
        edtPartSpec=findViewById(R.id.edtPartSpec);
        edtQty=findViewById(R.id.edtQty);
        spinnerCause=findViewById(R.id.spinnerCause);
        spinnerASType=findViewById(R.id.spinnerASType);
        edtComplain=findViewById(R.id.edtComplain);
        edtAction=findViewById(R.id.edtAction);
        edtActionEmployee=findViewById(R.id.edtActionEmployee);

        scroll=findViewById(R.id.scroll);
        btnOK = findViewById(R.id.btnOK);
        btnPicture=findViewById(R.id.btnPicture);
        btnCancel = findViewById(R.id.btnCancel);
        textViewRealDate = findViewById(R.id.textViewRealDate);

        dongList = new ArrayList<>();
        typeHashMap= new HashMap<>();
        partNameHashMap= new HashMap<>();
        causeHashMap= new HashMap<>();
        asTypeHashMap= new HashMap<>();

        edtPartSpec.setPrivateImeOptions("defaultInputmode=english;");
        edtPartSpec.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        SetDate();
        textViewRealDate.setText(mYear+"-"+(mMonth+1)+"-"+mDay);
        textViewRealDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatePickDialog();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editFlag==false){//등록
                    new SetASItemByPost().execute(getContext().getString(R.string.service_address)+"setASItem");// onPost에 사진등록 첨부
                }
                else{//수정
                    new UpdateASItemByPost().execute(getContext().getString(R.string.service_address)+"updateASItem");// onPost에 사진등록 첨부
                }
            }
        });

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진관리
                images= new ArrayList<>();
                if(tempASItem==null){// 기존에 AS아이템 없이 추가하면서, 이미지도 같이 올릴때

                    Intent i = new Intent(getContext(), ComplaintImageActivity.class);
                    i.putExtra("ItemNo", "X");//아이템 번호 = AS번호
                    i.putExtra("data", images);//이미지정보
                    i.putExtra("type", "2");//추가분 이냐 AS냐 구분 2번: AS

                    getContext().startActivity(i);
                }
                else{
                    new GetComplaintImageList().execute(getContext().getString(R.string.service_address)+"getComplaintImageList");
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Temp.tempList=new ArrayList<>();
                ASItemDialog.this.dismiss();
            }
        });

        /**
         * 수정시
         */
      /*  if(editFlag==true){//수정 시, 칸을 채운다.
            edtHo.setText(tempAddItemDetail.Ho);
            edtLocation.setText(tempAddItemDetail.HoLocation);
            edtMspec.setText(tempAddItemDetail.MSpec);
            edtQty.setText(tempAddItemDetail.Qty);
            edtRemark.setText(tempAddItemDetail.Remark);
        }*/

        new GetASItemStandard().execute(getContext().getString(R.string.service_address)+"getASItemStandard");//AS 기준정보를 가져온다.-> (onPost에 구현)동정보를 가져온다.
    }

    private class GetComplaintImageList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return postForComplaintImageList(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                image= new ComplaintImage();


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);


                    image.ItemNo = child.getString("ItemNo");
                    image.No = child.getString("No");
                    image.Type = child.getString("Type");
                    image.ImageName = child.getString("ImageName");
                    image.ImageFile = "";
                    image.SmallImageFile = child.getString("ImageFile");

                    images.add(new ComplaintImage(image.ItemNo, image.No, image.Type, image.ImageName, image.ImageFile, image.SmallImageFile));
                }



                Intent i = new Intent(getContext(), ComplaintImageActivity.class);
                i.putExtra("ItemNo", tempASItem.SupervisorASNo);//이미지정보
                i.putExtra("data", images);//이미지정보
                i.putExtra("type", "2");//추가분 이냐 AS냐 구분 2번: AS
                getContext().startActivity(i);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String postForComplaintImageList(String url) {
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
                jsonObject.put("ItemNo", tempASItem.SupervisorASNo);//이미지 번호

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

    private class GetDongBySupervisor extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForGetDong(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                dongList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    dongList.add(child.getString("Dong"));
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        dongList);

                spinnerDong.setAdapter(adapter);
                spinnerDong.setSelection(0);


                if(editFlag==true){//수정일시, 변경
                    int dongNum=-1;
                    int typeNum=-1;
                    int partNameNum=-1;
                    int causeNum=-1;
                    int asTypeNum=-1;

                    for(int i=0;i<dongList.size();i++){//동 index 찾기
                        if(tempASItem.Dong.equals(dongList.get(i))){
                            dongNum=i;
                        }
                    }


                    for(int i=0;i<typeArrayList.size();i++){//동 index 찾기
                        if(tempASItem.ItemType.equals(typeArrayList.get(i))){
                            typeNum=i;
                        }
                    }

                    for(int i=0;i<partNameArrayList.size();i++){//동 index 찾기
                        if(tempASItem.Item.equals(partNameArrayList.get(i))){
                            partNameNum=i;
                        }
                    }

                    for(int i=0;i<causeArrayList.size();i++){//동 index 찾기
                        if(tempASItem.Reason.equals(causeArrayList.get(i))){
                            causeNum=i;
                        }
                    }

                    for(int i=0;i<asTypeArrayList.size();i++){//동 index 찾기
                        if(tempASItem.AsType.equals(asTypeArrayList.get(i))){
                            asTypeNum=i;
                        }
                    }

                    spinnerDong.setSelection(dongNum);
                    edtHo.setText(tempASItem.Ho);
                    edtLocation.setText(tempASItem.HoLocation);
                    spinnerType.setSelection(typeNum);
                    spinnerPartName.setSelection(partNameNum);
                    edtPartSpec.setText(tempASItem.ItemSpecs);
                    edtQty.setText(tempASItem.Quantity);
                    spinnerCause.setSelection(causeNum);
                    spinnerASType.setSelection(asTypeNum);
                    edtComplain.setText(tempASItem.Remark);
                    edtAction.setText(tempASItem.Actions);
                    edtActionEmployee.setText(tempASItem.ActionEmployee);
                }

            } catch (Exception e) {
                Toast.makeText(getContext(), "동정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                e.printStackTrace();
            }
        }

        public String PostForGetDong(String url) {
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
                jsonObject.put("SupervisorWoNo", supervisorWoNo);//작업일보번호
                jsonObject.put("ContractNo", contractNo);//작업일보번호

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
                Toast.makeText(getContext(), "동정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
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
               // message = jsonArray.getJSONObject(0).getString("Message");
              //  resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            } catch (Exception ex) {
                Toast.makeText(getContext(), "동정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            inputStream.close();
            return result;
        }

    }



    private class GetASItemStandard extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForGetASItemStandard(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    String StandardNo="";
                    String Name="";
                    String Parent="";

                    StandardNo=child.getString("StandardNo");
                    Name=child.getString("Name");
                    Parent=child.getString("Parent");

                    if(Integer.parseInt(Parent)==1){
                        typeHashMap.put(Integer.parseInt(StandardNo),Name);
                    }
                    else if(Integer.parseInt(Parent)==2){
                        partNameHashMap.put(Integer.parseInt(StandardNo),Name);
                    }
                    else if(Integer.parseInt(Parent)==3){
                        causeHashMap.put(Integer.parseInt(StandardNo),Name);
                    }
                    else if(Integer.parseInt(Parent)==4){
                        asTypeHashMap.put(Integer.parseInt(StandardNo),Name);
                    }
                }

               typeArrayList = new ArrayList<>(typeHashMap.values());
               partNameArrayList = new ArrayList<>(partNameHashMap.values());
               causeArrayList = new ArrayList<>(causeHashMap.values());
               asTypeArrayList = new ArrayList<>(asTypeHashMap.values());


                ArrayAdapter adapter1 = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        typeArrayList);
                spinnerType.setAdapter(adapter1);
                spinnerType.setSelection(0);

                ArrayAdapter adapter2 = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        partNameArrayList);
                spinnerPartName.setAdapter(adapter2);
                spinnerPartName.setSelection(0);

                ArrayAdapter adapter3 = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        causeArrayList);
                spinnerCause.setAdapter(adapter3);
                spinnerCause.setSelection(0);

                ArrayAdapter adapter4 = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        asTypeArrayList);
                spinnerASType.setAdapter(adapter4);
                spinnerASType.setSelection(0);

            } catch (Exception e) {
                Toast.makeText(getContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                e.printStackTrace();
            }
            new GetDongBySupervisor().execute(getContext().getString(R.string.service_address)+"getDongBySupervisorWoNo");//동정보를 가져온다.-> (onPost에 구현)'수정'일시 셋팅
            progressOFF();
        }

        public String PostForGetASItemStandard(String url) {
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
                Toast.makeText(getContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
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
               // message = jsonArray.getJSONObject(0).getString("Message");
              //  resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            } catch (Exception ex) {
                Toast.makeText(getContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            inputStream.close();
            return result;
        }

    }


    private class SetASItemByPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForSetASItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String ResultCode="";
                String Message="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message= child.getString("Message");


                }

                if(ResultCode.equals("false")) {
                    Toast.makeText(getContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    Temp.tempList=new ArrayList<>();
                    return;
                }
                else {//저장성공
                    Toast.makeText(getContext(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    ASItem asItem= new ASItem(supervisorCode,supervisorName,supervisorASNo,supervisorWoNo,spinnerDong.getSelectedItem().toString(), edtHo.getText().toString(), edtLocation.getText().toString(), spinnerType.getSelectedItem().toString(),
                            spinnerPartName.getSelectedItem().toString(), edtPartSpec.getText().toString(), edtQty.getText().toString(), spinnerCause.getSelectedItem().toString(), spinnerASType.getSelectedItem().toString(),
                            edtComplain.getText().toString(), edtAction.getText().toString(), edtActionEmployee.getText().toString());

                    ASItemDialog.this.mDialogResult.finish(asItem);

                    for(int i=0;i<Temp.tempList.size();i++){
                        new SetComplaintImageWhenInit(i).execute(getContext().getString(R.string.service_address)+"setComplaintImageWhenInit");
                    }

                    ASItemDialog.this.dismiss();
                }

            } catch (Exception e) {
                Temp.tempList=new ArrayList<>();
                e.printStackTrace();
            }
        }

        public String PostForSetASItem(String url) {
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

                //SupervisorASNo 는 C#쪽에서 생성

                int ItemType=-1;
                int Item=-1;
                int Reason=-1;
                int ASType=-1;

                String qty;//수량

                for (Map.Entry<Integer,String> haEntry:typeHashMap.entrySet()) {
                    if(spinnerType.getSelectedItem().toString().equals(haEntry.getValue()))
                        ItemType=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:partNameHashMap.entrySet()) {
                    if(spinnerPartName.getSelectedItem().toString().equals(haEntry.getValue()))
                        Item=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:causeHashMap.entrySet()) {
                    if(spinnerCause.getSelectedItem().toString().equals(haEntry.getValue()))
                        Reason=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:asTypeHashMap.entrySet()) {
                    if(spinnerASType.getSelectedItem().toString().equals(haEntry.getValue()))
                        ASType=haEntry.getKey();
                }

                qty=edtQty.getText().toString();

                if(edtQty.getText().toString().equals("")){//빈칸이면 0처리
                    qty="0";
                }

                jsonObject.put("SupervisorWoNo", supervisorWoNo);
                jsonObject.put("SupervisorCode", Users.USER_ID);
                jsonObject.put("Dong", spinnerDong.getSelectedItem().toString());
                jsonObject.put("Ho", edtHo.getText().toString());
                jsonObject.put("HoLocation", edtLocation.getText().toString());
                jsonObject.put("ItemType", ItemType);
                jsonObject.put("Item", Item);
                jsonObject.put("ItemSpecs", edtPartSpec.getText().toString());
                jsonObject.put("Quantity", qty);
                jsonObject.put("Reason", Reason);
                jsonObject.put("AsType", ASType);
                jsonObject.put("Remark", edtComplain.getText().toString());
                jsonObject.put("Actions", edtAction.getText().toString());
                jsonObject.put("ActionEmployee", edtActionEmployee.getText().toString());
                jsonObject.put("ContractNo", contractNo);
                jsonObject.put("FromDate", textViewRealDate.getText().toString());

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
                Temp.tempList=new ArrayList<>();
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
                message = jsonArray.getJSONObject(0).getString("Message");
                resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

                if(resultCode.equals("true"))
                    supervisorASNo=message;

            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

    }



    private class UpdateASItemByPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForUpdateASItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String ResultCode="";
                String Message="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message= child.getString("Message");
                }

                if(ResultCode.equals("false")) {
                    Toast.makeText(getContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    Temp.tempList=new ArrayList<>();
                    return;
                }
                else {//저장성공
                    Toast.makeText(getContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    ASItem asItem= new ASItem(supervisorCode,supervisorName,supervisorASNo,supervisorWoNo,spinnerDong.getSelectedItem().toString(), edtHo.getText().toString(), edtLocation.getText().toString(), spinnerType.getSelectedItem().toString(),
                            spinnerPartName.getSelectedItem().toString(), edtPartSpec.getText().toString(), edtQty.getText().toString(), spinnerCause.getSelectedItem().toString(), spinnerASType.getSelectedItem().toString(),
                            edtComplain.getText().toString(), edtAction.getText().toString(), edtActionEmployee.getText().toString());

                    ASItemDialog.this.mDialogResult.finish(asItem);
                    ASItemDialog.this.dismiss();
                }

            } catch (Exception e) {
                Temp.tempList=new ArrayList<>();
                e.printStackTrace();
            }
        }


        public String PostForUpdateASItem(String url) {
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

                //SupervisorASNo 는 C#쪽에서 생성

                int ItemType=-1;
                int Item=-1;
                int Reason=-1;
                int ASType=-1;

                String qty;//수량

                for (Map.Entry<Integer,String> haEntry:typeHashMap.entrySet()) {
                    if(spinnerType.getSelectedItem().toString().equals(haEntry.getValue()))
                        ItemType=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:partNameHashMap.entrySet()) {
                    if(spinnerPartName.getSelectedItem().toString().equals(haEntry.getValue()))
                        Item=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:causeHashMap.entrySet()) {
                    if(spinnerCause.getSelectedItem().toString().equals(haEntry.getValue()))
                        Reason=haEntry.getKey();
                }

                for (Map.Entry<Integer,String> haEntry:asTypeHashMap.entrySet()) {
                    if(spinnerASType.getSelectedItem().toString().equals(haEntry.getValue()))
                        ASType=haEntry.getKey();
                }

                qty=edtQty.getText().toString();

                if(edtQty.getText().toString().equals("")){//빈칸이면 0처리
                    qty="0";
                }

                jsonObject.put("SupervisorASNo", tempASItem.SupervisorASNo);
                jsonObject.put("SupervisorWoNo", supervisorWoNo);
                jsonObject.put("Dong", spinnerDong.getSelectedItem().toString());
                jsonObject.put("Ho", edtHo.getText().toString());
                jsonObject.put("HoLocation", edtLocation.getText().toString());
                jsonObject.put("ItemType", ItemType);
                jsonObject.put("Item", Item);
                jsonObject.put("ItemSpecs", edtPartSpec.getText().toString());
                jsonObject.put("Quantity", qty);
                jsonObject.put("Reason", Reason);
                jsonObject.put("AsType", ASType);
                jsonObject.put("Remark", edtComplain.getText().toString());
                jsonObject.put("Actions", edtAction.getText().toString());
                jsonObject.put("ActionEmployee", edtActionEmployee.getText().toString());
                jsonObject.put("SupervisorCode", Users.USER_ID);
                jsonObject.put("FromDate", textViewRealDate.getText().toString());

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
                Temp.tempList=new ArrayList<>();
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
                message = jsonArray.getJSONObject(0).getString("Message");
                resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

                if(resultCode.equals("true"))
                    supervisorASNo=message;

            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

    }

    /**
     * A/S, 추가분 등록시, 이미지도 함께 등록할때 쓰인다.
     */
    private class SetComplaintImageWhenInit extends AsyncTask<String, Void, String> {
        int index;//이미지 리스트의 인덱스

        public SetComplaintImageWhenInit(int index){
            this.index=index;
        }

        @Override
        protected String doInBackground(String... urls) {

            return PostForSetComplaintImageWhenInit(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String ResultCode="";
                String Message="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message= child.getString("Message");


                }

                if(ResultCode.equals("false")) {
                    //Log.i("사진리스트 저장실패: ", Integer.toString(Temp.tempList.size()));
                    Toast.makeText(getContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_LONG).show();
                    Temp.tempList=new ArrayList<>();
                    return;
                }
                else {//저장성공
                   // Log.i("사진리스트 저장완료: ", Integer.toString(Temp.tempList.size()));
                    if(index==Temp.tempList.size()-1) {
                        Temp.tempList = new ArrayList<>();
                        //Log.i("사진리스트 저장전체완료: ", Integer.toString(Temp.tempList.size()));
                    }
                }

            } catch (Exception e) {
                Temp.tempList=new ArrayList<>();
                e.printStackTrace();
            }
        }

        public String PostForSetComplaintImageWhenInit(String url) {
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

                //SupervisorASNo 는 C#쪽에서 생성

                jsonObject.put("ItemNo", supervisorASNo);
                jsonObject.put("No",Temp.tempList.get(index).No);
                jsonObject.put("Type",Temp.tempList.get(index).Type);
                jsonObject.put("ImageName", Temp.tempList.get(index).ImageName);
                jsonObject.put("ImageFile", Temp.tempList.get(index).ImageFile);
                jsonObject.put("SupervisorCode", Users.USER_ID);


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
                Temp.tempList=new ArrayList<>();
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
                message = jsonArray.getJSONObject(0).getString("Message");
                resultCode = jsonArray.getJSONObject(0).getString("ResultCode");


            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

    }


}

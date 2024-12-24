package com.kumkangkind.kumkangsm2;


import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

public class AddItemDetailDialog extends Dialog implements BaseActivityInterface{

    OnDialogResult mDialogResult;

    ArrayList<AddItemStandard> addItemStandardArrayList;

    EditText edtPart;
    String addItemNo;
    EditText edtHo;
    EditText edtLocation;
    EditText edtQty;
    EditText edtRemark;
    ScrollView scroll;
    String SeqNo;
    AddItemDetail tempAddItemDetail;


    Spinner addTypeSpinner;
    Button btnOK;
    Button btnPicture;
    Button btnCancel;
    boolean editFlag=false;

    ArrayList<ComplaintImage> images;
    ComplaintImage image;

    @Override
    public void onBackPressed() {
        Temp.tempList=new ArrayList<>();
    }

    public void setDialogResult(OnDialogResult dialogResult) {

        mDialogResult = dialogResult;
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
    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
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
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    public interface OnDialogResult {
        void finish(AddItemDetail addItemDetail);
    }


    public AddItemDetailDialog(Context context, ArrayList<AddItemStandard> addItemStandardArrayList, String addItemNo) {
        super(context);
        this.addItemNo=addItemNo;
        this.addItemStandardArrayList = addItemStandardArrayList;
    }


    public AddItemDetailDialog(Context context, ArrayList<AddItemStandard> addItemStandardArrayList, String addItemNo, AddItemDetail addItemDetail,boolean editFlag) {
        super(context);
        this.addItemNo=addItemNo;
        this.addItemStandardArrayList = addItemStandardArrayList;
        this.tempAddItemDetail=addItemDetail;
        this.editFlag=editFlag;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//소프트키 생성시, 화면 가리는것을 해결한다.

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_additem_detail);

        Temp.tempList=new ArrayList<>();
        edtPart = findViewById(R.id.edtPart);
        edtHo = findViewById(R.id.edtHo);
        edtLocation = findViewById(R.id.edtLocation);
        edtQty = findViewById(R.id.edtQty);
        edtRemark = findViewById(R.id.edtRemark);

        scroll=findViewById(R.id.scroll);

        btnOK = findViewById(R.id.btnOK);
        btnPicture=findViewById(R.id.btnPicture);
        btnCancel = findViewById(R.id.btnCancel);

        ArrayList<String> addItemStandardList = new ArrayList<>();

        for (AddItemStandard addItemStandard : addItemStandardArrayList) {
            addItemStandardList.add(addItemStandard.Name);
        }
        addTypeSpinner = findViewById(R.id.spinnerAddType);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                addItemStandardList);

        addTypeSpinner.setAdapter(adapter);
        addTypeSpinner.setSelection(0);


        edtPart.setPrivateImeOptions("defaultInputmode=english;");
        edtPart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkInputData()) {
                    Toast.makeText(getContext(), "품명은 반드시 입력하여야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editFlag==false){//등록
                    new SetAddItemDetailByPost().execute(Users.ServiceAddress+"setAddItemDetail");
                }
                else{//수정
                    new UpdateAddItemDetailByPost().execute(Users.ServiceAddress+"updateAddItemDetail");
                }


            }
        });


        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //사진관리
                images= new ArrayList<>();
                if(tempAddItemDetail==null){// 기존에 추가분 순번 없이 추가하면서, 이미지도 같이 올릴때

                    Intent i = new Intent(getContext(), ComplaintImageActivity.class);
                    i.putExtra("ItemNo", "X");//아이템 번호 = AS번호
                    i.putExtra("data", images);//이미지정보
                    i.putExtra("type", "1");//추가분 이냐 AS냐 구분 1번: 추가분

                    getContext().startActivity(i);


                }
                else{
                    new GetComplaintImageList().execute(Users.ServiceAddress+"getComplaintImageList");
                }


            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Temp.tempList=new ArrayList<>();
                AddItemDetailDialog.this.dismiss();
            }
        });


        /**
         * 수정시
         */
        if(editFlag==true){//수정 시, 칸을 채운다.
            int addType=-1;
            for (AddItemStandard addItemStandard:addItemStandardArrayList) {
                if(tempAddItemDetail.AddType.equals(addItemStandard.Name))
                    addType=Integer.parseInt(addItemStandard.StandardNo);
            }
            edtHo.setText(tempAddItemDetail.Ho);
            edtLocation.setText(tempAddItemDetail.HoLocation);
            addTypeSpinner.setSelection(addType-1);
            edtPart.setText(tempAddItemDetail.Part);
            edtQty.setText(tempAddItemDetail.Qty);
            edtRemark.setText(tempAddItemDetail.Remark);
        }


    }

    private boolean checkInputData(){
        if(edtPart.getText().toString().equals(""))
            return false;
        return true;
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
                i.putExtra("ItemNo", addItemNo+"-"+tempAddItemDetail.SeqNo);//아이템번호
                i.putExtra("data", images);//이미지정보
                i.putExtra("type", "1");//추가분 이냐 AS냐 구분 2번: AS
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
                jsonObject.put("ItemNo", addItemNo+"-"+tempAddItemDetail.SeqNo);//이미지 번호

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


    private class SetAddItemDetailByPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForSetAddItemDetail(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getContext(), "등록 되었습니다.", Toast.LENGTH_SHORT).show();

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ResultCode="true";
                String Message="";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                }

                if(ResultCode.equals("false")) {
                    Toast.makeText(getContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    Temp.tempList=new ArrayList<>();
                    return;
                }
                else {//저장성공
                    Toast.makeText(getContext(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    if(edtQty.getText().toString().equals(""))
                        edtQty.setText("0");


                    AddItemDetail addItemDetail= new AddItemDetail(addItemNo,SeqNo,edtHo.getText().toString(),
                            edtLocation.getText().toString(),addTypeSpinner.getSelectedItem().toString(),
                            edtQty.getText().toString(),edtRemark.getText().toString(),edtPart.getText().toString());

                    AddItemDetailDialog.this.mDialogResult.finish(addItemDetail);
                    AddItemDetailDialog.this.dismiss();

                    for(int i=0;i<Temp.tempList.size();i++){
                        new SetComplaintImageWhenInit(i).execute(Users.ServiceAddress+"setComplaintImageWhenInit");
                    }

                }

            } catch (JSONException e) {
                Temp.tempList=new ArrayList<>();
                e.printStackTrace();
            }

        }
    }

    public String PostForSetAddItemDetail(String url) {
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

            int addType=-1;
            for (AddItemStandard addItemStandard:addItemStandardArrayList) {
                if(addTypeSpinner.getSelectedItem().toString().equals(addItemStandard.Name))
                    addType=Integer.parseInt(addItemStandard.StandardNo);
            }

            String Qty=edtQty.getText().toString();
            if(Qty.equals(""))
                Qty="0";

            //Delete & Insert
            jsonObject.put("AddItemNo", addItemNo);//추가분번호
            jsonObject.put("Ho", edtHo.getText().toString());//세대
            jsonObject.put("HoLocation", edtLocation.getText().toString());//위치
            jsonObject.put("AddType", addType);//추가분구분
            jsonObject.put("Qty", Qty);//수량
            jsonObject.put("Remark", edtRemark.getText().toString());//현상
            jsonObject.put("Part", edtPart.getText().toString());//추가분구분
            jsonObject.put("SupervisorCode", Users.USER_ID);//슈퍼바이저코드

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
            //Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        //Log.i("result", result.toString());
        return result;
    }

    public String PostForUpdateAddItemDetail(String url) {
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

            int addType=-1;
            for (AddItemStandard addItemStandard:addItemStandardArrayList) {
                if(addTypeSpinner.getSelectedItem().toString().equals(addItemStandard.Name))
                    addType=Integer.parseInt(addItemStandard.StandardNo);
            }

            String Qty=edtQty.getText().toString();
            if(Qty.equals(""))
                Qty="0";

            //Delete & Insert
            jsonObject.put("AddItemNo", addItemNo);//추가분번호
            jsonObject.put("SeqNo", tempAddItemDetail.SeqNo);//추가분번호
            jsonObject.put("Ho", edtHo.getText().toString());//세대
            jsonObject.put("HoLocation", edtLocation.getText().toString());//위치
            jsonObject.put("AddType", addType);//추가분구분
            jsonObject.put("Qty", Qty);//수량
            jsonObject.put("Remark", edtRemark.getText().toString());//현상
            jsonObject.put("Part", edtPart.getText().toString());//현상
            jsonObject.put("SupervisorCode", Users.USER_ID);//슈퍼바이저코드

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
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");


            if(resultCode.equals("true") && editFlag==false)
                SeqNo=message;

        } catch (Exception ex) {

        }

        inputStream.close();
        return result;
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


                jsonObject.put("ItemNo", addItemNo+"-"+SeqNo);
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


    private class UpdateAddItemDetailByPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForUpdateAddItemDetail(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getContext(), "등록 되었습니다.", Toast.LENGTH_SHORT).show();

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ResultCode="true";
                String Message="";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                }

                if(ResultCode.equals("false")) {
                    Toast.makeText(getContext(), "저장에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    Temp.tempList=new ArrayList<>();
                    return;
                }
                else {//저장성공
                    Toast.makeText(getContext(), "세부사항 갱신이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    AddItemDetail addItemDetail= new AddItemDetail(addItemNo,tempAddItemDetail.SeqNo,edtHo.getText().toString(),
                            edtLocation.getText().toString(),addTypeSpinner.getSelectedItem().toString(),edtQty.getText().toString(),edtRemark.getText().toString()
                            ,edtPart.getText().toString());

                    AddItemDetailDialog.this.mDialogResult.finish(addItemDetail);
                    AddItemDetailDialog.this.dismiss();


                }

            } catch (JSONException e) {
                Temp.tempList=new ArrayList<>();
                e.printStackTrace();
            }





        }
    }
}
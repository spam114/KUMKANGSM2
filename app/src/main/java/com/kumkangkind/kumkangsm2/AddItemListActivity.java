package com.kumkangkind.kumkangsm2;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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


public class AddItemListActivity extends BaseActivity {//추가분 리스트 액티비티

    private final int REQUEST_ADD_BUTTON = 1;
    private final int REQUEST_DETAIL_BUTTON = 2;
    private ListView listView1;
    TextView textCustomer;
    TextView textLocation;
    TextView textViewDelete;
    ArrayList<AddItem> addItemArrayList;
    AddItemAdapter adapter;
    String key = "";
    ProgressDialog mProgressDialog;
    AddItem currentItem = null;
    Dialog dialog;
    String customer = "";
    String location = "";
    Button addButton;
    ArrayList<String> dongList;
    String supervisorWoNo;
    String contractNo;
    String addItemNo = "";
    String type;//수정용 확인
    String statusFlag;


    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 5000);
        progressON("Loading...", handler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_additem);

        addItemArrayList = (ArrayList<AddItem>) getIntent().getSerializableExtra("addItemArrayList");
        customer = getIntent().getStringExtra("customer");
        location = getIntent().getStringExtra("location");
        contractNo = getIntent().getStringExtra("contractNo");
        supervisorWoNo = getIntent().getStringExtra("SupervisorWoNo");

        textCustomer = (TextView) findViewById(R.id.textViewCustomer);
        textLocation = (TextView) findViewById(R.id.textViewLocation);

        textCustomer.setText(customer);
        textLocation.setText(location);
        textViewDelete=findViewById(R.id.textViewDelete);

        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setText("+ 추가분\n 리스트");


        type=getIntent().getStringExtra("type");
        statusFlag=getIntent().getStringExtra("statusFlag");

        if(type.equals("확인") || statusFlag.equals("2")){
            addButton.setText("추가분관리");
            addButton.setPadding(0,0,0,0);
            addButton.setClickable(false);
            textViewDelete.setText("");
        }

        if(type.equals("현장별")){
            addButton.setVisibility(View.GONE);
        }

        adapter = new AddItemAdapter(this, R.layout.listview_additem_row, addItemArrayList,type, statusFlag);

        //ListView
        listView1 = (ListView) findViewById(R.id.listViewImage);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);
        listView1.setFocusable(false);

        new GetDongBySupervisor().execute(getString(R.string.service_address)+"getDongBySupervisorWoNo");//현장별 동정보를 가져온다


        progressOFF();
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

                if(type.equals("현장별")) {
                    if(!Users.UserName.equals(currentItem.SupervisorName))//나의 추가분이 아니면 확인용으로 수정이 불가능하게 설정
                        type="확인";//=수정용
                    new GetAddItemDetail().execute(getString(R.string.service_address)+"getAddItemDetail");
                }
                progressOFF();

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void mOnClick(View v) {

        startProgress();

        switch (v.getId()) {

            case R.id.btnAdd: //추가버튼 클릭
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_confirm_additem, null);
                AlertDialog.Builder buider = new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder 객체 생성
                //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)


                final Spinner dongSpinner = dialogView.findViewById(R.id.dongSpinner);

                final ArrayAdapter adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dongList);

                dongSpinner.setAdapter(adapter);
                dongSpinner.setSelection(0);

                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        currentItem = null;
                        new SetAddItemByPost(dongSpinner.getSelectedItem().toString()).execute(getString(R.string.service_address)+"setAddItem");
                    }
                });

                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressOFF();
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();
                break;
        }
    }


    /**
     * 추가분 선택및 디테일 정보 가져오기
     */
    private class GetAddItemDetail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForAddItemDetail(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                ArrayList<AddItemDetail> addItemDetailArrayList = new ArrayList<>();
                AddItemDetail detail;

                JSONArray jsonArray = new JSONArray(result);

                String AddItemNo = "";
                String SeqNo = "";
                String Ho = "";
                String HoLocation = "";
                String AddType = "";
                String Qty = "";
                String Remark = "";
                String Part="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    AddItemNo = child.getString("AddItemNo");
                    SeqNo = child.getString("SeqNo");
                    Ho = child.getString("Ho");
                    HoLocation = child.getString("HoLocation");
                    AddType = child.getString("AddType");
                    Qty = child.getString("Qty");
                    Remark = child.getString("Remark");
                    Part= child.getString("Part");

                    detail = new AddItemDetail(AddItemNo, SeqNo, Ho, HoLocation, AddType, Qty, Remark, Part);
                    addItemDetailArrayList.add(detail);
                }


                HashMap<String, String> addMasterData = new HashMap<>();
                addMasterData.put("dong", currentItem.Dong);
                addMasterData.put("employeeCode", currentItem.ReceiptEmployeeCode);
                addMasterData.put("employeeName", currentItem.ReceiptEmployeeName);
                addMasterData.put("requestDate", currentItem.RequestDate);
                addMasterData.put("hoppingDate", currentItem.HoppingDate);

                Intent i = new Intent(getBaseContext(), AddItemDetailListActivity.class);

                i.putExtra("supervisorWoNo", supervisorWoNo);
                i.putExtra("customer", customer);
                i.putExtra("location", location);
                i.putExtra("dongList", dongList);
                i.putExtra("addItemNo", currentItem.AddItemNo);
                i.putExtra("addItemDetailArrayList", addItemDetailArrayList);
                i.putExtra("addMasterData", addMasterData);
                i.putExtra("type", type);
                i.putExtra("statusFlag", statusFlag);
                startActivityForResult(i, REQUEST_DETAIL_BUTTON);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 신규 추가분 리스트 작성
     */
    private class SetAddItemByPost extends AsyncTask<String, Void, String> {

        String dong;

        public SetAddItemByPost(String dong) {
            this.dong = dong;
        }


        @Override
        protected String doInBackground(String... urls) {

            return PostForSetAddItem(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                Calendar cal = new GregorianCalendar();
                int currentYear= cal.get(Calendar.YEAR);
                int currentMonth = cal.get(Calendar.MONTH);
                int currentDay = cal.get(Calendar.DATE);

                ArrayList<AddItemDetail> addItemDetailArrayList= new ArrayList<>();
                HashMap<String,String> addMasterData= new HashMap<>();
                addMasterData.put("dong",dong);
                addMasterData.put("employeeCode","");
                addMasterData.put("employeeName","");
                addMasterData.put("requestDate",currentYear+"-"+(currentMonth+1)+"-"+currentDay);//새 추가분 작성시에는 요청일을 현재 날짜로 설
                addMasterData.put("hoppingDate","");

                Intent i = new Intent(getBaseContext(), AddItemDetailListActivity.class);

                i.putExtra("supervisorWoNo", supervisorWoNo);
                i.putExtra("customer", customer);
                i.putExtra("location", location);
                i.putExtra("dongList",dongList);
                i.putExtra("addItemNo",addItemNo);
                i.putExtra("addItemDetailArrayList",addItemDetailArrayList);
                i.putExtra("addMasterData",addMasterData);
                i.putExtra("statusFlag",statusFlag);
                i.putExtra("type",type);
                startActivityForResult(i,REQUEST_ADD_BUTTON);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 신규 추가분을 작성 한다.
         */
        public String PostForSetAddItem(String url) {
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
                jsonObject.put("ContractNo", contractNo);
                jsonObject.put("Dong", dong);
                jsonObject.put("SupervisorCode", Users.USER_ID);
                jsonObject.put("SupervisorWoNo", supervisorWoNo);


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

    }

    /**
     * 기존 추가분 가져오기
     */
    public String PostForAddItemDetail(String url) {
        InputStream inputStream = null;
        String result = "";
        String AddItemNo = "";
        if (currentItem != null) {
            AddItemNo = currentItem.AddItemNo;
        }

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            //Delete & Insert
            jsonObject.put("AddItemNo", AddItemNo);

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


    private String convertInputStreamToString(InputStream inputStream) throws IOException {//문제없을시, 추가분 번호를 return 한다.
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
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            message = jsonArray.getJSONObject(0).getString("Message");
            if(resultCode.equals("true")){//추가분 리스트를 새로 생성하고, 문제가 없을시 추가분 번호를 넣어준다.
                addItemNo=message;
            }

        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //클릭했을 때 동작
            currentItem = addItemArrayList.get(position);
            startProgress();

            if(type.equals("현장별")){//현장별(=현장불만사례)이면 클릭했을때 동리스트를 가져온후-> 디테일 가져온다.
                supervisorWoNo=currentItem.SupervisorWoNo;
                new GetDongBySupervisor().execute(getString(R.string.service_address)+"getDongBySupervisorWoNo");//현장별 동정보 (다시)를 가져온다-> 다음 디테일 가져오기
            }
            else{//나머지는 onCreate 에서 이미 동리스트 가져옴: 디테일만 가져오면됨
                new GetAddItemDetail().execute(getString(R.string.service_address)+"getAddItemDetail");
            }
        }
    };

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

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_BUTTON://추가버튼 클릭후 반환값
                    AddItem addItem=(AddItem)data.getSerializableExtra("addItem");
                    addItemArrayList.add(addItem);
                    adapter.notifyDataSetChanged();
                    type= getIntent().getStringExtra("type");
                    break;

                case REQUEST_DETAIL_BUTTON://추가분 세부항목 조회후 반환값
                    AddItem AddItem2=(AddItem)data.getSerializableExtra("addItem");
                    int num=-1;
                    for (AddItem AddItem:addItemArrayList) {
                        if(AddItem.AddItemNo.equals(AddItem2.AddItemNo)){
                            AddItem.Dong=AddItem2.Dong;
                            AddItem.RequestDate=AddItem2.RequestDate;
                            AddItem.HoppingDate=AddItem2.HoppingDate;
                            AddItem.ReceiptEmployeeCode=AddItem2.ReceiptEmployeeCode;
                            AddItem.ReceiptEmployeeName=AddItem2.ReceiptEmployeeName;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    type= getIntent().getStringExtra("type");
                    break;
            }
            //반환후 변경되야할 화면 구성


        }


    }


}

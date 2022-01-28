package com.kumkangkind.kumkangsm2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.kumkangkind.kumkangsm2.ProgressFloor.ProgressFloorViewAdapter;
import com.kumkangkind.kumkangsm2.ProgressFloor.YearMonthPickerDialog;

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
import java.util.HashMap;
import java.util.TreeMap;


public class ProgressFloorActivity extends BaseActivity {

    private ListView listView1;
    //private SuWorder[] items;

    TextView tvCustomerLocation;
    String contractNo = "";
    String type = "";
    String restURL = "";
    String arrayName = "";
    String customerLocation = "";
    public HashMap<String, Dong> dongHashMap;
    TreeMap<String, Dong> dongTreeMap;
    ProgressFloorViewAdapter adapter;
    ArrayList<Dong> dongArrayList;

    String dong = "";
    String yearMonth = "";
    String floor = "";
    String inputYearMonth = "";
    String inputFloor = "";
    int clickPosition=-1;//다이얼로그 종료시, 복귀 포지션을 위함
    View clickView=null;
    long clickId=0;

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_floor_listview);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);
        contractNo = getIntent().getStringExtra("contractNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        tvCustomerLocation.setText(customerLocation);
        dongHashMap = (HashMap<String, Dong>) (getIntent().getSerializableExtra("dongHashMap"));//todo

        dongTreeMap = new TreeMap<>(dongHashMap);
       // Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
       // Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

        listView1 = (ListView) findViewById(R.id.listView1);
        dongArrayList = new ArrayList<>();

        for (Dong dong : dongTreeMap.values()) {
            dongArrayList.add(dong);
        }

        adapter = new ProgressFloorViewAdapter(ProgressFloorActivity.this, R.layout.progress_floor_row, dongArrayList);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);

        progressOFF();

    }

    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnDelete://삭제
                ShowDeleteDialog();
                break;


            case R.id.btnAdd://추가
                ShowAddDialog();
                break;
        }
    }

    private void ShowDeleteDialog() {


        if (clickPosition==-1) {
            Toast.makeText(getApplicationContext(), "동을 선택한 후, 진행하시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dongArrayList.get(clickPosition).ConstructionEmployee.equals(Users.UserName)) {
            Toast.makeText(getApplicationContext(), "본인의 진행정보만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (yearMonth.equals("") || floor.equals("")) {
            Toast.makeText(getApplicationContext(), "삭제 할 진행정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }





        new AlertDialog.Builder(ProgressFloorActivity.this)


                .setTitle("진행 월/층 삭제")
                .setMessage("'" + yearMonth.substring(0, 4) + "년 " + yearMonth.substring(4, 6) + "월, 진행층수: " + floor + "층' 의 내역을 삭제하시겠습니까?")
                //.setIcon(R.drawable.ninja)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mHandler2.sendEmptyMessage(1);
                            }
                        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mHandler2.sendEmptyMessage(0);

                    }
                }).show();

    }


    private void ShowAddDialog() {

        if (dong.equals("")) {
            Toast.makeText(getApplicationContext(), "동을 선택한 후, 진행하시기 바랍니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dongArrayList.get(clickPosition).ConstructionEmployee.equals(Users.UserName)) {
            Toast.makeText(getApplicationContext(), "본인의 진행정보만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        inputYearMonth="";
        inputFloor="";
        YearMonthPickerDialog pd = new YearMonthPickerDialog(mHandler, contractNo, dong, yearMonth, floor);
        pd.setListener(d);
        pd.show(getFragmentManager(), "YearMonthPickerTest");

    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String mes = items[position -1].WorkNo;
            //이동한다
            //String key = items[position - 1].WorkNo;
            dong = dongArrayList.get(position).Dong;

            yearMonth = dongArrayList.get(position).ProgressYearMonth;//입력 년월로 수정
            floor = dongArrayList.get(position).ProgressFloor;//입력 층으로 수정

            clickPosition=position;
            clickView=view;
            clickId=id;

            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 /*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*/

        }
    };


    //진행 월/층 추가
    public Handler mHandler = new Handler() { //다이얼로그 종료시 액티비티 데이터 전송을 위함
        @Override
        public void handleMessage(Message msg) {
                inputYearMonth = msg.getData().getString("yearMonth");
                if(inputYearMonth.equals("취소")){
                    Toast.makeText(ProgressFloorActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                inputFloor = msg.getData().getString("floor");
                new SetDongProgressFloorByPost().execute(getString(R.string.service_address)+"setDongProgressFloor");

        }
    };

    private class SetDongProgressFloorByPost extends AsyncTask<String, Void, String> {//todo


        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String ResultCode="";
            String Message="";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message=child.getString("Message");
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(ResultCode.equals("false")){
                Toast.makeText(ProgressFloorActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(ProgressFloorActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            new GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");

        }
    }

    private class GetDongProgessFloorByPost extends AsyncTask<String, Void, String> {//todo
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                HashMap<String, Dong> _dongHashMap= new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                Dong dong=null;
                String key="";
                String Dong ="";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Dong = child.getString("Dong");
                    key=Dong;

                    if(!_dongHashMap.containsKey(key)){//없으면
                        dong = new Dong(child.getString("Dong"),
                                child.getString("ProgressYearMonth"),child.getString("ProgressFloor"), child.getString("ConstructionEmployee"));
                        _dongHashMap.put(key,dong);
                    }
                    else{//있으면: 전 달 데이터 setting
                        dong=_dongHashMap.get(key);
                        dong.ExProgressYearMonth=child.getString("ProgressYearMonth");
                        dong.ExProgressFloor=child.getString("ProgressFloor");
                    }

                }
                dongHashMap=_dongHashMap;




                dongTreeMap = new TreeMap<>(dongHashMap);
            //    Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
             //   Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

                listView1 = (ListView) findViewById(R.id.listView1);
                //View header = (View) getLayoutInflater().inflate(R.layout.progress_floor_header, null);
               // listView1.addHeaderView(header);
                dongArrayList = new ArrayList<>();

                for (Dong _dong : dongTreeMap.values()) {
                    dongArrayList.add(_dong);
                }

                adapter = new ProgressFloorViewAdapter(ProgressFloorActivity.this, R.layout.progress_floor_row, dongArrayList);
                adapter.notifyDataSetChanged();
                listView1.setAdapter(adapter);
                clickPosition=-1;
                //listView1.performItemClick(clickView,clickPosition,clickId);// 다이얼로그, 종료후 클릭이벤트 실행할려는데, 실행이 안됌: check


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


            jsonObject.put("ContractNo", contractNo);//계약번호
            jsonObject.put("Dong", dong);//동
            jsonObject.put("YearMonth", inputYearMonth);//계약번호
            jsonObject.put("Floor", inputFloor);//계약번호

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

       /* String message = "";
        String resultCode = "";
        try {
            //.i("JSON", result);
            JSONArray jsonArray = new JSONArray(result);
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            message = jsonArray.getJSONObject(0).getString("Message");

            if(resultCode.equals("true")){
                Toast.makeText(ProgressFloorActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

        }*/

        inputStream.close();
        return result;
    }


    //진행 월/층 삭제
    public Handler mHandler2 = new Handler() { //다이얼로그 종료시 액티비티에 재반영 작업을 위함
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//확인
                inputYearMonth=yearMonth;
                new DeleteDongProgressFloorByPost().execute(getString(R.string.service_address)+"deleteDongProgressFloor");

            } else {//취소
                Toast.makeText(ProgressFloorActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private class DeleteDongProgressFloorByPost extends AsyncTask<String, Void, String> {//todo


        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            String ResultCode="";
            String Message="";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message=child.getString("Message");
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(ResultCode.equals("false")){
                Toast.makeText(ProgressFloorActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                Toast.makeText(ProgressFloorActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                new GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");
            }




        }
    }




    /**
     * 결과 인텐트를 받아온다.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *//*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            //int seqNo = data.getIntExtra("seqNo", -1);
            //   suWorders.get(seqNo).Supervisor  = data.getStringExtra("SupervisorName");
            //data.getStringExtra("SupervisorCode");
            this.adapter.notifyDataSetChanged();
        }
    }*/
}

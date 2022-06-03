package com.kumkangkind.kumkangsm2;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kumkangkind.kumkangsm2.ProgressFloor.ProgressFloorReturnViewAdapter;

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
import java.util.HashMap;
import java.util.TreeMap;

public class ProgressFloorReturnActivity extends BaseActivity {

    private RecyclerView recyclerView;
    //private SuWorder[] items;

    TextView tvCustomerLocation;
    String contractNo = "";
    String locationNo="";
    String type = "";
    String restURL = "";
    String arrayName = "";
    String customerLocation = "";
    public HashMap<String, Dong> dongHashMap;
    TreeMap<String, Dong> dongTreeMap;
    ProgressFloorReturnViewAdapter adapter;
    ArrayList<Dong> dongArrayList;
    TextView txtFromDate;
    public int tyear;
    public int tmonth;
    public int tdate;

    String dong = "";
    //String yearMonth = "";
    String floor = "";
    //String inputYearMonth = "";
    String inputFloor = "";
    //int clickPosition = -1;//다이얼로그 종료시, 복귀 포지션을 위함
    View clickView = null;
    long clickId = 0;
    LinearLayout layoutTop;

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
        setContentView(R.layout.progress_floor_return_listview);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);
        contractNo = getIntent().getStringExtra("contractNo");
        locationNo = getIntent().getStringExtra("locationNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        tvCustomerLocation.setText(customerLocation);
        this.txtFromDate = findViewById(R.id.txtFromDate);
        final Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);
        this.layoutTop = findViewById(R.id.layoutTop);

        this.txtFromDate.setText("[ " + tyear + "-" + (tmonth + 1) + "-" + tdate + " ]");
        this.txtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(tyear, tmonth, tdate);
            }
        });
        //dongHashMap = (HashMap<String, Dong>) (getIntent().getSerializableExtra("dongHashMap"));//todo


        // Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
        // Iterator<String> keyIterator = dongTreeMap.keySet().iterator();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        String fromDate = tyear + "-" + (tmonth + 1) + "-" + tdate;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getDongProgressFloorReturn2(fromDate);
    }

    private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtFromDate.setText("[ " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " ]");
                                tyear = year;
                                tmonth = monthOfYear;
                                tdate = dayOfMonth;
                                String fromDate = tyear + "-" + (tmonth + 1) + "-" + tdate;
                                //DATA가져오기
                                //getViewSaleOrderData(fromDate);
                                getDongProgressFloorReturn2(fromDate);
                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }

    public void getDongProgressFloorReturn2(String fromDate) {
        String url = getString(R.string.service_address) + "getDongProgressFloorReturn2";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("FromDate", fromDate);
        values.put("LocationNo", locationNo);
        GetDongProgressFloorReturn2 gsod = new GetDongProgressFloorReturn2(url, values, fromDate);
        gsod.execute();
    }

    public class GetDongProgressFloorReturn2 extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String fromDate;

        GetDongProgressFloorReturn2(String url, ContentValues values, String fromDate) {
            this.url = url;
            this.values = values;
            this.fromDate = fromDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {

                HashMap<String, Dong> dongHashMap = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                Dong dong = null;
                String key = "";
                String Dong = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ProgressFloorReturnActivity.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Dong = child.getString("Dong");
                    key = Dong;

                    if (!dongHashMap.containsKey(key)) {//없으면
                        dong = new Dong();
                        dong.Dong = child.getString("Dong");
                        if (child.getString("Type").equals("당일")) {
                            dong.ProgressDate = child.getString("ProgressDate");
                            dong.ProgressFloor = child.getString("ProgressFloor");
                        } else {
                            dong.ExProgressDate = child.getString("ProgressDate");
                            dong.ExProgressFloor = child.getString("ProgressFloor");
                        }
                        dong.CollectEmployee = child.getString("CollectEmployee");
                        dong.InPlanData = child.getString("InPlanData");
                        dong.TotalFloor = child.getString("TotalFloor");
                        dong.BaseFloor = child.getString("BaseFloor");
                        dongHashMap.put(key, dong);
                    } else {//있으면: 전 달 데이터 setting
                        dong = dongHashMap.get(key);
                        if (child.getString("Type").equals("당일")) {
                            dong.ProgressDate = child.getString("ProgressDate");
                            dong.ProgressFloor = child.getString("ProgressFloor");
                        } else {
                            dong.ExProgressDate = child.getString("ProgressDate");
                            dong.ExProgressFloor = child.getString("ProgressFloor");
                        }
                    }
                }


                dongTreeMap = new TreeMap<>(dongHashMap);
                dongArrayList = new ArrayList<>();

                for (Dong _dong : dongTreeMap.values()) {
                    dongArrayList.add(_dong);
                }

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(ProgressFloorReturnActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);

                ProgressFloorReturnViewAdapter adapter = new ProgressFloorReturnViewAdapter(ProgressFloorReturnActivity.this, layoutTop, contractNo, this.fromDate);
                adapter.setItems(dongArrayList);
                recyclerView.setAdapter(adapter);

                // adapter = new ProgressFloorReturnViewAdapter(, R.layout.progress_floor_return_row, dongArrayList, );
                // recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                //Log.i("순서확인", "미납/재고종료");
                progressOFF2(this.getClass().getName());
            }
        }
    }

    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnDelete://삭제
                //ShowDeleteDialog();
                break;


            case R.id.btnAdd://추가
                //ShowAddDialog();
                break;
        }
    }

   /* private void ShowDeleteDialog() {


        if (clickPosition == -1) {
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


        new AlertDialog.Builder(ProgressFloorReturnActivity.this)


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

    }*/



    /*AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String mes = items[position -1].WorkNo;
            //이동한다
            //String key = items[position - 1].WorkNo;
            dong = dongArrayList.get(position).Dong;

            yearMonth = dongArrayList.get(position).ProgressYearMonth;//입력 년월로 수정
            floor = dongArrayList.get(position).ProgressFloor;//입력 층으로 수정

            clickPosition = position;
            clickView = view;
            clickId = id;

            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 *//*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*//*

        }
    };*/


    //진행 월/층 추가
    /*public Handler mHandler = new Handler() { //다이얼로그 종료시 액티비티 데이터 전송을 위함
        @Override
        public void handleMessage(Message msg) {
            inputYearMonth = msg.getData().getString("yearMonth");
            if (inputYearMonth.equals("취소")) {
                Toast.makeText(ProgressFloorReturnActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            inputFloor = msg.getData().getString("floor");
            new SetDongProgressFloorByPost().execute(getString(R.string.service_address) + "setDongProgressFloor");

        }
    };*/

    private class SetDongProgressFloorByPost extends AsyncTask<String, Void, String> {//todo


        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String ResultCode = "";
            String Message = "";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ResultCode.equals("false")) {
                Toast.makeText(ProgressFloorReturnActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(ProgressFloorReturnActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            new GetDongProgessFloorByPost().execute(getString(R.string.service_address) + "getDongProgressFloor");

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
                HashMap<String, Dong> _dongHashMap = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                Dong dong = null;
                String key = "";
                String Dong = "";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Dong = child.getString("Dong");
                    key = Dong;

                    if (!_dongHashMap.containsKey(key)) {//없으면
                        dong = new Dong(child.getString("Dong"),
                                child.getString("ProgressYearMonth"), child.getString("ProgressFloor"), child.getString("ConstructionEmployee"));
                        _dongHashMap.put(key, dong);
                    } else {//있으면: 전 달 데이터 setting
                        dong = _dongHashMap.get(key);
                        dong.ExProgressYearMonth = child.getString("ProgressYearMonth");
                        dong.ExProgressFloor = child.getString("ProgressFloor");
                    }

                }
                dongHashMap = _dongHashMap;


                dongTreeMap = new TreeMap<>(dongHashMap);
                //    Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
                //   Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                //View header = (View) getLayoutInflater().inflate(R.layout.progress_floor_header, null);
                // listView1.addHeaderView(header);
                dongArrayList = new ArrayList<>();

                for (Dong _dong : dongTreeMap.values()) {
                    dongArrayList.add(_dong);
                }

                //adapter = new ProgressFloorReturnViewAdapter(ProgressFloorReturnActivity.this, R.layout.progress_floor_row, dongArrayList);
                //adapter.notifyDataSetChanged();
                //listView1.setAdapter(adapter);
                //clickPosition = -1;
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
            //jsonObject.put("YearMonth", inputYearMonth);//계약번호
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
  /*  public Handler mHandler2 = new Handler() { //다이얼로그 종료시 액티비티에 재반영 작업을 위함
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//확인
                inputYearMonth = yearMonth;
                new DeleteDongProgressFloorByPost().execute(getString(R.string.service_address) + "deleteDongProgressFloor");

            } else {//취소
                Toast.makeText(ProgressFloorReturnActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };*/


    private class DeleteDongProgressFloorByPost extends AsyncTask<String, Void, String> {//todo


        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            String ResultCode = "";
            String Message = "";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ResultCode.equals("false")) {
                Toast.makeText(ProgressFloorReturnActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(ProgressFloorReturnActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                new GetDongProgessFloorByPost().execute(getString(R.string.service_address) + "getDongProgressFloor");
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


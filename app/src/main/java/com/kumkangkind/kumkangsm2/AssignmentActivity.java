package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class AssignmentActivity extends BaseActivity{
    private ListView listView1;
    //private SuWorder[] items;

    TextView tvCustomerLocation;
    String contractNo = "";
    String type = "";
    String restURL = "";
    String arrayName = "";
    String customerLocation = "";
    TreeMap<String, String> dongTreeMap;
    AssignmentViewAdapter adapter;
    ArrayList<Dong> dongArrayList;
    public HashMap<String, String> dongHashMap;
    String dong = "";
    String yearMonth = "";
    String floor = "";
    String inputYearMonth = "";
    String inputFloor = "";
    int clickPosition=0;
    View clickView=null;
    long clickId=0;
    String constructionEmployee="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_assignment);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);
        contractNo = getIntent().getStringExtra("contractNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        tvCustomerLocation.setText(customerLocation);
        dongHashMap = (HashMap<String, String>) (getIntent().getSerializableExtra("dongHashMap"));//todo
        dongTreeMap = new TreeMap<>(dongHashMap);

        // Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
        // Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

        listView1 = (ListView) findViewById(R.id.listView1);
        dongArrayList = new ArrayList<>();
        Dong dong= null;

        for (Map.Entry<String,String> dongEntry:dongTreeMap.entrySet()) {
            dong= new Dong(dongEntry.getKey(),dongEntry.getValue());
            dongArrayList.add(dong);
        }


        adapter = new AssignmentViewAdapter(AssignmentActivity.this, R.layout.assignment_row, dongArrayList);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);

    }






    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            dong = dongArrayList.get(position).Dong;
            constructionEmployee=dongArrayList.get(position).ConstructionEmployee;
            clickPosition=position;

            /*
             * 담당자가 미배정 상태일시, 담당자 추가 다이얼로그
             * 담당자가 있을시(본인), 담당자 제거 다이얼로그
             * */

            if(constructionEmployee.equals("미배정"))
                ShowAddDialog();
            else
                ShowDeleteDialog();



            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 /*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*/

        }
    };

    private void ShowAddDialog(){

        new AlertDialog.Builder(AssignmentActivity.this)


                .setTitle("담당자 등록")
                .setMessage(customerLocation+"-"+dong+"의 담당자를 '"+ Users.UserName+"'"+" 로 지정하시겠습니까?")
                //.setIcon(R.drawable.ninja)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mHandler.sendEmptyMessage(1);
                            }
                        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mHandler.sendEmptyMessage(0);

                    }
                }).show();
    }

    private void ShowDeleteDialog(){

        if (!constructionEmployee.equals(Users.UserName)) {
            Toast.makeText(getApplicationContext(), "다른 담당자의 현장정보는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(AssignmentActivity.this)


                .setTitle("담당자 삭제")

                .setMessage(customerLocation+"-"+dong+"의 담당자를 삭제 하시겠습니까? ")
                //.setIcon(R.drawable.ninja)
                .setPositiveButton("삭제",
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


    //담당자 등록
    public Handler mHandler = new Handler() { //다이얼로그 종료시 액티비티 데이터 전송을 위함
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){//담당자등록->확인
                new AssignmentActivity.SetConstructionEmployeeByPost().execute(getString(R.string.service_address)+"setConstructionEmployee");
                return;
            }
            else{
                Toast.makeText(AssignmentActivity.this, "담당자 등록이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }


        }
    };

    private class SetConstructionEmployeeByPost extends AsyncTask<String, Void, String> {//todo


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
                Toast.makeText(AssignmentActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }


         //   Toast.makeText(AssignmentActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

           // new AssignmentActivity.GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");
            dongArrayList.get(clickPosition).ConstructionEmployee=Users.UserName;
            listView1.setAdapter(adapter);
            Toast.makeText(AssignmentActivity.this, "담당자 등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
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
            jsonObject.put("UserCode", Users.USER_ID);//유저번호: 슈퍼바이저번호

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
                new AssignmentActivity.DeleteConstructionEmployeeByPost().execute(getString(R.string.service_address)+"deleteConstructionEmployee");

            } else {//취소
                Toast.makeText(AssignmentActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class DeleteConstructionEmployeeByPost extends AsyncTask<String, Void, String> {//todo


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
                Toast.makeText(AssignmentActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }


            dongArrayList.get(clickPosition).ConstructionEmployee="미배정";

            listView1.setAdapter(adapter);

            Toast.makeText(AssignmentActivity.this, "담당자 삭제가 완료 되었습니다.", Toast.LENGTH_SHORT).show();

            //new AssignmentActivity.GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");

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

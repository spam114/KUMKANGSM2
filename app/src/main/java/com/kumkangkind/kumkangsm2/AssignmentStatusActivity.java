package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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

public class AssignmentStatusActivity extends BaseActivity{
    CheckBox chkAll;

    private ListView listView1;
    //private SuWorder[] items;
    TextView tvCustomerLocation;
    String contractNo = "";
    String type = "";
    String restURL = "";
    String arrayName = "";
    String customerLocation = "";
    TreeMap<String, String> dongTreeMap;
    AssignmentStatusAdapter adapter;
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
    ImageButton imageButton;
    String supervisorUserCode="";
    String supervisorUserName="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_assignment_status);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);
        contractNo = getIntent().getStringExtra("contractNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        tvCustomerLocation.setText(customerLocation);
        dongHashMap = (HashMap<String, String>) (getIntent().getSerializableExtra("dongHashMap"));//todo
        dongTreeMap = new TreeMap<>(dongHashMap);

        // Set<String> keyset = dongHashMap.keySet();//TreeMap 을 이용한 Key값으로 정렬
        // Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

        listView1 = (ListView) findViewById(R.id.listview_status);
        dongArrayList = new ArrayList<>();
        Dong dong= null;

        for (Map.Entry<String,String> dongEntry:dongTreeMap.entrySet()) {
            dong= new Dong(dongEntry.getKey(),dongEntry.getValue());
            dongArrayList.add(dong);
        }


        adapter = new AssignmentStatusAdapter(AssignmentStatusActivity.this, R.layout.assignment_status_row, dongArrayList);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);

    }



    protected void ShowAddDialog(String dong, int position){
        clickPosition=position;
        this.dong=dong;
        //Dialog에서 보여줄 입력화면 View 객체 생성 작업
        //Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
        LayoutInflater inflater=getLayoutInflater();

        //res폴더>>layout폴더>>dialog_addmember.xml 레이아웃 리소스 파일로 View 객체 생성
        //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
        final View dialogView= inflater.inflate(R.layout.dialog_find_supervisor, null);

        TextView textView=dialogView.findViewById(R.id.textView);
        TextView textView2=dialogView.findViewById(R.id.textView2);
        /*
         * 스피너의 폰트, 글자색 변경을 위함
         * */
        chkAll=dialogView.findViewById(R.id.chkAll);

        /**
         * 체크박스 상태변경
         */
        chkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){//체크할시
                    Toast.makeText(getBaseContext(), "'일괄'을 체크할 시, 해당 현장의 모든 동을 지정한 담당자로 배정합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Spinner searchSpinner=dialogView.findViewById(R.id.superVisorSpinner);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        buider.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                supervisorUserName=searchSpinner.getSelectedItem().toString();

                for (int i=0;i<Users.supervisorList.length;i++) {
                    if (Users.supervisorList[i].substring(6, Users.supervisorList[i].length()).equals(searchSpinner.getSelectedItem().toString())) {//저장 할 슈퍼바이저의 userCode 찾기
                        supervisorUserCode = Users.supervisorList[i].substring(0, 5);
                    }
                }

                dialog.cancel();
                mHandler.sendEmptyMessage(1);//확인

            }
        });


        buider.setNegativeButton("취소", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mHandler.sendEmptyMessage(0);//취소
            }
        });

//설정한 값으로 AlertDialog 객체 생성

        AlertDialog dialog=buider.create();
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
        //Dialog 보이기

        dialog.show();

        String[] userList= new String[Users.supervisorList.length];
        int myNum=0;
        int j=0;
        for (int i=0;i<Users.supervisorList.length;i++) {
            if(Users.supervisorList[i].substring(6,Users.supervisorList[i].length()).equals("미지정")){//미지정 제외
                continue;
            }

            if(Users.supervisorList[i].substring(6,Users.supervisorList[i].length()).equals(Users.UserName)){//내 이름의 indexNo 찾기
                myNum=j;
            }

            userList[j]=Users.supervisorList[i].substring(6,Users.supervisorList[i].length());
            j++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                userList);
        searchSpinner.setAdapter(adapter);
    }

    protected void ShowDeleteDialog(String dong, int position){
        clickPosition=position;
        this.dong=dong;


        new AlertDialog.Builder(AssignmentStatusActivity.this)
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
                if(!chkAll.isChecked())//체크 안되있음
                    new AssignmentStatusActivity.SetConstructionEmployeeByPost().execute(getString(R.string.service_address)+"setConstructionEmployee");
                else//체크되있음: 일괄
                    new AssignmentStatusActivity.SetConstructionEmployeeByPost().execute(getString(R.string.service_address)+"setConstructionEmployeeDesignated");
                return;
            }
            else{
                Toast.makeText(AssignmentStatusActivity.this, "담당자 등록이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AssignmentStatusActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }

            //   Toast.makeText(AssignmentActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            // new AssignmentActivity.GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");
            if(!chkAll.isChecked()) {//체크 안되있음
                dongArrayList.get(clickPosition).ConstructionEmployee=supervisorUserName;
                listView1.setAdapter(adapter);
            }
            else {//일괄
                for(int i=0;i<dongArrayList.size();i++){
                    dongArrayList.get(i).ConstructionEmployee=supervisorUserName;
                    listView1.setAdapter(adapter);
                }
            }


            Toast.makeText(AssignmentStatusActivity.this, "담당자 등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
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
            jsonObject.put("UserCode", supervisorUserCode);//슈퍼바이저번호

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
                new AssignmentStatusActivity.DeleteConstructionEmployeeByPost().execute(getString(R.string.service_address)+"deleteConstructionEmployee");

            } else {//취소
                Toast.makeText(AssignmentStatusActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AssignmentStatusActivity.this, Message, Toast.LENGTH_SHORT).show();
                return;
            }


            dongArrayList.get(clickPosition).ConstructionEmployee="미배정";
            listView1.setAdapter(adapter);

            Toast.makeText(AssignmentStatusActivity.this, "담당자 삭제가 완료 되었습니다.", Toast.LENGTH_SHORT).show();

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

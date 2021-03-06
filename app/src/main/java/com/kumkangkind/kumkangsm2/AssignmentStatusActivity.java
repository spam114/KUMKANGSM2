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

        // Set<String> keyset = dongHashMap.keySet();//TreeMap ??? ????????? Key????????? ??????
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
        //Dialog?????? ????????? ???????????? View ?????? ?????? ??????
        //Layout xml ????????? ????????? View ????????? ????????? ??????(inflate) LayoutInflater ?????? ??????
        LayoutInflater inflater=getLayoutInflater();

        //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
        //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
        final View dialogView= inflater.inflate(R.layout.dialog_find_supervisor, null);

        TextView textView=dialogView.findViewById(R.id.textView);
        TextView textView2=dialogView.findViewById(R.id.textView2);
        /*
         * ???????????? ??????, ????????? ????????? ??????
         * */
        chkAll=dialogView.findViewById(R.id.chkAll);

        /**
         * ???????????? ????????????
         */
        chkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){//????????????
                    Toast.makeText(getBaseContext(), "'??????'??? ????????? ???, ?????? ????????? ?????? ?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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



        //????????? ???????????? ?????? Dialog ?????? ??? ?????????
        AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder ?????? ??????
        //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
        buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)
        buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                supervisorUserName=searchSpinner.getSelectedItem().toString();

                for (int i=0;i<Users.supervisorList.length;i++) {
                    if (Users.supervisorList[i].substring(6, Users.supervisorList[i].length()).equals(searchSpinner.getSelectedItem().toString())) {//?????? ??? ?????????????????? userCode ??????
                        supervisorUserCode = Users.supervisorList[i].substring(0, 5);
                    }
                }

                dialog.cancel();
                mHandler.sendEmptyMessage(1);//??????

            }
        });


        buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mHandler.sendEmptyMessage(0);//??????
            }
        });

//????????? ????????? AlertDialog ?????? ??????

        AlertDialog dialog=buider.create();
        //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
        dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
        //Dialog ?????????

        dialog.show();

        String[] userList= new String[Users.supervisorList.length];
        int myNum=0;
        int j=0;
        for (int i=0;i<Users.supervisorList.length;i++) {
            if(Users.supervisorList[i].substring(6,Users.supervisorList[i].length()).equals("?????????")){//????????? ??????
                continue;
            }

            if(Users.supervisorList[i].substring(6,Users.supervisorList[i].length()).equals(Users.UserName)){//??? ????????? indexNo ??????
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
                .setTitle("????????? ??????")
                .setMessage(customerLocation+"-"+dong+"??? ???????????? ?????? ??????????????????? ")
                //.setIcon(R.drawable.ninja)
                .setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mHandler2.sendEmptyMessage(1);
                            }
                        })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mHandler2.sendEmptyMessage(0);

                    }
                }).show();
    }


    //????????? ??????
    public Handler mHandler = new Handler() { //??????????????? ????????? ???????????? ????????? ????????? ??????
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){//???????????????->??????
                if(!chkAll.isChecked())//?????? ????????????
                    new AssignmentStatusActivity.SetConstructionEmployeeByPost().execute(getString(R.string.service_address)+"setConstructionEmployee");
                else//???????????????: ??????
                    new AssignmentStatusActivity.SetConstructionEmployeeByPost().execute(getString(R.string.service_address)+"setConstructionEmployeeDesignated");
                return;
            }
            else{
                Toast.makeText(AssignmentStatusActivity.this, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
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

            //   Toast.makeText(AssignmentActivity.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

            // new AssignmentActivity.GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");
            if(!chkAll.isChecked()) {//?????? ????????????
                dongArrayList.get(clickPosition).ConstructionEmployee=supervisorUserName;
                listView1.setAdapter(adapter);
            }
            else {//??????
                for(int i=0;i<dongArrayList.size();i++){
                    dongArrayList.get(i).ConstructionEmployee=supervisorUserName;
                    listView1.setAdapter(adapter);
                }
            }


            Toast.makeText(AssignmentStatusActivity.this, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
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


            jsonObject.put("ContractNo", contractNo);//????????????
            jsonObject.put("Dong", dong);//???
            jsonObject.put("UserCode", supervisorUserCode);//?????????????????????

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


    //?????? ???/??? ??????
    public Handler mHandler2 = new Handler() { //??????????????? ????????? ??????????????? ????????? ????????? ??????
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//??????
                inputYearMonth=yearMonth;
                new AssignmentStatusActivity.DeleteConstructionEmployeeByPost().execute(getString(R.string.service_address)+"deleteConstructionEmployee");

            } else {//??????
                Toast.makeText(AssignmentStatusActivity.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
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


            dongArrayList.get(clickPosition).ConstructionEmployee="?????????";
            listView1.setAdapter(adapter);

            Toast.makeText(AssignmentStatusActivity.this, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();

            //new AssignmentActivity.GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");

        }
    }




    /**
     * ?????? ???????????? ????????????.
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

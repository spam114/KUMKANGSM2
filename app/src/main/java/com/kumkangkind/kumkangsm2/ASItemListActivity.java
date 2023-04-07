package com.kumkangkind.kumkangsm2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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


public class ASItemListActivity extends BaseActivity {


    HashMap<Integer, String> typeHashMap;
    HashMap<Integer, String> partNameHashMap;
    HashMap<Integer, String> causeHashMap;
    HashMap<Integer, String> asTypeHashMap;

    ArrayList<String> typeArrayList;
    ArrayList<String> partNameArrayList;
    ArrayList<String> causeArrayList;
    ArrayList<String> asTypeArrayList;

    private ListView listView1;
    View tempView;//임시뷰

    TextView tvCustomerLocation;

    String customer = "";//거래처
    String location = "";//현장
    ArrayList<ASItem> asItemArrayList;//추가분 세부분류항목
    ASItemAdapter adapter;

    int clickPosition = -1;

    View clickView = null;
    long clickId = 0;

    Button btnAdd;
    Button btnDelete;
    Button btnEdit;
    Button btnCopy;

    String supervisorWoNo;
    String type;//수정용 확인을 위함
    String statusFlag;
    String asNoForCopy;//복사를 위해 insert시키고 생성된 ASNO
    String contractNo;

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
        setContentView(R.layout.listview_asitem);

        typeHashMap = new HashMap<>();
        partNameHashMap = new HashMap<>();
        causeHashMap = new HashMap<>();
        asTypeHashMap = new HashMap<>();

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);

        type = getIntent().getStringExtra("type");
        statusFlag = getIntent().getStringExtra("statusFlag");

        customer = getIntent().getStringExtra("customer");
        location = getIntent().getStringExtra("location");
        asItemArrayList = (ArrayList<ASItem>) getIntent().getSerializableExtra("asItemArrayList");
        supervisorWoNo = getIntent().getStringExtra("supervisorWoNo");
        contractNo = getIntent().getStringExtra("contractNo");
        tvCustomerLocation.setText(customer + " - " + location);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnCopy = findViewById(R.id.btnCopy);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickPosition == -1)
                    Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else
                    ShowDeleteDialog();

            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickPosition == -1)
                    Toast.makeText(getBaseContext(), "수정할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else
                    ShowEditDialog();

            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickPosition == -1)
                    Toast.makeText(getBaseContext(), "복사할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                else {
                    new GetASItemStandard().execute(getString(R.string.service_address) + "getASItemStandard");//AS 기준정보를 가져온 후에, 복사 다이얼로그를 실행한다.
                }
            }
        });

        listView1 = (ListView) findViewById(R.id.listView1);


        adapter = new ASItemAdapter(ASItemListActivity.this, R.layout.listview_asitem_row, asItemArrayList);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);

        if (type.equals("확인") || statusFlag.equals("2")) {//수정용으로 확인
            HorizontalScrollView scrollView = findViewById(R.id.scroll);
            LinearLayout layout = findViewById(R.id.layout);
            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
            params.weight = 9;
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layout.getLayoutParams();
            params2.weight = 0;

        }

        if (type.equals("현장별")) {
            btnAdd.setVisibility(View.GONE);
        }

        progressOFF();

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
                    String StandardNo = "";
                    String Name = "";
                    String Parent = "";

                    StandardNo = child.getString("StandardNo");
                    Name = child.getString("Name");
                    Parent = child.getString("Parent");

                    if (Integer.parseInt(Parent) == 1) {
                        typeHashMap.put(Integer.parseInt(StandardNo), Name);
                    } else if (Integer.parseInt(Parent) == 2) {
                        partNameHashMap.put(Integer.parseInt(StandardNo), Name);
                    } else if (Integer.parseInt(Parent) == 3) {
                        causeHashMap.put(Integer.parseInt(StandardNo), Name);
                    } else if (Integer.parseInt(Parent) == 4) {
                        asTypeHashMap.put(Integer.parseInt(StandardNo), Name);
                    }
                }

                typeArrayList = new ArrayList<>(typeHashMap.values());
                partNameArrayList = new ArrayList<>(partNameHashMap.values());
                causeArrayList = new ArrayList<>(causeHashMap.values());
                asTypeArrayList = new ArrayList<>(asTypeHashMap.values());

                ShowCopyDialog();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                e.printStackTrace();
            }
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
                Toast.makeText(getBaseContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getBaseContext(), "A/S항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            inputStream.close();
            return result;
        }

    }


    public void mOnClick(View v) {
        startProgress();

        switch (v.getId()) {


            case R.id.btnAdd://추가
                ShowAddDialog();
                break;

        }
    }

    private void ShowDeleteDialog() {

        final ASItem asItem;
        try {
            asItem = asItemArrayList.get(clickPosition);

            if (type.equals("현장별") && !Users.USER_ID.equals(asItem.SupervisorCode)) {
                Toast.makeText(getBaseContext(), "본인이 등록한 A/S항목만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = "동: " + asItem.Dong + "\n" +
                    "세대/위치: " + asItem.Ho + " " + asItem.HoLocation + "\n" +
                    "구분: " + asItem.ItemType + "\n" +
                    "품명: " + asItem.Item + "\n" +
                    "수량: " + asItem.Quantity + "\n" +
                    "항목 정보를 삭제하시겠습니까?";

            new android.app.AlertDialog.Builder(tempView.getContext())
                    .setTitle("A/S항목 삭제")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton
                            ("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DeleteASItem(asItem.SupervisorASNo, clickPosition).execute(getString(R.string.service_address) + "deleteASItem");
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } catch (IndexOutOfBoundsException e) {
            clickPosition = -1;
            Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "삭제를 하려면, 항목을 길게 눌러주시기 바랍니다.", Toast.LENGTH_SHORT).show();

    }

    private void ShowCopyDialog() {

        final ASItem asItem;
        try {
            asItem = asItemArrayList.get(clickPosition);

            if (type.equals("현장별") && !Users.USER_ID.equals(asItem.SupervisorCode)) {
                Toast.makeText(getBaseContext(), "본인이 등록한 A/S항목만 복사할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = "동: " + asItem.Dong + "\n" +
                    "세대/위치: " + asItem.Ho + " " + asItem.HoLocation + "\n" +
                    "구분: " + asItem.ItemType + "\n" +
                    "품명: " + asItem.Item + "\n" +
                    "수량: " + asItem.Quantity + "\n" +
                    "항목 정보를 복사하시겠습니까?";

            new android.app.AlertDialog.Builder(tempView.getContext())
                    .setTitle("A/S항목 복사")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton
                            ("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new SetASItemByPost(asItem).execute(getString(R.string.service_address) + "setASItem");
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } catch (IndexOutOfBoundsException e) {
            clickPosition = -1;
            Toast.makeText(getBaseContext(), "삭제할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "삭제를 하려면, 항목을 길게 눌러주시기 바랍니다.", Toast.LENGTH_SHORT).show();

    }

    private class SetASItemByPost extends AsyncTask<String, Void, String> {
        ASItem asItem;

        public SetASItemByPost(ASItem asItem) {
            this.asItem = asItem;
        }

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

                String ResultCode = "";
                String Message = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode = child.getString("ResultCode");
                    Message = child.getString("Message");


                }

                if (ResultCode.equals("false")) {
                    Toast.makeText(getBaseContext(), "복사에 실패하였습니다. " + Message, Toast.LENGTH_SHORT).show();
                    return;
                } else {//저장성공
                    Toast.makeText(getBaseContext(), "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    ASItem item = new ASItem(asItem.SupervisorCode, asItem.SupervisorName, asNoForCopy, supervisorWoNo, asItem.Dong, asItem.Ho, asItem.HoLocation, asItem.ItemType,
                            asItem.Item, asItem.ItemSpecs, asItem.Quantity, asItem.Reason, asItem.AsType,
                            asItem.Remark, asItem.Actions, asItem.ActionEmployee, asItem.FromDate);

                    asItemArrayList.add(item);
                    adapter.notifyDataSetChanged();
                }

            } catch (Exception e) {
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

                int ItemType = -1;
                int Item = -1;
                int Reason = -1;
                int ASType = -1;

                String qty;//수량

                for (Map.Entry<Integer, String> haEntry : typeHashMap.entrySet()) {
                    if (asItem.ItemType.equals(haEntry.getValue()))
                        ItemType = haEntry.getKey();
                }

                for (Map.Entry<Integer, String> haEntry : partNameHashMap.entrySet()) {
                    if (asItem.Item.equals(haEntry.getValue()))
                        Item = haEntry.getKey();
                }

                for (Map.Entry<Integer, String> haEntry : causeHashMap.entrySet()) {
                    if (asItem.Reason.equals(haEntry.getValue()))
                        Reason = haEntry.getKey();
                }

                for (Map.Entry<Integer, String> haEntry : asTypeHashMap.entrySet()) {
                    if (asItem.AsType.equals(haEntry.getValue()))
                        ASType = haEntry.getKey();
                }

                qty = asItem.Quantity;

                if (qty.equals("")) {//빈칸이면 0처리
                    qty = "0";
                }

                jsonObject.put("SupervisorWoNo", supervisorWoNo);
                jsonObject.put("SupervisorCode", Users.USER_ID);
                jsonObject.put("Dong", asItem.Dong);
                jsonObject.put("Ho", asItem.Ho);
                jsonObject.put("HoLocation", asItem.HoLocation);
                jsonObject.put("ItemType", ItemType);
                jsonObject.put("Item", Item);
                jsonObject.put("ItemSpecs", asItem.ItemSpecs);
                jsonObject.put("Quantity", qty);
                jsonObject.put("Reason", Reason);
                jsonObject.put("AsType", ASType);
                jsonObject.put("Remark", asItem.Remark);
                jsonObject.put("Actions", asItem.Actions);
                jsonObject.put("ActionEmployee", asItem.ActionEmployee);
                jsonObject.put("ContractNo", contractNo);
                jsonObject.put("FromDate", asItem.FromDate);

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

                if (resultCode.equals("true"))
                    asNoForCopy = message;

            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

    }


    private class DeleteASItem extends AsyncTask<String, Void, String> {

        String supervisorASNo;//추가분 번호
        int position;//리스트상에서의 위치

        public DeleteASItem(String supervisorASNo, int position) {
            this.supervisorASNo = supervisorASNo;
            this.position = position;
        }

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String resultCode = "";
                String message = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode = child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if (resultCode.equals("false"))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else {

                    asItemArrayList.remove(position);
                    adapter.notifyDataSetChanged();

                    if (asItemArrayList.size() != 0) {//지우고난후 리스트에 자료가 있을시!

                    } else {//지우고난후 없을시
                        clickPosition = -1;
                    }

                    Toast.makeText(getBaseContext(), "항목이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
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

                //Delete & Insert
                jsonObject.put("SupervisorASNo", supervisorASNo);
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
    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            tempView = view;
            clickPosition = position;

            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 /*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*/

        }
    };


    /**
     * A/S 항목을 등록한다.
     */
    private void ShowAddDialog() {


        ASItemDialog asItemDialog = new ASItemDialog(this, supervisorWoNo, Users.USER_ID, Users.UserName, contractNo);
        asItemDialog.show();
        asItemDialog.setDialogResult(new ASItemDialog.OnDialogResult() {
            @Override
            public void finish(ASItem asItem) {
                asItemArrayList.add(asItem);
                adapter.notifyDataSetChanged();
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = asItemDialog.getWindow();
        int x = (int) (size.x * 0.9f);

        window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);
        progressOFF();

    }


    /*
       A/S수정 다이얼로그를 부른다.
       */
    private void ShowEditDialog() {
        try {
            ASItem tempASItem = asItemArrayList.get(clickPosition);

            if (type.equals("현장별") && !Users.USER_ID.equals(tempASItem.SupervisorCode)) {
                Toast.makeText(getBaseContext(), "본인이 등록한 A/S항목만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            ASItemDialog asItemDialog = new ASItemDialog(this, tempASItem.SupervisorWoNo, tempASItem, true, tempASItem.SupervisorCode, tempASItem.SupervisorName, contractNo);
            asItemDialog.show();
            asItemDialog.setDialogResult(new ASItemDialog.OnDialogResult() {
                @Override
                public void finish(ASItem asItem) {//다이얼로그에서 반환한 값

                    asItemArrayList.set(clickPosition, asItem);
                    adapter.notifyDataSetChanged();
                }
            });

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = asItemDialog.getWindow();
            int x = (int) (size.x * 0.9f);

            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);
            progressOFF();

        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getBaseContext(), "수정 할 항목을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * 세부사항 구분값을 가져온다.
     *//*

    private class GetAddItemStandard extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForAddItemStandard(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {

                JSONArray jsonArray = new JSONArray(result);
                String StandardNo="";
                String Name="";
                AddItemStandard addItemStandard;

                for (int i = 0; i < jsonArray.length(); i++) {



                    JSONObject child = jsonArray.getJSONObject(i);
                    StandardNo = child.getString("StandardNo");
                    Name=child.getString("Name");

                    addItemStandard= new AddItemStandard(StandardNo,Name);
                    addItemStandardArrayList.add(addItemStandard);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/
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

        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }


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

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            //int seqNo = data.getIntExtra("seqNo", -1);
            //   suWorders.get(seqNo).Supervisor  = data.getStringExtra("SupervisorName");
            //data.getStringExtra("SupervisorCode");
            this.adapter.notifyDataSetChanged();
        }
    }


}

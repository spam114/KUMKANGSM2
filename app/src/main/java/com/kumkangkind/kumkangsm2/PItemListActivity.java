package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 1. 해당 클래스는 작업지시 목록을 보인다.
 * 2. 항목을 클릭할 경우, 서버와 통신하여 해당 데이터를 가져온다.
 */
public class PItemListActivity extends BaseActivity {

    private ListView listView1;
    private WoItem[] items;
    TextView textViewUserName;
    ArrayList<WoItem> itemList;
    PItemAdapter adapter;
    String key = "";
    ProgressDialog mProgressDialog;
    int removePosition = 0;
    WoItem currentItem = null;
    String FIXFLAG = "0";
    Dialog dialog;
    EditText editItemName;
    EditText editPersonCount;
    EditText editQuantity;
    String customer = "";
    Button addButton;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_image);

        key = getIntent().getStringExtra("key");
        customer = getIntent().getStringExtra("customer");
        FIXFLAG = getIntent().getStringExtra("fix");
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(customer);
        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setText("+ 품목추가");


        makeImageList();

        adapter = new PItemAdapter(this, R.layout.listview_itemrow, itemList);

        //ListView
        listView1 = (ListView) findViewById(R.id.listViewImage);
        View header = (View) getLayoutInflater().inflate(R.layout.listview_itemheader, null);
        listView1.addHeaderView(header);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);
        listView1.setFocusable(false);

        progressOFF();
    }

    public void mOnClick(View v) {

        if (FIXFLAG.equals("2"))
            return;

        switch (v.getId()) {


            case R.id.btnAdd: //추가

                //Dialog 디자인 해야함

                dialog = new Dialog(PItemListActivity.this);

                dialog.setContentView(R.layout.dialog_additem);
                dialog.setTitle("품목추가");

                editItemName = (EditText) dialog.findViewById(R.id.edittextItemName);
                editPersonCount = (EditText) dialog.findViewById(R.id.edittextPersonCount);
                editQuantity = (EditText) dialog.findViewById(R.id.edittextQuantity);
                Button okButton = (Button) dialog.findViewById(R.id.btnItemOK);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        InsetItem();
                        dialog.dismiss();
                    }
                });

                Button cancelButton = (Button) dialog.findViewById(R.id.btnItemCancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;

            case R.id.buttonDelete:

                final View v2 = v;
                new AlertDialog.Builder(this).setMessage("삭제할까요?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //POST 명령어 호출(업데이트를 적용한다)

                        mHandler = new Handler();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog = ProgressDialog.show(PItemListActivity.this, "",
                                        "잠시만 기다려 주세요.", true);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 10000);
                            }
                        });

                        int position = (int) v2.getTag();

                        if (position != ListView.INVALID_POSITION) {
                            removePosition = position;
                            currentItem = itemList.get(position);
                            new HttpAsyncTaskDelete().execute(getString(R.string.service_address)+"deleteitem");
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0)
                return;
            //클릭했을 때 동작

            currentItem = itemList.get(position - 1);
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

    }

    private void InsetItem() {

        try {

            //서버에 전송한다.
            new AlertDialog.Builder(this).setMessage("등록할까요?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //POST 명령어 호출(업데이트를 적용한다)

                    mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(PItemListActivity.this, "",
                                    "잠시만 기다려 주세요.", true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 10000);
                        }
                    });
                    currentItem = new WoItem();
                    currentItem.WoNo = key;
                    currentItem.SeqNo = "";
                    currentItem.ItemName = editItemName.getText().toString();
                    currentItem.PersonCount = editPersonCount.getText().toString();
                    currentItem.Quantity = editQuantity.getText().toString();

                    new HttpAsyncTask().execute(getString(R.string.service_address)+"insertitem");

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            Log.i("mHandler", "mHandler");
            onResume();
        }
    };

    Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            Log.i("mHandler", "mHandler");
            onResume();
        }
    };

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();

            if (!currentItem.SeqNo.equals("false")) {
                itemList.add(currentItem);
            }
            mHandler2.sendEmptyMessage(0);
        }
    }

    private class HttpAsyncTaskDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            itemList.remove(removePosition);
            mHandler2.sendEmptyMessage(0);
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
            jsonObject.put("SupervisorWoNo", currentItem.WoNo);
            jsonObject.put("SeqNo", currentItem.SeqNo);
            jsonObject.put("ItemName", currentItem.ItemName);
            jsonObject.put("PersonCount", currentItem.PersonCount);
            jsonObject.put("Quantity", currentItem.Quantity);

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
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        Log.i("result", result.toString());
        mProgressDialog.dismiss();
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
            Log.i("JSON", result);

            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

            if (!resultCode.equals("false"))
                currentItem.SeqNo = resultCode;
        } catch (Exception ex) {
        }

        inputStream.close();
        return message + "(" + resultCode + ")";
    }

    private void makeImageList() {

        itemList = (ArrayList<WoItem>) getIntent().getSerializableExtra("data");
        items = new WoItem[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            items[i] = new WoItem(itemList.get(i).WoNo,
                    itemList.get(i).SeqNo,
                    itemList.get(i).ItemName,
                    itemList.get(i).PersonCount,
                    itemList.get(i).Quantity);
        }
    }

}

package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;
import com.kumkangkind.kumkangsm2.CustomerLocation.Location;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LocationTreeViewActivity extends BaseActivity {//트리뷰 엑티비티, 검색 X(기본)
    //진행층수 등록, 진행기준 정보관리, 일보작성, 현장불만사례

    TextView textView;

    int leftComplainDateArr[];
    int rightcomplainDateArr[];

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_tree);


        //현장 불만사례 날짜 셋팅을 위함
        Calendar cal = new GregorianCalendar();
        leftComplainDateArr = new int[3];
        leftComplainDateArr[0] = cal.get(Calendar.YEAR);
        leftComplainDateArr[1] = cal.get(Calendar.MONTH);
        leftComplainDateArr[2] = cal.get(Calendar.DATE);

        rightcomplainDateArr = new int[3];
        rightcomplainDateArr[0] = cal.get(Calendar.YEAR);
        rightcomplainDateArr[1] = cal.get(Calendar.MONTH);
        rightcomplainDateArr[2] = cal.get(Calendar.DATE);

        String programType = getIntent().getStringExtra("programType");
        textView = findViewById(R.id.textView);
        if (programType.equals("현장지원요청")) {
            textView.setText("지원받을 현장을 선택하세요");
        } else
            textView.setText(programType);

        HashMap<String, Customer> customerHashMap;
        TreeMap<String, Customer> customerTreeMap;
        customerHashMap = (HashMap<String, Customer>) (getIntent().getSerializableExtra("hashMap"));

        LinearLayout linearLayout = findViewById(R.id.layout);
        TreeNode root = TreeNode.root();//root
        Customer customer = null;

        customerTreeMap = new TreeMap<>(customerHashMap);
        List<Customer> list = new ArrayList<>(customerTreeMap.values());
        Collections.sort(list);//정렬


        for (Customer _customer : list) {
            customer = _customer;
            String customerName = customer.CustomerName;
            HashMap<String, Location> locationHashMap = customer.locationHashMap;

            ParentNode.IconTreeItem parentItem = new ParentNode.IconTreeItem();//부모 노드설정
            TreeNode parent = new TreeNode(parentItem).setViewHolder(new ParentNode(this, customerName));//부모노드설정

            Location location = null;
            for (Map.Entry<String, Location> locationEntry : locationHashMap.entrySet()) {

                location = locationEntry.getValue();
                String locationNo = location.LocationNo;
                String locationName = location.LocationName;
                String contractNo = location.ContractNo;
                MyHolder.IconTreeItem nodeItem = new MyHolder.IconTreeItem();//자식노드 설정
                TreeNode child1 = new TreeNode(nodeItem).setViewHolder(new MyHolder(this, locationNo, locationName, contractNo, customerName, programType, leftComplainDateArr, rightcomplainDateArr));//자식 노드설정
                parent.addChildren(child1);//parent 하위에 child 붙이기

            }

            root.addChild(parent);
        }


        AndroidTreeView tView = new AndroidTreeView(this, root);
        linearLayout.addView(tView.getView());

        progressOFF();//뷰다한다음에 이전페이지의 progressdialog 끄기
    }

    @Override
    public void onBackPressed() {
        if (textView.getText().toString().equals("일보작성")) {
            setResult(RESULT_OK);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}


class MyHolder extends TreeNode.BaseNodeViewHolder<MyHolder.IconTreeItem> {

    String locationNo;
    String locationName;
    String contractNo;
    String customerName;
    String programType;
    int leftComplainDateArr[];
    int rightComplainDateArr[];

    public MyHolder(Context context, String locationNo, String locationName, String contractNo, String customerName, String programType, int leftComplainDateArr[], int rightComplainDateArr[]) {
        super(context);
        this.programType = programType;
        this.locationNo = locationNo;
        this.locationName = locationName;
        this.contractNo = contractNo;
        this.customerName = customerName;

        this.leftComplainDateArr = leftComplainDateArr;
        this.rightComplainDateArr = rightComplainDateArr;
    }


    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(locationName);
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//현장 클릭시, 이벤트
                //contractNo 보내기
                if (programType.equals("진행층수등록"))
                    new GetDongProgessFloorByPost().execute(context.getString(R.string.service_address) + "getDongProgressFloor");
                else if (programType.equals("진행기준정보관리"))
                    new GetDongByPost(customerName, locationName).execute(context.getString(R.string.service_address) + "getDong");
                else if (programType.equals("일보작성"))
                    ShowRequestDailyReport();
                else if (programType.equals("현장불만사례"))
                    ShowComplainDialog();
                else if (programType.equals("현장지원요청"))
                    ShowSupportDialog();


            }
        });
        return view;
    }

    private void ShowSupportDialog() {


        SupportDialog supportDialog = new SupportDialog(context, contractNo, customerName, locationName);
        supportDialog.show();

    }

    private void ShowComplainDialog() {
        ComplainDialog complainDialog = new ComplainDialog(context, locationNo, customerName, locationName, leftComplainDateArr, rightComplainDateArr);
        complainDialog.show();


        complainDialog.setDialogResult(new ComplainDialog.OnDialogResult() {
            @Override
            public void finish(int[] dateArr) {
                //left 날짜 저장 갱신
                leftComplainDateArr = dateArr;
            }
        });


        complainDialog.setDialogResult2(new ComplainDialog.OnDialogResult() {
            @Override
            public void finish(int[] dateArr) {
                //right 날짜 저장 갱신
                rightComplainDateArr = dateArr;

            }
        });

    }

    private void ShowRequestDailyReport() {

        Intent i;
        i = new Intent(context, RegisterActivity2.class);
        i.putExtra("type", "작업");
        i.putExtra("key", "생성모드");
        i.putExtra("contractNo", contractNo);
        i.putExtra("customerLocation", customerName + "(" + locationName + ")");

        i.putExtra("customer", customerName);
        i.putExtra("location", locationName);

        context.startActivity(i);

    }

    public static class IconTreeItem {
        public int icon;
        public String text;
    }


    /*
     * 작업일보 생성 확인 누른후 처리: 음성용 작업일보 액티비티로 이동
     * */
 /*   Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };*/


    //담당자배정이랑 동일한 리스트뷰
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
                HashMap<String, Dong> dongHashMap = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                Dong dong = null;
                String key = "";
                String Dong = "";
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Dong = child.getString("Dong");
                    key = Dong;

                    if (!dongHashMap.containsKey(key)) {//없으면
                        dong = new Dong(child.getString("Dong"),
                                child.getString("ProgressYearMonth"), child.getString("ProgressFloor"), child.getString("ConstructionEmployee"));
                        dongHashMap.put(key, dong);
                    } else {//있으면: 전 달 데이터 setting
                        dong = dongHashMap.get(key);
                        dong.ExProgressYearMonth = child.getString("ProgressYearMonth");
                        dong.ExProgressFloor = child.getString("ProgressFloor");
                    }

                }

                Intent i = new Intent(context, ProgressFloorActivity.class);

                i.putExtra("customerLocation", customerName + "-" + locationName);
                i.putExtra("contractNo", contractNo);
                i.putExtra("dongHashMap", dongHashMap);
                context.startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class GetDongByPost extends AsyncTask<String, Void, String> {//todo

        String customerLocation = "";

        public GetDongByPost(String customerName, String locationName) {
            customerLocation = customerName + "-" + locationName;
        }

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {

                HashMap<String, String> dongHashMap = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    dongHashMap.put(child.getString("Dong"),
                            child.getString("ConstructionEmployee"));
                }

                Intent i;
                i = new Intent(context, DongPersonListViewActivity.class);


                i.putExtra("dongHashMap", dongHashMap);
                i.putExtra("customerLocation", customerLocation);
                i.putExtra("contractNo", contractNo);

                context.startActivity(i);

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

            //Delete & Insert
            jsonObject.put("ContractNo", contractNo);//계약번호
            jsonObject.put("Type1", "전체");

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
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }
}


class ParentNode extends TreeNode.BaseNodeViewHolder<ParentNode.IconTreeItem> {

    String customerName;

    public ParentNode(Context context, String customerName) {
        super(context);
        this.customerName = customerName;
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_parent_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(this.customerName);

        return view;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
    }
}
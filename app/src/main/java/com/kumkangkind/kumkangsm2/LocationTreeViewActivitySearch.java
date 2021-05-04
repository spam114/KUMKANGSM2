package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LocationTreeViewActivitySearch extends BaseActivity {//+검색
    //담당자 배정에서 사용
    EditText edtCustomerName;
    Button searchButton;
    boolean refreshToken=false;
    AndroidTreeView treeView;
    List<Customer> customerList;
    String type="";
    TextView textView;
    int leftComplainDateArr[];
    int rightcomplainDateArr[];
    String programType="";

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshToken=false;
        setContentView(R.layout.activity_location_tree_search);

        Calendar cal = new GregorianCalendar();
        leftComplainDateArr= new int[3];
        leftComplainDateArr[0]=cal.get(Calendar.YEAR);
        leftComplainDateArr[1]=cal.get(Calendar.MONTH);
        leftComplainDateArr[2]=cal.get(Calendar.DATE);

        rightcomplainDateArr= new int[3];
        rightcomplainDateArr[0]=cal.get(Calendar.YEAR);
        rightcomplainDateArr[1]=cal.get(Calendar.MONTH);
        rightcomplainDateArr[2]=cal.get(Calendar.DATE);

        programType= getIntent().getStringExtra("programType");
        textView=findViewById(R.id.textView);
        textView.setText(programType);
        type= getIntent().getStringExtra("type");
        RefreshTreeView(refreshToken);

        progressOFF();//앞단에서 불러온 progress 로딩완료후 끄기


    }

    private void SetListener(){
        edtCustomerName= findViewById(R.id.edtCustomerName);

        edtCustomerName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {//검색 버튼 클릭
            @Override
            public void onClick(View v) {
                refreshToken=true;
                RefreshTreeView(refreshToken);

            }
        });



    }

    /*
    * 트리뷰를 갱신한다.
    * */
    private void RefreshTreeView(boolean token){

        if(token==false) {//처음 불러올때
            HashMap<String, Customer> customerHashMap = (HashMap<String, Customer>) (getIntent().getSerializableExtra("hashMap"));

            customerList = new ArrayList<>(customerHashMap.values());
            Collections.sort(customerList);//정렬

            ViewTree(token);
        }

        else {//검색 할때

            new GetCustomerLocationForSearchByPost().execute(getString(R.string.service_address)+"getCustomerLocationForSearch");
            //customerHashMap = (HashMap<String, Customer>) (getIntent().getSerializableExtra("hashMap"));
        }

    }


    private void ViewTree(boolean refreshToken){
        LinearLayout treeLayout= findViewById(R.id.treeLayout);
        TreeNode root = TreeNode.root();//root
        Customer customer=null;

        searchButton =findViewById(R.id.searchBtn);
        SetListener();



        for (Customer _customer:customerList) {
            customer=_customer;
            String customerName=customer.CustomerName;
            HashMap<String, Location> locationHashMap=customer.locationHashMap;

            ParentNode2.IconTreeItem parentItem = new ParentNode2.IconTreeItem();//부모 노드설정
            TreeNode parent = new TreeNode(parentItem).setViewHolder(new ParentNode2(this,customerName));//부모노드설정

            Location location=null;
            for (Map.Entry<String,Location> locationEntry:locationHashMap.entrySet()) {
                if(programType.equals("현장불만사례"))
                    type="현장불만사례";
                location=locationEntry.getValue();
                String locationNo=location.LocationNo;
                String locationName=location.LocationName;
                String contractNo=location.ContractNo;
                MyHolder2.IconTreeItem nodeItem = new MyHolder2.IconTreeItem();//자식노드 설정
                TreeNode child1 = new TreeNode(nodeItem).setViewHolder(new MyHolder2(this,locationNo,locationName,contractNo, customerName, type,leftComplainDateArr,rightcomplainDateArr));//자식 노드설정
                parent.addChildren(child1);//parent 하위에 child 붙이기

            }

            root.addChild(parent);
        }

        treeView = new AndroidTreeView(this, root);
        if(refreshToken==true) {//검색후 초기화 부분
            treeLayout.removeAllViews();
            treeView.setDefaultAnimation(true);
            treeView.setDefaultViewHolder(MyHolder2.class);
            treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        }
        treeLayout.addView(treeView.getView());
    }

    private class GetCustomerLocationForSearchByPost extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return POST2(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                HashMap<String, Customer> customerHashMap;
                JSONArray jsonArray = new JSONArray(result);

                customerHashMap= new HashMap<>();
                Customer customer=null;
                String key=null;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    key=child.getString("CustomerCode");

                    if(!customerHashMap.containsKey(key)){//없으면
                        customer = new Customer(child.getString("CustomerCode"),
                                child.getString("CustomerName"));
                        customerHashMap.put(key,customer);
                    }
                    else{//있으면
                        customer=customerHashMap.get(key);
                    }
                    customer.addData(child.getString("LocationNo"),child.getString("LocationName"),child.getString("ContractNo"));
                }

                customerList= new ArrayList<>(customerHashMap.values());
                Collections.sort(customerList);

                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();
                ViewTree(refreshToken);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String POST2(String url){


        String customerName=edtCustomerName.getText().toString();
        if(customerName.equals(""))
            customerName="-1";

        String userCode="";
        if(type.equals("담당자배정")){
            userCode=Users.USER_ID;
        }
        else{
            userCode="모든현장";
        }

        //todo

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
            jsonObject.put("SupervisorCode",userCode);
            jsonObject.put("CustomerName", customerName);

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
        return  result;
    }
}


class MyHolder2 extends TreeNode.BaseNodeViewHolder<MyHolder2.IconTreeItem> {

    String locationNo;
    String locationName;
    String contractNo;
    String customerName;
    String type;
    Context context;

    int leftComplainDateArr[];
    int rightcomplainDateArr[];
    // ProgressDialog mProgressDialog;

    public MyHolder2(Context context, String locationNo,String locationName, String contractNo, String customerName, String type,int leftComplainDateArr[],  int rightcomplainDateArr[]) {
        super(context);
        this.context=context;
        this.locationNo=locationNo;
        this.locationName=locationName;
        this.contractNo=contractNo;
        this.customerName=customerName;
        this.type=type;
        this.leftComplainDateArr=leftComplainDateArr;
        this.rightcomplainDateArr=rightcomplainDateArr;
    }


    @Override
    public View createNodeView(TreeNode node, MyHolder2.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(locationName);
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contractNo 보내기
                //new GetDongProgessFloorByPost().execute(getString(R.string.service_address)+"getDongProgressFloor");
                if(type.equals("현장불만사례")){
                    ComplainDialog complainDialog = new ComplainDialog(context, locationNo,customerName,locationName,leftComplainDateArr, rightcomplainDateArr);

                    //window.setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


                    WindowManager.LayoutParams params = complainDialog.getWindow().getAttributes();
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    complainDialog.getWindow().setAttributes(params);

                    complainDialog.show();


                    complainDialog.setDialogResult(new ComplainDialog.OnDialogResult() {
                        @Override
                        public void finish(int[] dateArr) {
                            //left 날짜 저장 갱신
                            leftComplainDateArr=dateArr;
                        }
                    });


                    complainDialog.setDialogResult2(new ComplainDialog.OnDialogResult() {
                        @Override
                        public void finish(int[] dateArr) {
                            //right 날짜 저장 갱신
                            rightcomplainDateArr=dateArr;

                        }
                    });
                }


                else{//담당자배정, 담당자배정현황
                    new GetDongByPost(customerName, locationName).execute(context.getString(R.string.service_address)+"getDong");
                }

            }
        });
        return view;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
    }




    private class GetDongByPost extends AsyncTask<String, Void, String> {//todo

        String customerLocation="";

        public GetDongByPost(String customerName,String locationName){
            customerLocation=customerName+"-"+locationName;
        }

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {

                HashMap<String, String> dongHashMap= new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    dongHashMap.put(child.getString("Dong"),
                            child.getString("ConstructionEmployee"));
                }

                Intent i;

                if(type.equals("담당자배정")){
                    i = new Intent(context, AssignmentActivity.class);
                }
                else{//담당자배정현황
                    i = new Intent(context, AssignmentStatusActivity.class);
                }


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
            jsonObject.put("ContractNo",contractNo);//계약번호
            jsonObject.put("Type1","전체");

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
        return  result;
    }
}



class ParentNode2 extends TreeNode.BaseNodeViewHolder<ParentNode2.IconTreeItem> {

    String customerName;

    public ParentNode2(Context context, String customerName) {
        super(context);
        this.customerName=customerName;
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
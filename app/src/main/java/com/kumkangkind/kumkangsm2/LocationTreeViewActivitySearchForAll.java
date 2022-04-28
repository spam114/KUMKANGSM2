package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.CustomerLocation.Customer;
import com.kumkangkind.kumkangsm2.CustomerLocation.Location;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationTreeViewActivitySearchForAll extends BaseActivity {//+검색
    //담당자 배정에서 사용
    EditText edtCustomerName;
    Button searchButton;
    boolean refreshToken = false;
    AndroidTreeView treeView;
    List<Customer> customerList;
    String type = "";
    TextView textView;
    int leftComplainDateArr[];
    int rightcomplainDateArr[];
    String programType = "";
    TextView textView13;

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
        refreshToken = false;
        setContentView(R.layout.activity_location_tree_search);

        Calendar cal = new GregorianCalendar();
        leftComplainDateArr = new int[3];
        leftComplainDateArr[0] = cal.get(Calendar.YEAR);
        leftComplainDateArr[1] = cal.get(Calendar.MONTH);
        leftComplainDateArr[2] = cal.get(Calendar.DATE);

        rightcomplainDateArr = new int[3];
        rightcomplainDateArr[0] = cal.get(Calendar.YEAR);
        rightcomplainDateArr[1] = cal.get(Calendar.MONTH);
        rightcomplainDateArr[2] = cal.get(Calendar.DATE);

        programType = getIntent().getStringExtra("programType");
        textView = findViewById(R.id.textView);
        textView13 = findViewById(R.id.textView13);
        textView.setText(programType);
        textView13.setText("검색: ");
        type = getIntent().getStringExtra("type");
        RefreshTreeView(refreshToken);


    }

    private void SetListener() {
        edtCustomerName = findViewById(R.id.edtCustomerName);

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
                refreshToken = true;
                RefreshTreeView(refreshToken);

            }
        });


    }

    /*
     * 트리뷰를 갱신한다.
     * */
    private void RefreshTreeView(boolean token) {

        if (token == false) {//처음 불러올때
            getCustomerLocationForAll("-1");
        } else {//검색 할때

            getCustomerLocationForAll(edtCustomerName.getText().toString());
        }

    }

    private void getCustomerLocationForAll(String searchString) {
        String url = getString(R.string.service_address) + "getCustomerLocationForAll";
        ContentValues values = new ContentValues();
        values.put("SearchString", searchString);
        GetCustomerLocationForAll gsod = new GetCustomerLocationForAll(url, values);
        gsod.execute();
    }

    public class GetCustomerLocationForAll extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCustomerLocationForAll(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
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
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                HashMap<String, Customer> customerHashMap;
                customerHashMap = new HashMap<>();
                Customer customer = null;
                String key = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    key = child.getString("CustomerCode");

                    if (!customerHashMap.containsKey(key)) {//없으면
                        customer = new Customer(child.getString("CustomerCode"),
                                child.getString("CustomerName"));
                        customerHashMap.put(key, customer);
                    } else {//있으면
                        customer = customerHashMap.get(key);
                    }
                    customer.addData2(child.getString("LocationNo"), child.getString("LocationName"));
                }
                customerList = new ArrayList<>(customerHashMap.values());
                Collections.sort(customerList);

                //Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();
                ViewTree(refreshToken);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    private void ViewTree(boolean refreshToken) {
        LinearLayout treeLayout = findViewById(R.id.treeLayout);
        TreeNode root = TreeNode.root();//root
        Customer customer = null;

        searchButton = findViewById(R.id.searchBtn);
        SetListener();


        for (Customer _customer : customerList) {
            customer = _customer;
            String customerName = customer.CustomerName;
            HashMap<String, Location> locationHashMap = customer.locationHashMap;

            ParentNode3.IconTreeItem parentItem = new ParentNode3.IconTreeItem();//부모 노드설정
            TreeNode parent = new TreeNode(parentItem).setViewHolder(new ParentNode3(this, customerName));//부모노드설정

            Location location = null;
            for (Map.Entry<String, Location> locationEntry : locationHashMap.entrySet()) {
                location = locationEntry.getValue();
                String locationNo = location.LocationNo;
                String locationName = location.LocationName;
                MyHolder3.IconTreeItem nodeItem = new MyHolder3.IconTreeItem();//자식노드 설정
                TreeNode child1 = new TreeNode(nodeItem).setViewHolder(new MyHolder3(this, locationNo, locationName, customerName, type, leftComplainDateArr, rightcomplainDateArr));//자식 노드설정
                parent.addChildren(child1);//parent 하위에 child 붙이기
            }
            root.addChild(parent);
        }


        treeView = new AndroidTreeView(this, root);
        if (refreshToken == true) {//검색후 초기화 부분
            treeLayout.removeAllViews();
            treeView.setDefaultAnimation(true);
            treeView.setDefaultViewHolder(MyHolder3.class);
            treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        }
        treeLayout.addView(treeView.getView());
    }
}


class MyHolder3 extends TreeNode.BaseNodeViewHolder<MyHolder3.IconTreeItem> {

    String locationNo;
    String locationName;
    String customerName;
    String type;
    Context context;

    int leftComplainDateArr[];
    int rightcomplainDateArr[];
    // ProgressDialog mProgressDialog;

    public MyHolder3(Context context, String locationNo, String locationName, String customerName, String type, int leftComplainDateArr[], int rightcomplainDateArr[]) {
        super(context);
        this.context = context;
        this.locationNo = locationNo;
        this.locationName = locationName;
        this.customerName = customerName;
        this.type = type;
        this.leftComplainDateArr = leftComplainDateArr;
        this.rightcomplainDateArr = rightcomplainDateArr;
    }


    @Override
    public View createNodeView(TreeNode node, MyHolder3.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(locationName);
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문내역 Activity
                Intent i;
                i = new Intent(context, SaleOrderActivity.class);
                i.putExtra("customerName", customerName);
                i.putExtra("locationNo", locationNo);
                i.putExtra("locationName", locationName);

                context.startActivity(i);
            }
        });
        return view;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
    }
}


class ParentNode3 extends TreeNode.BaseNodeViewHolder<ParentNode3.IconTreeItem> {

    String customerName;

    public ParentNode3(Context context, String customerName) {
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
package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Adapter.ProductionProgressAdapter;
import com.kumkangkind.kumkangsm2.Object.ProductionProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductionProgressActivity extends BaseActivity {

    TextView txtTitle;
    TextView txtCustomerLoaction;
    TextView txtRemark;
    ListView listview;
    ArrayList<ProductionProgress> productionProgressArrayList;
    ProductionProgressAdapter productionProgressAdapter;
    String saleOrderNo;
    String remark;
    String customerName;
    String locationName;
    String worderRequestNo;
    //TextView txtSaleOrderNo;

//FragmentViewSaleOrder fragmentViewSaleOrder;

//1.앞쪽에서 선택한 데이터를 가져온다.
//2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
//1번과 2번 데이터를 합쳐서
//화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_progress);
        listview = findViewById(R.id.listview);
        txtTitle=findViewById(R.id.txtTitle);
        txtCustomerLoaction=findViewById(R.id.txtCustomerLocation);
        txtRemark=findViewById(R.id.txtRemark);
        saleOrderNo = getIntent().getStringExtra("saleOrderNo");
        remark = getIntent().getStringExtra("remark");
        customerName = getIntent().getStringExtra("customerName");
        locationName = getIntent().getStringExtra("locationName");
        worderRequestNo = getIntent().getStringExtra("worderRequestNo");
        txtTitle.setText("생산 진행현황("+saleOrderNo+")");
        txtCustomerLoaction.setText(customerName+"("+locationName+")");
        txtRemark.setText(remark);
        if(remark.equals(""))
            txtRemark.setVisibility(View.GONE);
        else
            txtRemark.setVisibility(View.VISIBLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getProductionProgress();
    }


    private void getProductionProgress() {
        String url = getString(R.string.service_address) + "getProductionProgress";
        ContentValues values = new ContentValues();
        values.put("WorderRequestNo", worderRequestNo);
        GetProductionProgress gsod = new GetProductionProgress(url, values);
        gsod.execute();
    }


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

    public class GetProductionProgress extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetProductionProgress(String url, ContentValues values) {
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
            double initTotalAmt = 0;
            double initTotalWeight = 0;
            try {
                ProductionProgress productionProgress;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                productionProgressArrayList = new ArrayList<>();
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                //double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ProductionProgressActivity.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    productionProgress = new ProductionProgress();
                    productionProgress.PartName = child.getString("PartName");
                    productionProgress.PartSpec = child.getString("PartSpec");
                    productionProgress.Mspec = child.getString("Mspec");
                    productionProgress.OrderQty = child.getString("OrderQty");
                    productionProgress.AllocQty = child.getString("AllocQty");
                    productionProgress.ProcessQty = child.getString("ProcessQty");
                    productionProgress.InstructionQty = child.getString("InstructionQty");
                    productionProgress.ProductionQty = child.getString("ProductionQty");
                    productionProgress.PackingQty = child.getString("PackingQty");
                    productionProgress.OutQty = child.getString("OutQty");
                    productionProgress.SeqNo = child.getString("SeqNo");

                    productionProgressArrayList.add(productionProgress);

                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }
                productionProgressAdapter = new ProductionProgressAdapter
                        (ProductionProgressActivity.this, R.layout.listview_production_progress_row, productionProgressArrayList, listview, worderRequestNo);
                listview.setAdapter(productionProgressAdapter);
                //txtTotal.setText("Why");

              /*  partNameSequences = new CharSequence[partNameDic.size() + 1];
                partNameSequences[0] = "전체";
                for (int i = 1; i < partNameDic.size() + 1; i++) {
                    partNameSequences[i] = partNameDic.get(i - 1);
                }

               *//* partSpecNameSequences = new CharSequence[partSpecNameDic.size()+1];
                partSpecNameSequences[0] ="전체";
                for (int i = 1; i < partSpecNameDic.size()+1; i++) {
                    partSpecNameSequences[i] = partSpecNameDic.get(i-1);
                }*//*

                availablePartAdapter = new AvailablePartAdapter
                        (SearchAvailablePartActivity.this, R.layout.listview_available_part, stockArrayList, txtBadge);*/


            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }
}
package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Object.LocationProgress2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationProgressActivity2 extends BaseActivity {

    //ListView listview;
    ArrayList<LocationProgress2> locationProgressArrayList;
    //LocationProgressAdapter2 locationProgressAdapter2;
    TextView txtTitle;
    ArrayList<String> typeList;
    ImageView imvRefresh;
    //ArrayList<String> calList;
    boolean firstFlag = true;
    String locationNo;
    String customerLocation;

    TextView AlName;
    TextView al_outWeight_g;
    TextView al_inWeight;
    TextView al_over;
    TextView al_rate;
    TextView GaugeDate;

    TextView StName;
    TextView st_outWeight;
    TextView st_inWeight;
    TextView st_rate;
    TextView GaugeDate2;

    TextView OtName;
    TextView ot_outWeight;
    TextView ot_inWeight;
    TextView ot_rate;
    TextView GaugeDate4;

    TextView SpName;
    TextView sp_outQty;
    TextView sp_inQty;
    TextView sp_qrate;
    TextView GaugeDate6;

    TextView KdspName;
    TextView kdsp_outQty;
    TextView kdsp_inQty;
    TextView kdsp_qrate;
    TextView GaugeDate8;

    TextView BeamName;
    TextView kb_outQty;
    TextView kb_inQty;
    TextView kb_qrate;
    TextView GaugeDate7;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_progress2);
        //listview = findViewById(R.id.listview);
        txtTitle = findViewById(R.id.txtTitle);
        imvRefresh = findViewById(R.id.imvRefresh);

        AlName = findViewById(R.id.AlName);
        al_outWeight_g = findViewById(R.id.al_outWeight_g);
        al_inWeight = findViewById(R.id.al_inWeight);
        al_over = findViewById(R.id.al_over);
        al_rate = findViewById(R.id.al_rate);
        GaugeDate = findViewById(R.id.GaugeDate);

        StName = findViewById(R.id.StName);
        st_outWeight = findViewById(R.id.st_outWeight);
        st_inWeight = findViewById(R.id.st_inWeight);
        st_rate = findViewById(R.id.st_rate);
        GaugeDate2 = findViewById(R.id.GaugeDate2);

        OtName = findViewById(R.id.OtName);
        ot_outWeight = findViewById(R.id.ot_outWeight);
        ot_inWeight = findViewById(R.id.ot_inWeight);
        ot_rate = findViewById(R.id.ot_rate);
        GaugeDate4 = findViewById(R.id.GaugeDate4);

        SpName = findViewById(R.id.SpName);
        sp_outQty = findViewById(R.id.sp_outQty);
        sp_inQty = findViewById(R.id.sp_inQty);
        sp_qrate = findViewById(R.id.sp_qrate);
        GaugeDate6 = findViewById(R.id.GaugeDate6);

        KdspName = findViewById(R.id.KdspName);
        kdsp_outQty = findViewById(R.id.kdsp_outQty);
        kdsp_inQty = findViewById(R.id.kdsp_inQty);
        kdsp_qrate = findViewById(R.id.kdsp_qrate);
        GaugeDate8 = findViewById(R.id.GaugeDate8);

        BeamName = findViewById(R.id.BeamName);
        kb_outQty = findViewById(R.id.kb_outQty);
        kb_inQty = findViewById(R.id.kb_inQty);
        kb_qrate = findViewById(R.id.kb_qrate);
        GaugeDate7 = findViewById(R.id.GaugeDate7);

        locationNo = getIntent().getStringExtra("locationNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        //spinnerCal = findViewById(R.id.spinnerCal);
        txtTitle.setText(customerLocation);


        /*spinnerCal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLocationProgress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationProgress();
            }
        });
        getLocationProgress();
    }

    private void setField(int position) {

        /*locationProgressAdapter2 = new LocationProgressAdapter2
                (LocationProgressActivity2.this, R.layout.listview_location_progress_row2, locationProgressArrayList, spinnerType.getSelectedItemPosition());
        listview.setAdapter(locationProgressAdapter2);*/


    }

    private void getLocationProgress() {
        String sType = "1";//정산전 으로 셋팅 2일시 정산완료
    /*    int type = spinnerType.getSelectedItemPosition();
        String sType = "-1";
        if (type == 0)
            sType = "1";
        else
            sType = "2";*/
        //int calType = spinnerCal.getSelectedItemPosition();
        /*if (requestPosition == 0)
            requestType = "-1";
        else if (requestPosition == 1)
            requestType = "S";
        else if (requestPosition == 2)
            requestType = "P";
        else if (requestPosition == 3)
            requestType = "R";
        else if (requestPosition == 4)
            requestType = "C";
        else
            requestType = "A";*/


        String url = getString(R.string.service_address) + "getUserStockInReport2";
        ContentValues values = new ContentValues();
        values.put("SupervisorCode", Users.USER_ID);
        values.put("Type", sType);
        values.put("LocationNo", locationNo);
        GetUserStockInReport2 gsod = new GetUserStockInReport2(url, values);
        gsod.execute();
    }


    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 30000);
        progressON("Loading...", handler);
    }

    public class GetUserStockInReport2 extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetUserStockInReport2(String url, ContentValues values) {
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
                //dongDic = new ArrayList<>();
                //LocationProgress2 locationProgress;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //locationProgressArrayList = new ArrayList<>();
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                //double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(LocationProgressActivity2.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(LocationProgressActivity.this, ErrorCheck, 2);
                        return;
                    }

                    AlName.setText(child.getString("AlName"));
                    al_outWeight_g.setText(child.getString("al_outWeight_g"));
                    al_inWeight.setText(child.getString("al_inWeight"));
                    al_over.setText(child.getString("al_over"));
                    al_rate.setText(child.getString("al_rate"));
                    GaugeDate.setText(child.getString("GaugeDate"));

                    StName.setText(child.getString("StName"));
                    st_outWeight.setText(child.getString("st_outWeight"));
                    st_inWeight.setText(child.getString("st_inWeight"));
                    st_rate.setText(child.getString("st_rate"));
                    GaugeDate2.setText(child.getString("GaugeDate2"));

                    OtName.setText(child.getString("OtName"));
                    ot_outWeight.setText(child.getString("ot_outWeight"));
                    ot_inWeight.setText(child.getString("ot_inWeight"));
                    ot_rate.setText(child.getString("ot_rate"));
                    GaugeDate4.setText(child.getString("GaugeDate4"));

                    SpName.setText(child.getString("SpName"));
                    sp_outQty.setText(child.getString("sp_outQty")+"("+child.getString("sp_outWeight_g")+")");
                    sp_inQty.setText(child.getString("sp_inQty")+"("+child.getString("sp_inWeight_g")+")");
                    sp_qrate.setText(child.getString("sp_qrate"));
                    GaugeDate6.setText(child.getString("GaugeDate6"));

                    KdspName.setText(child.getString("KdspName"));
                    kdsp_outQty.setText(child.getString("kdsp_outQty")+"("+child.getString("kdsp_outWeight_g")+")");
                    kdsp_inQty.setText(child.getString("kdsp_inQty")+"("+child.getString("kdsp_inWeight_g")+")");
                    kdsp_qrate.setText(child.getString("kdsp_qrate"));
                    GaugeDate8.setText(child.getString("GaugeDate8"));

                    BeamName.setText(child.getString("BeamName"));
                    kb_outQty.setText(child.getString("kb_outQty")+"("+child.getString("kb_outWeight_g")+")");
                    kb_inQty.setText(child.getString("kb_inQty")+"("+child.getString("kb_inWeight")+")");
                    kb_qrate.setText(child.getString("kb_qrate"));
                    GaugeDate7.setText(child.getString("GaugeDate7"));


                    //locationProgressArrayList.add(locationProgress);
                }
                /*locationProgressAdapter2 = new LocationProgressAdapter2
                        (LocationProgressActivity2.this, R.layout.listview_location_progress_row2, locationProgressArrayList, spinnerType.getSelectedItemPosition());*/
                //listview.setAdapter(locationProgressAdapter2);
                //spinnerType.setSelection(0);
                firstFlag = false;
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

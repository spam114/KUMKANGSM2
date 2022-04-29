package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
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

    TextView txtAlName;
    TextView txtAl_outWeight_g;
    TextView txtAl_outWeight;
    TextView txtAl_inWeight;
    TextView txtAl_over;
    TextView txtGaugeDate;

    TextView txtStName;
    TextView txtSt_outWeight_g;
    TextView txtSt_outWeight;
    TextView txtSt_inWeight;
    TextView txtGaugeDate2;

    TextView txtOtName;
    TextView txtOt_outWeight_g;
    TextView txtOt_outWeight;
    TextView txtOt_inWeight;
    TextView txtGaugeDate4;

    TextView txtSpName;
    TextView txtSp_outWeight_g;
    TextView txtSp_outWeight;
    TextView txtSp_inWeight_g;
    TextView txtGaugeDate6;

    TextView txtKdspName;
    TextView txtKdsp_outWeight_g;
    TextView txtKdsp_outWeight;
    TextView txtKdsp_inWeight_g;
    TextView txtGaugeDate7;

    TextView txtBeamName;
    TextView txtKb_outWeight_g;
    TextView txtKb_inWeight;
    TextView txtGaugeDate8;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_progress2);
        //listview = findViewById(R.id.listview);
        txtTitle = findViewById(R.id.txtTitle);
        imvRefresh = findViewById(R.id.imvRefresh);

        txtAlName = findViewById(R.id.txtAlName);
        txtAl_outWeight_g = findViewById(R.id.txtAl_outWeight_g);
        txtAl_outWeight = findViewById(R.id.txtAl_outWeight);
        txtAl_inWeight = findViewById(R.id.txtAl_inWeight);
        txtAl_over = findViewById(R.id.txtAl_over);
        txtGaugeDate = findViewById(R.id.txtGaugeDate);

        txtStName = findViewById(R.id.txtStName);
        txtSt_outWeight_g = findViewById(R.id.txtSt_outWeight_g);
        txtSt_outWeight = findViewById(R.id.txtSt_outWeight);
        txtSt_inWeight = findViewById(R.id.txtSt_inWeight);
        txtGaugeDate2 = findViewById(R.id.txtGaugeDate2);

        txtOtName = findViewById(R.id.txtOtName);
        txtOt_outWeight_g = findViewById(R.id.txtOt_outWeight_g);
        txtOt_outWeight = findViewById(R.id.txtOt_outWeight);
        txtOt_inWeight = findViewById(R.id.txtOt_inWeight);
        txtGaugeDate4 = findViewById(R.id.txtGaugeDate4);

        txtSpName = findViewById(R.id.txtSpName);
        txtSp_outWeight_g = findViewById(R.id.txtSp_outWeight_g);
        txtSp_outWeight = findViewById(R.id.txtSp_outWeight);
        txtSp_inWeight_g = findViewById(R.id.txtSp_inWeight_g);
        txtGaugeDate6 = findViewById(R.id.txtGaugeDate6);

        txtKdspName = findViewById(R.id.txtKdspName);
        txtKdsp_outWeight_g = findViewById(R.id.txtKdsp_outWeight_g);
        txtKdsp_outWeight = findViewById(R.id.txtKdsp_outWeight);
        txtKdsp_inWeight_g = findViewById(R.id.txtKdsp_inWeight_g);
        txtGaugeDate7 = findViewById(R.id.txtGaugeDate7);

        txtBeamName = findViewById(R.id.txtBeamName);
        txtKb_outWeight_g = findViewById(R.id.txtKb_outWeight_g);
        txtKb_inWeight = findViewById(R.id.txtKb_inWeight);
        txtGaugeDate8 = findViewById(R.id.txtGaugeDate8);

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

                    txtAlName.setText(child.getString("AlName"));
                    txtAl_outWeight_g.setText(child.getString("al_outWeight_g"));
                    txtAl_outWeight.setText(child.getString("al_outWeight"));
                    txtAl_inWeight.setText(child.getString("al_inWeight"));
                    txtAl_over.setText(child.getString("al_over"));
                    txtGaugeDate.setText(child.getString("GaugeDate"));
                    txtStName.setText(child.getString("StName"));
                    txtSt_outWeight_g.setText(child.getString("st_outWeight_g"));
                    txtSt_outWeight.setText(child.getString("st_outWeight"));
                    txtSt_inWeight.setText(child.getString("st_inWeight"));
                    txtGaugeDate2.setText(child.getString("GaugeDate2"));
                    txtOtName.setText(child.getString("OtName"));
                    txtOt_outWeight_g.setText(child.getString("ot_outWeight_g"));
                    txtOt_outWeight.setText(child.getString("ot_outWeight"));
                    txtOt_inWeight.setText(child.getString("ot_inWeight"));
                    txtGaugeDate4.setText(child.getString("GaugeDate4"));
                    txtSpName.setText(child.getString("SpName"));
                    txtSp_outWeight_g.setText(child.getString("sp_outWeight_g"));
                    txtSp_outWeight.setText(child.getString("sp_outWeight"));
                    txtSp_inWeight_g.setText(child.getString("sp_inWeight_g"));
                    txtGaugeDate6.setText(child.getString("GaugeDate6"));

                    txtKdspName.setText(child.getString("KdspName"));
                    txtKdsp_outWeight_g.setText(child.getString("kdsp_outWeight_g"));
                    txtKdsp_outWeight.setText(child.getString("kdsp_outWeight"));
                    txtKdsp_inWeight_g.setText(child.getString("kdsp_inWeight_g"));
                    txtGaugeDate8.setText(child.getString("GaugeDate8"));


                    txtBeamName.setText(child.getString("BeamName"));
                    txtKb_outWeight_g.setText(child.getString("kb_outWeight_g"));
                    txtKb_inWeight.setText(child.getString("kb_inWeight"));
                    txtGaugeDate7.setText(child.getString("GaugeDate7"));


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

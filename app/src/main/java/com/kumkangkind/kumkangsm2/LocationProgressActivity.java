package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Adapter.LocationProgressAdapter;
import com.kumkangkind.kumkangsm2.Object.LocationProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationProgressActivity extends BaseActivity {

    ListView listview;
    ArrayList<LocationProgress> locationProgressArrayList;
    LocationProgressAdapter locationProgressAdapter;
    Spinner spinnerType;
    //Spinner spinnerCal;
    TextView txtTitle;
    ArrayList<String> typeList;
    ImageView imvRefresh;
    //ArrayList<String> calList;
    boolean firstFlag = true;
    //Filter filter;//검색 필터
    //ArrayList<String> dongDic;//동 검색을 위한 리스트
    //CharSequence[] dongSequences;
    int selectedIndex = 0;
    //TextView txtLocationProgressNo;

    TextView txt19;
    TextView txt20;
    TextView txt21;
    TextView txt22;
    TextView txt23;
    TextView txt24;
    TextView txt25;
    TextView txt26;
    TextView txt27;
    TextView txt28;
    TextView txt29;
    TextView txt30;
    TextView txt31;
    TextView txt32;
    TextView txt33;
    TextView txt34;
    TextView txt35;
    TextView txt36;
    TextView txt37;
    TextView txt38;
    TextView txt39;
    TextView txt40;
    TextView txt41;
    TextView txt42;
    TextView txt43;
    TextView txt44;
    TextView txt45;
    TextView txt46;
    TextView txt47;
    TextView txt48;
    TextView txt49;
    TextView txt50;
    TextView txt51;
    TextView txt52;

//FragmentViewLocationProgress fragmentViewLocationProgress;

//1.앞쪽에서 선택한 데이터를 가져온다.
//2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
//1번과 2번 데이터를 합쳐서
//화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_progress);
        listview = findViewById(R.id.listview);
        txtTitle = findViewById(R.id.txtTitle);
        spinnerType = findViewById(R.id.spinnerType);
        imvRefresh = findViewById(R.id.imvRefresh);
        //spinnerCal = findViewById(R.id.spinnerCal);
        txtTitle.setText("현장 진행현황 [" + Users.UserName + "]");
        txt19 = findViewById(R.id.txt19);
        txt20 = findViewById(R.id.txt20);
        txt21 = findViewById(R.id.txt21);
        txt22 = findViewById(R.id.txt22);
        txt23 = findViewById(R.id.txt23);
        txt24 = findViewById(R.id.txt24);
        txt25 = findViewById(R.id.txt25);
        txt26 = findViewById(R.id.txt26);
        txt27 = findViewById(R.id.txt27);
        txt28 = findViewById(R.id.txt28);
        txt29 = findViewById(R.id.txt29);
        txt30 = findViewById(R.id.txt30);
        txt31 = findViewById(R.id.txt31);
        txt32 = findViewById(R.id.txt32);
        txt33 = findViewById(R.id.txt33);
        txt34 = findViewById(R.id.txt34);
        txt35 = findViewById(R.id.txt35);
        txt36 = findViewById(R.id.txt36);
        txt37 = findViewById(R.id.txt37);
        txt38 = findViewById(R.id.txt38);
        txt39 = findViewById(R.id.txt39);
        txt40 = findViewById(R.id.txt40);
        txt41 = findViewById(R.id.txt41);
        txt42 = findViewById(R.id.txt42);
        txt43 = findViewById(R.id.txt43);
        txt44 = findViewById(R.id.txt44);
        txt45 = findViewById(R.id.txt45);
        txt46 = findViewById(R.id.txt46);
        txt47 = findViewById(R.id.txt47);
        txt48 = findViewById(R.id.txt48);
        txt49 = findViewById(R.id.txt49);
        txt50 = findViewById(R.id.txt50);
        txt51 = findViewById(R.id.txt51);
        txt52 = findViewById(R.id.txt52);
        setSpinnerData();

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //필드 정렬
                //getLocationProgress();
                setField(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        if (firstFlag)
            return;

        txt19.setVisibility(View.GONE);
        txt20.setVisibility(View.GONE);
        txt21.setVisibility(View.GONE);
        txt22.setVisibility(View.GONE);
        txt23.setVisibility(View.GONE);
        txt24.setVisibility(View.GONE);

        txt25.setVisibility(View.GONE);
        txt26.setVisibility(View.GONE);
        txt27.setVisibility(View.GONE);
        txt28.setVisibility(View.GONE);
        txt29.setVisibility(View.GONE);

        txt30.setVisibility(View.GONE);
        txt31.setVisibility(View.GONE);
        txt32.setVisibility(View.GONE);

        txt33.setVisibility(View.GONE);
        txt34.setVisibility(View.GONE);
        txt35.setVisibility(View.GONE);
        txt36.setVisibility(View.GONE);
        txt37.setVisibility(View.GONE);
        txt38.setVisibility(View.GONE);
        txt39.setVisibility(View.GONE);

        txt40.setVisibility(View.GONE);
        txt41.setVisibility(View.GONE);
        txt42.setVisibility(View.GONE);
        txt43.setVisibility(View.GONE);
        txt44.setVisibility(View.GONE);
        txt45.setVisibility(View.GONE);
        txt46.setVisibility(View.GONE);

        txt47.setVisibility(View.GONE);
        txt48.setVisibility(View.GONE);
        txt49.setVisibility(View.GONE);
        txt50.setVisibility(View.GONE);
        txt51.setVisibility(View.GONE);
        txt52.setVisibility(View.GONE);
        if (position == 0) {//알폼
            txt19.setVisibility(View.VISIBLE);
            txt20.setVisibility(View.VISIBLE);
            txt21.setVisibility(View.VISIBLE);
            txt22.setVisibility(View.VISIBLE);
            txt23.setVisibility(View.VISIBLE);
            txt24.setVisibility(View.VISIBLE);
        } else if (position == 1) {//스틸
            txt25.setVisibility(View.VISIBLE);
            txt26.setVisibility(View.VISIBLE);
            txt27.setVisibility(View.VISIBLE);
            txt28.setVisibility(View.VISIBLE);
            txt29.setVisibility(View.VISIBLE);
        } else if (position == 2) {//부속철물
            txt30.setVisibility(View.VISIBLE);
            txt31.setVisibility(View.VISIBLE);
            txt32.setVisibility(View.VISIBLE);
        } else if (position == 3) {//AL서포트
            txt33.setVisibility(View.VISIBLE);
            txt34.setVisibility(View.VISIBLE);
            txt35.setVisibility(View.VISIBLE);
            txt36.setVisibility(View.VISIBLE);
            txt37.setVisibility(View.VISIBLE);
            txt38.setVisibility(View.VISIBLE);
            txt39.setVisibility(View.VISIBLE);
        } else if (position == 4) {//KD서포트
            txt40.setVisibility(View.VISIBLE);
            txt41.setVisibility(View.VISIBLE);
            txt42.setVisibility(View.VISIBLE);
            txt43.setVisibility(View.VISIBLE);
            txt44.setVisibility(View.VISIBLE);
            txt45.setVisibility(View.VISIBLE);
            txt46.setVisibility(View.VISIBLE);
        } else if (position == 5) {//KS-BEAM
            txt47.setVisibility(View.VISIBLE);
            txt48.setVisibility(View.VISIBLE);
            txt49.setVisibility(View.VISIBLE);
            txt50.setVisibility(View.VISIBLE);
            txt51.setVisibility(View.VISIBLE);
            txt52.setVisibility(View.VISIBLE);
        }

        locationProgressAdapter = new LocationProgressAdapter
                (LocationProgressActivity.this, R.layout.listview_location_progress_row, locationProgressArrayList, spinnerType.getSelectedItemPosition());
        listview.setAdapter(locationProgressAdapter);
        //locationProgressAdapter.notifyDataSetChanged();
        /*for(int i=0;i<listview.getCount();i++){
            listview.getItemAtPosition(i)
        }*/

        /*for(){

        }*/

    }


    private void setSpinnerData() {
        typeList = new ArrayList<>();
        //calList = new ArrayList<>();
        typeList.add("알폼");//index=0
        typeList.add("스틸");//index=1
        typeList.add("부속철물");//index=2
        typeList.add("AL서포트");//index=3
        typeList.add("KD서포트");//index=4
        typeList.add("KS-BEAM");//index=5

        //calList.add("정산전");//index=0
        //calList.add("정산완료");//index=1

        ArrayAdapter adapter = new ArrayAdapter<>(this,R.layout.spinner_item, typeList);

        spinnerType.setAdapter(adapter);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        spinnerType.setSelection(0);

        /*ArrayAdapter adapter2 = new ArrayAdapter<>(this,
                R.layout.spinner_item, calList);*/

        //spinnerCal.setAdapter(adapter2);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        //spinnerCal.setSelection(0);


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


        String url = getString(R.string.service_address) + "getUserStockInReport";
        ContentValues values = new ContentValues();
        values.put("SupervisorCode", Users.USER_ID);
        values.put("Type", sType);
        GetUserStockInReport gsod = new GetUserStockInReport(url, values);
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

    public class GetUserStockInReport extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetUserStockInReport(String url, ContentValues values) {
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
                LocationProgress locationProgress;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                locationProgressArrayList = new ArrayList<>();
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                //double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(LocationProgressActivity.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(LocationProgressActivity.this, ErrorCheck, 2);
                        return;
                    }
                    locationProgress = new LocationProgress();
                    locationProgress.CustomerName = child.getString("CustomerName");
                    locationProgress.LocationName = child.getString("LocationName");

                    locationProgress.AlOutWeight = child.getString("AlOutWeight");
                    locationProgress.AlOutWeightG = child.getString("AlOutWeightG");
                    locationProgress.AlInWeight = child.getString("AlInWeight");
                    locationProgress.Alminus = child.getString("Alminus");
                    locationProgress.AlRate = child.getString("AlRate");
                    locationProgress.AlOver = child.getString("AlOver");


                    locationProgress.StOutWeightG = child.getString("StOutWeightG");
                    locationProgress.StOutWeight = child.getString("StOutWeight");
                    locationProgress.StNewInWeight = child.getString("StNewInWeight");
                    locationProgress.StInWeight = child.getString("StInWeight");
                    locationProgress.StRate = child.getString("StRate");

                    locationProgress.OtOutWeightG = child.getString("OtOutWeightG");
                    locationProgress.OtInWeight = child.getString("OtInWeight");
                    locationProgress.OtRate = child.getString("OtRate");
                    locationProgress.SpOutQty = child.getString("SpOutQty");
                    locationProgress.SpInQty = child.getString("SpInQty");
                    locationProgress.SpQRate = child.getString("SpQRate");

                    locationProgress.SpOutWeightG = child.getString("SpOutWeightG");
                    locationProgress.SpOutWeight = child.getString("SpOutWeight");
                    locationProgress.SpInWeightG = child.getString("SpInWeightG");
                    locationProgress.SpWRate = child.getString("SpWRate");
                    locationProgress.KDSpOutQty = child.getString("KDSpOutQty");
                    locationProgress.KDSpInQty = child.getString("KDSpInQty");

                    locationProgress.KDSpQRate = child.getString("KDSpQRate");
                    locationProgress.KDSpOutWeightG = child.getString("KDSpOutWeightG");
                    locationProgress.KDSpOutWeight = child.getString("KDSpOutWeight");
                    locationProgress.KDSpInWeightG = child.getString("KDSpInWeightG");
                    locationProgress.KDSpWRate = child.getString("KDSpWRate");
                    locationProgress.KBOutQty = child.getString("KBOutQty");

                    locationProgress.KBInQty = child.getString("KBInQty");
                    locationProgress.KBQRate = child.getString("KBQRate");
                    locationProgress.KBOutWeightG = child.getString("KBOutWeightG");
                    locationProgress.KBInWeight = child.getString("KBInWeight");
                    locationProgress.KBWRate = child.getString("KBWRate");

                    //LocationProgress.orderQty = child.getString("OrderQty");
                    //LocationProgress.initState = true;

                    locationProgressArrayList.add(locationProgress);
                    /*if (!dongDic.contains(LocationProgress.Dong))
                        dongDic.add(LocationProgress.Dong);
                    Collections.sort(dongDic);*/
                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }

                /*dongSequences = new CharSequence[dongDic.size() + 1];
                dongSequences[0] = "전체";
                for (int i = 1; i < dongDic.size() + 1; i++) {
                    dongSequences[i] = dongDic.get(i - 1);
                }*/
                locationProgressAdapter = new LocationProgressAdapter
                        (LocationProgressActivity.this, R.layout.listview_location_progress_row, locationProgressArrayList, spinnerType.getSelectedItemPosition());
                listview.setAdapter(locationProgressAdapter);
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

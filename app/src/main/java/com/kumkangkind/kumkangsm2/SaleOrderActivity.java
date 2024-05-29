package com.kumkangkind.kumkangsm2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.kumkangkind.kumkangsm2.Adapter.SaleOrderAdapter;
import com.kumkangkind.kumkangsm2.Object.SaleOrder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class SaleOrderActivity extends BaseActivity {

    TextView txtDate;
    TextView txtCustomerLocation;
    ListView listview;
    ArrayList<SaleOrder> saleOrderArrayList;
    SaleOrderAdapter saleOrderAdapter;
    Spinner spinnerReceiptFlag;
    Spinner spinnerRequestType;
    ArrayList<String> receiptList;
    ArrayList<String> requestList;
    String customerName;
    String locationNo;
    String locationName;
    boolean firstFlag = true;
    int fromYear = 0;
    int fromMonth = 0;
    int fromDay = 0;

    int toYear = 0;
    int toMonth = 0;
    int toDay = 0;
    Filter filter;//검색 필터
    ArrayList<String> dongDic;//동 검색을 위한 리스트
    CharSequence[] dongSequences;
    DatePicker upDatePicker;
    DatePicker downDatePicker;
    LinearLayout layoutDate;
    TextView txtDong;
    int selectedIndex = 0;
    //TextView txtSaleOrderNo;

//FragmentViewSaleOrder fragmentViewSaleOrder;

//1.앞쪽에서 선택한 데이터를 가져온다.
//2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
//1번과 2번 데이터를 합쳐서
//화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saleorder);
        txtDate = findViewById(R.id.txtDate);
        layoutDate = findViewById(R.id.layoutDate);
        listview = findViewById(R.id.listview);
        txtCustomerLocation = findViewById(R.id.txtCustomerLocation);
        txtDong = findViewById(R.id.txtDong);
        spinnerReceiptFlag = findViewById(R.id.spinnerReceiptFlag);
        spinnerRequestType = findViewById(R.id.spinnerRequestType);
        customerName = getIntent().getStringExtra("customerName");
        locationNo = getIntent().getStringExtra("locationNo");
        locationName = getIntent().getStringExtra("locationName");
        txtCustomerLocation.setText(customerName + "(" + locationName + ")");
        setDate();
        setSpinnerData();

        spinnerRequestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstFlag) {
                    getSaleOrder();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerReceiptFlag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSaleOrder();
                firstFlag = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        txtDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SaleOrderActivity.this).setTitle("동을 선택하세요").setSingleChoiceItems(dongSequences, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex = which;
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        //listview.setFilterText("SPP BPE");
                        filter = saleOrderAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                        filter.filter(dongSequences[selectedIndex]);
                        dialog.dismiss();
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //listview.setFilterText("SPP BPE");
                        filter = saleOrderAdapter.getFilter();//글자가 나타나는 현상때문에 해당 소스로 변경
                        filter.filter(dongSequences[selectedIndex]);

                        dialog.dismiss();
                    }
                }).show();
            }
        });

    }

    private void setDate() {
        //현재일자를 년 월 일 별로 불러온다.
        Calendar cal = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();
        cal2.add(Calendar.YEAR, -1);
        fromYear = cal2.get(Calendar.YEAR);
        fromMonth = cal2.get(Calendar.MONTH);
        fromDay = cal2.get(Calendar.DATE);

        toYear = cal.get(Calendar.YEAR);
        toMonth = cal.get(Calendar.MONTH);
        toDay = cal.get(Calendar.DATE);

        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

        String outTxt = strFromDate + " ~ " + strToDate;

        // txtLeftCircle.setTextColor(Color.parseColor("#18A266"));
        // txtLeftCircle.setTextColor(Color.parseColor("#FFFFFF"));

        txtDate.setText(outTxt);

        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog에서 보여줄 입력화면 View 객체 생성 작업
                //Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                LayoutInflater inflater = getLayoutInflater();

                //res폴더>>layout폴더>>dialog_addmember.xml 레이아웃 리소스 파일로 View 객체 생성
                //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
                final View dialogView = inflater.inflate(R.layout.dialog_double_date_picker, null);

                TextView txtStart = dialogView.findViewById(R.id.txtStartDay);
                TextView txtEnd = dialogView.findViewById(R.id.txtEndDay);
                /*
                 * 스피너의 폰트, 글자색 변경을 위함
                 * */
                Spinner searchSpinner = dialogView.findViewById(R.id.spinnerSearchType);
                searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                //멤버의 세부내역 입력 Dialog 생성 및 보이기
                AlertDialog.Builder buider = new AlertDialog.Builder(SaleOrderActivity.this); //AlertDialog.Builder 객체 생성
                //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fromYear = upDatePicker.getYear();
                        fromMonth = upDatePicker.getMonth();
                        fromDay = upDatePicker.getDayOfMonth();

                        toYear = downDatePicker.getYear();
                        toMonth = downDatePicker.getMonth();
                        toDay = downDatePicker.getDayOfMonth();
                        String strFromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
                        String strToDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

                        String outTxt = strFromDate + " ~ " + strToDate;
                        txtDate.setText(outTxt);

                        getSaleOrder();
                        progressOFF();
                    }
                });


                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressOFF();
                    }
                });

//설정한 값으로 AlertDialog 객체 생성

                AlertDialog dialog = buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();

                upDatePicker = dialog.findViewById(R.id.start_date);
                downDatePicker = dialog.findViewById(R.id.end_date);
                searchSpinner.setVisibility(View.INVISIBLE);
                upDatePicker.updateDate(fromYear, fromMonth, fromDay);
                downDatePicker.updateDate(toYear, toMonth, toDay);
            }
        });
    }

    private void setSpinnerData() {
        this.receiptList = new ArrayList<>();
        this.requestList = new ArrayList<>();
        receiptList.add("전체");//data=-1, index=0
        receiptList.add("생산의뢰");//data=0, index=1
        receiptList.add("의뢰접수");//data=1, index=2
        receiptList.add("출고의뢰");//data=999, index=3

        requestList.add("전체");//data="-1", index=0
        requestList.add("주문품의");//data="S", index=1
        requestList.add("계획품의");//data="P", index=2
        requestList.add("계획의뢰");//data="R", index=3
        requestList.add("취소품의");//data="C", index=4
        requestList.add("추가품의");//data="A", index=5

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, receiptList);

        spinnerReceiptFlag.setAdapter(adapter);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        spinnerReceiptFlag.setSelection(2);

        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, requestList);

        spinnerRequestType.setAdapter(adapter2);
        //spinnerLocation.setMinimumWidth(150);
        //spinnerLocation.setDropDownWidth(150);
        spinnerRequestType.setSelection(0);


    }

    private void getSaleOrder() {
        String url = getString(R.string.service_address) + "getSaleOrder";
        ContentValues values = new ContentValues();

        String fromDate = fromYear + "-" + (fromMonth + 1) + "-" + fromDay;
        String toDate = toYear + "-" + (toMonth + 1) + "-" + toDay;

        String receiptFlag;
        String requestType;
        int receiptPosition = spinnerReceiptFlag.getSelectedItemPosition();
        if (receiptPosition == 0) receiptFlag = "-1";
        else if (receiptPosition == 1) receiptFlag = "0";
        else if (receiptPosition == 2) receiptFlag = "1";
        else receiptFlag = "999";
        int requestPosition = spinnerRequestType.getSelectedItemPosition();
        if (requestPosition == 0) requestType = "-1";
        else if (requestPosition == 1) requestType = "S";
        else if (requestPosition == 2) requestType = "P";
        else if (requestPosition == 3) requestType = "R";
        else if (requestPosition == 4) requestType = "C";
        else requestType = "A";


        values.put("FromDate", fromDate);
        values.put("ToDate", toDate);
        values.put("ReceiptFlag", receiptFlag);
        values.put("LocationNo", locationNo);
        values.put("RequestType", requestType);
        GetSaleOrder gsod = new GetSaleOrder(url, values);
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

    public class GetSaleOrder extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetSaleOrder(String url, ContentValues values) {
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
                dongDic = new ArrayList<>();
                SaleOrder saleOrder;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                saleOrderArrayList = new ArrayList<>();
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                //double totalAmt=0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(SaleOrderActivity.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }
                    saleOrder = new SaleOrder();
                    saleOrder.SaleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.Dong = child.getString("Dong");
                    saleOrder.RequestType = child.getString("RequestType");
                    saleOrder.RequestTypeName = child.getString("RequestTypeName");
                    saleOrder.ReceiptFlag = child.getString("ReceiptFlag");
                    saleOrder.ReceiptFlagName = child.getString("ReceiptFlagName");
                    saleOrder.WorderRequestNo = child.getString("WorderRequestNo");
                    saleOrder.Remark = child.getString("Remark");
                    //saleOrder.orderQty = child.getString("OrderQty");
                    //saleOrder.initState = true;

                    saleOrderArrayList.add(saleOrder);
                    if (!dongDic.contains(saleOrder.Dong)) dongDic.add(saleOrder.Dong);
                    Collections.sort(dongDic);
                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }

                dongSequences = new CharSequence[dongDic.size() + 1];
                dongSequences[0] = "전체";
                for (int i = 1; i < dongDic.size() + 1; i++) {
                    dongSequences[i] = dongDic.get(i - 1);
                }
                saleOrderAdapter = new SaleOrderAdapter(SaleOrderActivity.this, R.layout.listview_saleorder_row, saleOrderArrayList, listview, customerName, locationName);
                listview.setAdapter(saleOrderAdapter);
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

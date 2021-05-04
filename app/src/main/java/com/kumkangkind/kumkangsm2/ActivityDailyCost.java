package com.kumkangkind.kumkangsm2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityDailyCost extends BaseActivity {

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private TextView textViewMonth;
    public static final int KEY_1 = 1;
    public static final int KEY_2 = 2;

    ArrayList<DailyCost> monthList;
    DailyCost dailyCost = null;

    String type1 = "";
    String type2 = "";
    String yyyyMM = "";
    String supervisorCode;
    String yyyyMMdd = "";
    ProgressDialog mProgressDialog;
    Dialog dialog;
    TextView textViewCostType;
    TextView textViewCostDate;
    TextView textViewAmount;
    TextView currentTextView;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailycost);

        textViewMonth = (TextView)findViewById(R.id.textViewMonth);
        ((TextView)findViewById(R.id.costUser)).setText(Users.UserName);


        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH) + 1;
        mDay = cal.get(Calendar.DATE);

        if(mMonth  < 10)
            yyyyMM = String.valueOf(mYear) + "0" + String.valueOf(mMonth);
        else
            yyyyMM = String.valueOf(mYear) + String.valueOf(mMonth);

        textViewMonth = (TextView) findViewById(R.id.textViewMonth);
        textViewMonth.setText(yyyyMM);
    }

    public void mOnClick(View v){


        switch (v.getId()) {

            case R.id.btnSearchCost:
                //1. 일자클릭 시 년월선택

                GetDailyCost();
                break;
            case R.id.textViewMonth:
                //2. 조회 버튼 클릭시 통신하여 데이터를 가져온다./
                try {
                    CallDateDialog();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
                //3. 업데이트 문?
        }
    }

    private void CallDateDialog() {
        try {

            Calendar cal = new GregorianCalendar();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityDailyCost.this, mDateSetListener1, mYear, mMonth, mDay);
            Field[] f = datePickerDialog.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField.get(datePickerDialog);
                    Field datePickerFields[] = dateField.getType().getDeclaredFields();

                    //Day 제거
                    for (Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName()) || "mDayPicker".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
            datePickerDialog.show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear +1;
                    String yyyyMM= "";

                    if(mMonth  < 10)
                        yyyyMM = String.valueOf(mYear) + "0" + String.valueOf(mMonth);
                    else
                        yyyyMM = String.valueOf(mYear) + String.valueOf(mMonth);

                    textViewMonth = (TextView) findViewById(R.id.textViewMonth);
                    textViewMonth.setText(yyyyMM);

                    //textViewMonth.setText(String.format("%d-%d", mYear,mMonth + 1));
                }
            };

    /**
     * 월별로 테이블 레이아웃을 만든다.
     */
    private void MakeTableLayout() {

        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1, getResources().getDisplayMetrics());

        /*
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_dailycost);
*/
        //tableLayout
        TableLayout t0 = (TableLayout)findViewById(R.id.t0);

        t0.removeAllViews();

        TableRow tr_title = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("일자");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setTextColor(Color.BLUE);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setText("시내출장");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);

        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("시외숙박");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("시외일비");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("시외주차");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("시외통행");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("유류대");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("차량유지");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("회식대");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85 * dip);
        tv.setText("소모품");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85*dip);
        tv.setText("통신비");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);

        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(85*dip);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setText("합계");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        //tableLayout
        TableLayout t1        = (TableLayout)findViewById(R.id.t1);
        t1.removeAllViews();

        TableRow tr_date = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {



            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText(monthList.get(i).WeekDay);
            tvNo.setTextColor(Color.BLUE);
            tvNo.setBackgroundResource(R.drawable.background_tableheader);
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            tr_date.addView(tvNo);
        }
        t1.addView(tr_date, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost1 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //Cost1 시내출장
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText(monthList.get(i).Cost1);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "1");
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            tr_Cost1.addView(tvNo);
        }
        t1.addView(tr_Cost1, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost2 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //Cost2 시외숙박
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText(monthList.get(i).Cost2);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "2");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            tr_Cost2.addView(tvNo);
        }
        t1.addView(tr_Cost2, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost3 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //Cost3 시외일비
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost3);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "3");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            tr_Cost3.addView(tvNo);
        }
        t1.addView(tr_Cost3, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost4 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost4);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "4");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            tr_Cost4.addView(tvNo);
        }
        t1.addView(tr_Cost4, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost5 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost5);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "5");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost5.addView(tvNo);
        }
        t1.addView(tr_Cost5, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost6 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost6);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "6");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost6.addView(tvNo);
        }
        t1.addView(tr_Cost6, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost7 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost7);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "7");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost7.addView(tvNo);
        }
        t1.addView(tr_Cost7, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost8 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost8);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "8");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost8.addView(tvNo);
        }
        t1.addView(tr_Cost8, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost9 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70*dip);
            tvNo.setText(monthList.get(i).Cost9);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "9");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost9.addView(tvNo);
        }
        t1.addView(tr_Cost9, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost10 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText(monthList.get(i).Cost10);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "10");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost10.addView(tvNo);
        }
        t1.addView(tr_Cost10, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        TableRow tr_Cost11 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText(monthList.get(i).Cost11);
            tvNo.setTextColor(Color.BLUE);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i).CostDate);
            tvNo.setTag(R.id.costType, "11");
            tvNo.setPadding(10, 25, 0, 25);;
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost11.addView(tvNo);
        }
        t1.addView(tr_Cost11, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void GetDailyCost(){

        TextView tv = (TextView)findViewById(R.id.textViewcostTitle);
        supervisorCode = Users.USER_ID;
        yyyyMM = textViewMonth.getText().toString();

        tv.setText(yyyyMM.substring(0, 4) + "년 " + yyyyMM.substring(4, 6) + "월" + " 사용경비내역" );

        new HttpAsyncTask().execute(getString(R.string.service_address)+"getdailycost");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONArray jsonArray = new JSONArray(result);

                monthList = new ArrayList<DailyCost>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    dailyCost = new DailyCost();
                    JSONObject child = jsonArray.getJSONObject(i);
                    dailyCost.SupervisorCode = child.getString("SupervisorCode");
                    dailyCost.CostDate  = child.getString("CostDate");
                    dailyCost.Cost1 = child.getString("Cost1");
                    dailyCost.Cost2 = child.getString("Cost2");
                    dailyCost.Cost3 = child.getString("Cost3");
                    dailyCost.Cost4 = child.getString("Cost4");
                    dailyCost.Cost5 = child.getString("Cost5");
                    dailyCost.Cost6 = child.getString("Cost6");
                    dailyCost.Cost7 = child.getString("Cost7");
                    dailyCost.Cost8 = child.getString("Cost8");
                    dailyCost.Cost9 = child.getString("Cost9");
                    dailyCost.Cost10 = child.getString("Cost10");
                    dailyCost.Cost11 = child.getString("Cost11");
                    dailyCost.WeekDay = child.getString("WeekDay");
                    monthList.add(dailyCost);
                }
                mHandler.sendEmptyMessage(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String toCostType(String typeCode) {

        switch(typeCode) {

            case "1":
                return "시내출장비";
            case "2":
                return "시외숙박비";
            case "3":
                return "시외일비";
            case "4":
                return "시외주차료";
            case "5":
                return "시외통행료";
            case "6":
                return "유류대";
            case "7":
                return "차량유지보조금";
            case "8":
                return "복리후생비";
            case "9":
                return "소모품비";
            case "10":
                return "통신비";
            default:
                return "";
        }
    }

    TextView.OnClickListener mOnclickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String costType = v.getTag(R.id.costType).toString();
                    String type = toCostType(v.getTag(R.id.costType).toString());
                    String date = v.getTag(R.id.costDate).toString();
                    String amount = ((TextView) v).getText().toString();

                    //다이알로그
                    ShowDialog(costType, type, date, amount, (TextView)v);
                }
            };


    private void ShowDialog(String costType, String type, String costDate, final String amount, final TextView textView){

    //Dialog 디자인 해야함
        dialog = new Dialog(ActivityDailyCost.this);

        dialog.setContentView(R.layout.dialog_cost);
        dialog.setTitle("경비등록");

        textViewCostDate = (TextView) dialog.findViewById(R.id.textViewCostDate);
        textViewCostDate.setText(costDate);

        textViewCostType = (TextView) dialog.findViewById(R.id.textViewCostType);
        textViewCostType.setText(type);



        textViewAmount = (EditText) dialog.findViewById(R.id.EditTextAmount);
        if(amount.equals("0"))
            textViewAmount.setText("");
        else
            textViewAmount.setText(amount);


        //textViewAmount.setSelectAllOnFocus(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod (textViewAmount .getApplicationWindowToken(),InputMethodManager.SHOW_FORCED);


        Button okButton = (Button) dialog.findViewById(R.id.btnItemOK);

        yyyyMMdd = costDate;
        type1 = costType;


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type2 = textViewAmount.getText().toString();
                currentTextView = textView;
                new HttpAsyncTask2().execute(getString(R.string.service_address)+"setdailycost");
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
    }


    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //메시지를 처리한다.
            MakeTableLayout();
        }
    };


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
            jsonObject.put("Supervisor",supervisorCode);
            jsonObject.put("FromDate", yyyyMM);
            jsonObject.put("ToDate", "");
            jsonObject.put("Type1", "");
            jsonObject.put("Type2", "");
            jsonObject.put("Type3", "");
            jsonObject.put("Type4", "");

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
        //mProgressDialog.dismiss();
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        inputStream.close();
        return result;
    }

    private String convertInputStreamToString2(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        String message = "";
        String resultCode = "";

        try {

            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

            result =  message + "(" + resultCode  + ")";
            //Toast.makeText(getBaseContext() , message + "(" + resultCode  + ")" , Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        inputStream.close();
        return result;
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext() , result , Toast.LENGTH_SHORT).show();
            mHandler2.sendEmptyMessage(0);
        }
    }

    public String POST2(String url) {
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
            jsonObject.put("Supervisor",supervisorCode);
            jsonObject.put("FromDate", yyyyMMdd);
            jsonObject.put("ToDate", "");
            jsonObject.put("Type1", type1);
            jsonObject.put("Type2", type2.replace(",", ""));
            jsonObject.put("Type3", "");
            jsonObject.put("Type4", "");

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
                result = convertInputStreamToString2(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            //Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        //Log.i("result", result.toString());
        //mProgressDialog.dismiss();
        return result;
    }

    Handler mHandler2 = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            currentTextView.setText(textViewAmount.getText().toString());
            dialog.dismiss();
        }
    };


}

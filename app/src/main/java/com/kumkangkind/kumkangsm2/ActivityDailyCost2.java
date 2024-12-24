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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActivityDailyCost2 extends BaseActivity {

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private TextView textViewMonth;
    public static final int KEY_1 = 1;
    public static final int KEY_2 = 2;

    ArrayList<DailyCost2> monthList;
    ArrayList<Budget> budgetList;
    DailyCost2 dailyCost = null;

    String type1 = "";
    String type2 = "";
    String yyyyMM = "";
    String supervisorCode;
    String yyyyMMdd = "";
    String amountA= "";
    String amountB = "";
    String amountC = "";
    String remarkA = "";
    String remarkB = "";
    String remarkC = "";


    ProgressDialog mProgressDialog;
    Dialog dialog;
    TextView textViewCostType;
    TextView textViewCostDate;

    TextView textViewAmountA;
    TextView textViewAmountB;
    TextView textViewAmountC;

    TextView currentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailycost);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//소프트키 생성시, 화면 가리는것을 해결한다.

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
        setView();
        progressOFF();
    }

    private void setView() {
        if(Users.Language != 0){
            TextView textViewcostTitle = findViewById(R.id.textViewcostTitle);
            TextView textView1 = findViewById(R.id.textView1);
            TextView textView2 = findViewById(R.id.textView2);
            Button btnSearchCost = findViewById(R.id.btnSearchCost);
            Button btnSearchBudget = findViewById(R.id.btnSearchBudget);

            textViewcostTitle.setText("Details of on-site use expenses");
            textView1.setText("Supervisor");
            textView2.setText("Year/Month");
            btnSearchCost.setText("Search");
            btnSearchBudget.setText("Budget");
        }
    }

    public void mOnClick(View v){


        switch (v.getId()) {

            case R.id.btnSearchCost://

                GetDailyCost();
                break;
            case R.id.textViewMonth:
                try {
                    CallDateDialog();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;

            case R.id.btnSearchBudget:
                GetBudget();
                break;
            //예산을 Dialog로 보인다.
        }
    }

    private void CallDateDialog() {
        try {

            Calendar cal = new GregorianCalendar();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityDailyCost2.this, mDateSetListener1, mYear, mMonth, mDay);
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
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "일자" : "Date");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setTextColor(Color.BLUE);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setText(Users.Language == 0 ? "시내출장" : "business trip to the city");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "시외숙박" : "out-of-town accommodation");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "시외일비" : "out-of-town daily expenses");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "시외주차" : "out-of-town parking");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "시외통행" : "an out-of-town traffic");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "유류대" : "oil field");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "차량유지" : "vehicle maintenance");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        tr_title.setVisibility(View.GONE);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "회식대" : "get-together party");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95 * dip);
        tv.setText(Users.Language == 0 ? "소모품" : "consumables");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);
        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95*dip);
        tv.setText(Users.Language == 0 ? "통신비" : "communication costs");
        tv.setBackgroundResource(R.drawable.background_tableheader);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(10, 25, 0, 25);
        tr_title.addView(tv);

        t0.addView(tr_title, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_title = new TableRow(this);
        tv = new TextView(this);
        tv.setWidth(95*dip);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setText(Users.Language == 0 ? "합계" : "Total");
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
            tvNo.setWidth(95 * dip);
            tvNo.setText(monthList.get(i).WeekDay);
            tvNo.setTextColor(Color.BLUE);
            tvNo.setBackgroundResource(R.drawable.background_tableheader);
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

            tr_date.addView(tvNo);
        }
        t1.addView(tr_date, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost1 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //Cost1 시내출장
            TextView tvNo = new TextView(this);
            tvNo.setWidth(95 * dip);
            tvNo.setText(monthList.get(i).Cost1);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95 * dip);
            tvNo.setText(monthList.get(i).Cost2);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost3);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost4);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost5);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost6);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost7);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
            tvNo.setTag(R.id.costType, "7");
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setClickable(true);
            tvNo.setOnClickListener(mOnclickListener);
            tvNo.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            tr_Cost7.addView(tvNo);
            tr_Cost7.setVisibility(View.GONE);
        }
        t1.addView(tr_Cost7, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TableRow tr_Cost8 = new TableRow(this);
        for (int i = 0; i < monthList.size(); i++) {

            //일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost8);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95*dip);
            tvNo.setText(monthList.get(i).Cost9);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95 * dip);
            tvNo.setText(monthList.get(i).Cost10);
            tvNo.setTextColor(Color.BLACK);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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
            tvNo.setWidth(95 * dip);
            tvNo.setText(monthList.get(i).TotalCost);
            tvNo.setTextColor(Color.BLUE);
            tvNo.setBackgroundResource(R.drawable.background_tablecell);
            tvNo.setTag(R.id.costDate, monthList.get(i));
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

        tv.setText(yyyyMM.substring(0, 4) + (Users.Language == 0 ? "년 " : "year ") + yyyyMM.substring(4, 6)
                + (Users.Language == 0 ? "월" : "month") + (Users.Language == 0 ? " 사용경비내역" : " Details of use"));

        //new HttpAsyncTask().execute(Users.ServiceAddress+"getdailycost");
        new HttpAsyncTask().execute(Users.ServiceAddress + "getdaycost");
    }

    private void GetBudget(){

        TextView tv = (TextView)findViewById(R.id.textViewcostTitle);
        supervisorCode = Users.USER_ID;
        yyyyMM = textViewMonth.getText().toString();

        tv.setText(yyyyMM.substring(0, 4) + (Users.Language == 0 ? "년 " : "year ") + yyyyMM.substring(4, 6)
                + (Users.Language == 0 ? "월" : "month") + (Users.Language == 0 ? " 사용경비내역" : " Details of use"));

        //new HttpAsyncTask().execute(Users.ServiceAddress+"getdailycost");
        new HttpAsyncTask3().execute(Users.ServiceAddress+"getBudget");
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

                monthList = new ArrayList<DailyCost2>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    dailyCost = new DailyCost2();
                    JSONObject child = jsonArray.getJSONObject(i);
                    dailyCost.SupervisorCode = child.getString("SupervisorCode");
                    dailyCost.CostDate  = child.getString("CostDate");

                    dailyCost.Cost1 = child.getString("Cost1");
                    dailyCost.Cost1A = child.getString("Cost1A");
                    dailyCost.Cost1B = child.getString("Cost1B");
                    dailyCost.Cost1C = child.getString("Cost1C");
                    dailyCost.Remark1A = child.getString("Remark1A");
                    dailyCost.Remark1B = child.getString("Remark1B");
                    dailyCost.Remark1C = child.getString("Remark1C");


                    dailyCost.Cost2 = child.getString("Cost2");
                    dailyCost.Cost2A = child.getString("Cost2A");
                    dailyCost.Cost2B = child.getString("Cost2B");
                    dailyCost.Cost2C = child.getString("Cost2C");
                    dailyCost.Remark2A = child.getString("Remark2A");
                    dailyCost.Remark2B = child.getString("Remark2B");
                    dailyCost.Remark2C = child.getString("Remark2C");

                    dailyCost.Cost3 = child.getString("Cost3");
                    dailyCost.Cost3A = child.getString("Cost3A");
                    dailyCost.Cost3B = child.getString("Cost3B");
                    dailyCost.Cost3C = child.getString("Cost3C");
                    dailyCost.Remark3A = child.getString("Remark3A");
                    dailyCost.Remark3B = child.getString("Remark3B");
                    dailyCost.Remark3C = child.getString("Remark3C");

                    dailyCost.Cost4 = child.getString("Cost4");
                    dailyCost.Cost4A = child.getString("Cost4A");
                    dailyCost.Cost4B = child.getString("Cost4B");
                    dailyCost.Cost4C = child.getString("Cost4C");
                    dailyCost.Remark4A = child.getString("Remark4A");
                    dailyCost.Remark4B = child.getString("Remark4B");
                    dailyCost.Remark4C = child.getString("Remark4C");

                    dailyCost.Cost5 = child.getString("Cost5");
                    dailyCost.Cost5A = child.getString("Cost5A");
                    dailyCost.Cost5B = child.getString("Cost5B");
                    dailyCost.Cost5C = child.getString("Cost5C");
                    dailyCost.Remark5A = child.getString("Remark5A");
                    dailyCost.Remark5B = child.getString("Remark5B");
                    dailyCost.Remark5C = child.getString("Remark5C");

                    dailyCost.Cost6 = child.getString("Cost6");
                    dailyCost.Cost6A = child.getString("Cost6A");
                    dailyCost.Cost6B = child.getString("Cost6B");
                    dailyCost.Cost6C = child.getString("Cost6C");
                    dailyCost.Remark6A = child.getString("Remark6A");
                    dailyCost.Remark6B = child.getString("Remark6B");
                    dailyCost.Remark6C = child.getString("Remark6C");

                    dailyCost.Cost7 = child.getString("Cost7");
                    dailyCost.Cost7A = child.getString("Cost7A");
                    dailyCost.Cost7B = child.getString("Cost7B");
                    dailyCost.Cost7C = child.getString("Cost7C");
                    dailyCost.Remark7A = child.getString("Remark7A");
                    dailyCost.Remark7B = child.getString("Remark7B");
                    dailyCost.Remark7C = child.getString("Remark7C");

                    dailyCost.Cost8 = child.getString("Cost8");
                    dailyCost.Cost8A = child.getString("Cost8A");
                    dailyCost.Cost8B = child.getString("Cost8B");
                    dailyCost.Cost8C = child.getString("Cost8C");
                    dailyCost.Remark8A = child.getString("Remark8A");
                    dailyCost.Remark8B = child.getString("Remark8B");
                    dailyCost.Remark8C = child.getString("Remark8C");

                    dailyCost.Cost9 = child.getString("Cost9");
                    dailyCost.Cost9A = child.getString("Cost9A");
                    dailyCost.Cost9B = child.getString("Cost9B");
                    dailyCost.Cost9C = child.getString("Cost9C");
                    dailyCost.Remark9A = child.getString("Remark9A");
                    dailyCost.Remark9B = child.getString("Remark9B");
                    dailyCost.Remark9C = child.getString("Remark9C");

                    dailyCost.Cost10 = child.getString("Cost10");
                    dailyCost.Cost10A = child.getString("Cost10A");
                    dailyCost.Cost10B = child.getString("Cost10B");
                    dailyCost.Cost10C = child.getString("Cost10C");
                    dailyCost.Remark10A = child.getString("Remark10A");
                    dailyCost.Remark10B = child.getString("Remark10B");
                    dailyCost.Remark10C = child.getString("Remark10C");

                    dailyCost.TotalCost = child.getString("TotalCost");
                    dailyCost.WeekDay = child.getString("WeekDay");
                    monthList.add(dailyCost);
                }
                mHandler.sendEmptyMessage(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 예산
     */
    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {

                JSONArray jsonArray = new JSONArray(result);
                budgetList = new ArrayList<Budget>();
                Budget budget = null;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    budget = new Budget();
                    budget.AccountCode = child.getString("AccountCode");
                    budget.AccountName = child.getString("AccountName");
                    budget.Budget = Double.valueOf(child.getString("Budget"));
                    budget.Cost = Double.valueOf(child.getString("Cost"));
                    budget.Balance = Double.valueOf(child.getString("Balance"));
                    budgetList.add(budget);
                }
                mHandler3.sendEmptyMessage(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void MakeBudgetTable(){

        DecimalFormat df = new DecimalFormat("###,###,###,###");


        //Dialog 디자인 해야함
        dialog = new Dialog(ActivityDailyCost2.this);
        dialog.setContentView(R.layout.dialog_budget);
        dialog.setTitle(Users.DeptName +(Users.Language == 0 ? " 예산현황" : " Budget status"));

        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1, getResources().getDisplayMetrics());

        //tableLayout
        TableLayout t1        = (TableLayout)dialog.findViewById(R.id.t1);
        t1.removeAllViews();

        TableRow th_Row = new TableRow(this);
        TextView row_Header1 = new TextView(this);
        row_Header1.setWidth(95 * dip);
        row_Header1.setText("/");
        row_Header1.setTextColor(Color.BLUE);
        row_Header1.setBackgroundResource(R.drawable.background_tableheader);
        row_Header1.setPadding(10, 25, 0, 25);
        row_Header1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        th_Row.addView(row_Header1);

        //예산
        TextView row_Header2 = new TextView(this);
        row_Header2.setWidth(95 * dip);
        row_Header2.setText(Users.Language == 0 ? "예산" : "budget");
        row_Header2.setTextColor(Color.BLUE);
        row_Header2.setBackgroundResource(R.drawable.background_tableheader);
        row_Header2.setPadding(10, 25, 0, 25);
        row_Header2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        th_Row.addView(row_Header2);

        //사용
        TextView row_Header3 = new TextView(this);
        row_Header3.setWidth(95 * dip);
        row_Header3.setText(Users.Language == 0 ? "사용" : "use");
        row_Header3.setTextColor(Color.BLUE);
        row_Header3.setBackgroundResource(R.drawable.background_tableheader);
        row_Header3.setPadding(10, 25, 0, 25);
        row_Header3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        th_Row.addView(row_Header3);

        //잔고
        TextView row_Header4 = new TextView(this);
        row_Header4.setWidth(95 * dip);
        row_Header4.setText(Users.Language == 0 ? "잔고" : "balance");
        row_Header4.setTextColor(Color.BLUE);
        row_Header4.setBackgroundResource(R.drawable.background_tableheader);
        row_Header4.setPadding(10, 25, 0, 25);
        row_Header4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

        th_Row.addView(row_Header4);
        t1.addView(th_Row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < budgetList.size(); i++) {

            TableRow tr_date = new TableRow(this);
            //계정명
            TextView tvNo1 = new TextView(this);
            tvNo1.setWidth(95 * dip);
            tvNo1.setText(budgetList.get(i).AccountName);
            tvNo1.setTextColor(Color.BLACK);
            tvNo1.setBackgroundResource(R.drawable.background_tableheader);
            tvNo1.setPadding(10, 25, 0, 25);
            tvNo1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tr_date.addView(tvNo1);

            //예산
            TextView tvNo2 = new TextView(this);
            tvNo2.setWidth(95 * dip);
            tvNo2.setText(df.format(budgetList.get(i).Budget));
            tvNo2.setTextColor(Color.BLACK);
            tvNo2.setBackgroundResource(R.drawable.background_tableheader);
            tvNo2.setPadding(10, 25, 0, 25);
            tvNo2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tr_date.addView(tvNo2);

            //사용
            TextView tvNo3 = new TextView(this);
            tvNo3.setWidth(95 * dip);
            tvNo3.setText(df.format(budgetList.get(i).Cost));
            tvNo3.setTextColor(Color.BLUE);
            tvNo3.setBackgroundResource(R.drawable.background_tableheader);
            tvNo3.setPadding(10, 25, 0, 25);
            tvNo3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tr_date.addView(tvNo3);

            //잔고
            TextView tvNo4 = new TextView(this);
            tvNo4.setWidth(95 * dip);
            tvNo4.setText(df.format(budgetList.get(i).Balance));
            tvNo4.setTextColor(Color.RED);
            tvNo4.setBackgroundResource(R.drawable.background_tableheader);
            tvNo4.setPadding(10, 25, 0, 25);
            tvNo4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tr_date.addView(tvNo4);

            t1.addView(tr_date, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


        dialog.show();
    }

    private String toCostType(String typeCode) {

        switch(typeCode) {

            case "1":
                return Users.Language == 0 ? "시내출장비" : "business trip to the city";
            case "2":
                return Users.Language == 0 ? "시외숙박비" : "out-of-town accommodation costs";
            case "3":
                return Users.Language == 0 ? "시외일비" : "out-of-town daily expenses";
            case "4":
                return Users.Language == 0 ? "시외주차료" : "out-of-town parking fee";
            case "5":
                return Users.Language == 0 ? "시외통행료" : "out-of-town charge";
            case "6":
                return Users.Language == 0 ? "유류대" : "oil field";
            case "7":
                return Users.Language == 0 ? "차량유지보조금" : "vehicle maintenance";
            case "8":
                return Users.Language == 0 ? "회식대" : "get-together party";
            case "9":
                return Users.Language == 0 ? "소모품비" : "consumable";
            case "10":
                return Users.Language == 0 ? "통신비" : "communication costs";
            default:
                return "";
        }
    }

    TextView.OnClickListener mOnclickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dailyCost = (DailyCost2)v.getTag(R.id.costDate);

                    String costType = v.getTag(R.id.costType).toString();
                    String type = toCostType(v.getTag(R.id.costType).toString());
                    String date = dailyCost.CostDate.toString();

                    //다이알로그
                    ShowDialog(dailyCost, costType, type, date, (TextView)v);

                }
            };


    /**
     *
     * @param dailyCost2
     * @param costType
     * @param type
     * @param costDate
     * @param textView
     */
    private void ShowDialog(DailyCost2 dailyCost2, String costType, String type, String costDate, final TextView textView) {

        //게정별로 체크하여 금액을 보인다.
        remarkA = "";
        remarkB = "";
        remarkC = "";

        amountA = "";
        amountB = "";
        amountC= "";

        switch (costType) {
            case "1":
                remarkA = dailyCost2.Remark1A;
                remarkB = dailyCost2.Remark1B;
                remarkC = dailyCost2.Remark1C;
                amountA = dailyCost2.Cost1A;
                amountB = dailyCost2.Cost1B;
                amountC = dailyCost2.Cost1C;
                break;
            case "2":
                remarkA = dailyCost2.Remark2A;
                remarkB = dailyCost2.Remark2B;
                remarkC = dailyCost2.Remark2C;
                amountA = dailyCost2.Cost2A;
                amountB = dailyCost2.Cost2B;
                amountC = dailyCost2.Cost2C;
                break;
            case "3":
                remarkA = dailyCost2.Remark3A;
                remarkB = dailyCost2.Remark3B;
                remarkC = dailyCost2.Remark3C;
                amountA = dailyCost2.Cost3A;
                amountB = dailyCost2.Cost3B;
                amountC = dailyCost2.Cost3C;
                break;
            case "4":
                remarkA = dailyCost2.Remark4A;
                remarkB = dailyCost2.Remark4B;
                remarkC = dailyCost2.Remark4C;
                amountA = dailyCost2.Cost4A;
                amountB = dailyCost2.Cost4B;
                amountC = dailyCost2.Cost4C;
                break;
            case "5":
                remarkA = dailyCost2.Remark5A;
                remarkB = dailyCost2.Remark5B;
                remarkC = dailyCost2.Remark5C;
                amountA = dailyCost2.Cost5A;
                amountB = dailyCost2.Cost5B;
                amountC = dailyCost2.Cost5C;
                break;
            case "6":
                remarkA = dailyCost2.Remark6A;
                remarkB = dailyCost2.Remark6B;
                remarkC = dailyCost2.Remark6C;
                amountA = dailyCost2.Cost6A;
                amountB = dailyCost2.Cost6B;
                amountC = dailyCost2.Cost6C;
                break;
            case "7":
                remarkA = dailyCost2.Remark7A;
                remarkB = dailyCost2.Remark7B;
                remarkC = dailyCost2.Remark7C;
                amountA = dailyCost2.Cost7A;
                amountB = dailyCost2.Cost7B;
                amountC = dailyCost2.Cost7C;
                break;
            case "8":
                remarkA = dailyCost2.Remark8A;
                remarkB = dailyCost2.Remark8B;
                remarkC = dailyCost2.Remark8C;
                amountA = dailyCost2.Cost8A;
                amountB = dailyCost2.Cost8B;
                amountC = dailyCost2.Cost8C;
                break;
            case "9":
                remarkA = dailyCost2.Remark9A;
                remarkB = dailyCost2.Remark9B;
                remarkC = dailyCost2.Remark9C;
                amountA = dailyCost2.Cost9A;
                amountB = dailyCost2.Cost9B;
                amountC = dailyCost2.Cost9C;
                break;
            case "10":
                remarkA = dailyCost2.Remark10A;
                remarkB = dailyCost2.Remark10B;
                remarkC = dailyCost2.Remark10C;
                amountA = dailyCost2.Cost10A;
                amountB = dailyCost2.Cost10B;
                amountC = dailyCost2.Cost10C;
                break;

            default:
                remarkA = dailyCost2.Remark10A;
                remarkB = dailyCost2.Remark10B;
                remarkC = dailyCost2.Remark10C;
                amountA = dailyCost2.Cost10A;
                amountB = dailyCost2.Cost10B;
                amountC = dailyCost2.Cost10C;
        }

        //Dialog 디자인 해야함
        dialog = new Dialog(ActivityDailyCost2.this);

        dialog.setContentView(R.layout.dialog_cost2);
        dialog.setTitle(Users.Language == 0 ? "경비등록" : "Cost registration");

        textViewCostDate = (TextView) dialog.findViewById(R.id.textViewCostDate);
        textViewCostDate.setText(costDate);

        textViewCostType = (TextView) dialog.findViewById(R.id.textViewCostType);
        textViewCostType.setText(type);


        //스피너 세팅
        final Spinner spinner1 = (Spinner) dialog.findViewById(R.id.spinnerCreidtCard1);
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Users.CardList);
        spinner1.setAdapter(cardAdapter);
        spinner1.setSelection(getIndex(spinner1, remarkA));


        final Spinner spinner2 = (Spinner) dialog.findViewById(R.id.spinnerCreidtCard2);
        ArrayAdapter<String> cardAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Users.CardList);
        spinner2.setAdapter(cardAdapter2);
        spinner2.setSelection(getIndex(spinner2, remarkB));

        final Spinner spinner3 = (Spinner) dialog.findViewById(R.id.spinnerCreidtCard3);
        ArrayAdapter<String> cardAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Users.CardList);
        spinner3.setAdapter(cardAdapter3);
        spinner3.setSelection(getIndex(spinner3, remarkC));

        //금액을 넣는다.
        textViewAmountA = (EditText) dialog.findViewById(R.id.EditTextAmount1);
        if (amountA.equals("0"))
            textViewAmountA.setText("");
        else
            textViewAmountA.setText(amountA);

        textViewAmountB = (EditText) dialog.findViewById(R.id.EditTextAmount2);
        if (amountB.equals("0"))
            textViewAmountB.setText("");
        else
            textViewAmountB.setText(amountB);

        textViewAmountC = (EditText) dialog.findViewById(R.id.EditTextAmount3);
        if (amountC.equals("0"))
            textViewAmountC.setText("");
        else
            textViewAmountC.setText(amountC);

        //textViewAmount.setSelectAllOnFocus(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(textViewAmountA.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);


        Button okButton = (Button) dialog.findViewById(R.id.btnItemOK);

        yyyyMMdd = costDate;
        type1 = costType;

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력을 안했으면, 모두 0 처리한다.
                if(textViewAmountA.getText().toString().trim().equals(""))
                    textViewAmountA.setText("0");
                if(textViewAmountB.getText().toString().trim().equals(""))
                    textViewAmountB.setText("0");
                if(textViewAmountC.getText().toString().trim().equals(""))
                    textViewAmountC.setText("0");

                amountA = textViewAmountA.getText().toString();
                amountB = textViewAmountB.getText().toString();
                amountC = textViewAmountC.getText().toString();
                remarkA = spinner1.getSelectedItem().toString().split("[*]")[0];
                remarkB = spinner2.getSelectedItem().toString().split("[*]")[0];
                remarkC = spinner3.getSelectedItem().toString().split("[*]")[0];

                currentTextView = textView;
                new HttpAsyncTask2().execute(Users.ServiceAddress+"setdaycost");
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

    //private method of your class
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().contains(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }


    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //메시지를 처리한다.
            MakeTableLayout();
        }
    };

    Handler mHandler3 = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //메시지를 처리한다.
            MakeBudgetTable();
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
            jsonObject.put("CostA", amountA.replace(",", ""));
            jsonObject.put("CostB", amountB.replace(",", ""));
            jsonObject.put("CostC", amountC.replace(",", ""));
            jsonObject.put("RemarkA", remarkA);
            jsonObject.put("RemarkB", remarkB);
            jsonObject.put("RemarkC", remarkC);

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


            int totalCost =
                    Integer.valueOf(textViewAmountA.getText().toString().replace(",", ""))
                            + Integer.valueOf(textViewAmountB.getText().toString().replace(",",""))
                            + Integer.valueOf(textViewAmountC.getText().toString().replace(",",""));

            currentTextView.setText(String.valueOf(totalCost));
            String costType = currentTextView.getTag(R.id.costType).toString();

            switch (costType) {
                case "1":
                    dailyCost.Remark1A = remarkA;
                    dailyCost.Remark1B = remarkB;
                    dailyCost.Remark1C = remarkC;
                    dailyCost.Cost1A = amountA;
                    dailyCost.Cost1B = amountB;
                    dailyCost.Cost1C = amountC;
                    break;
                case "2":
                    dailyCost.Remark2A = remarkA;
                    dailyCost.Remark2B = remarkB;
                    dailyCost.Remark2C = remarkC;
                    dailyCost.Cost2A = amountA;
                    dailyCost.Cost2B = amountB;
                    dailyCost.Cost2C = amountC;
                    break;
                case "3":
                    dailyCost.Remark3A = remarkA;
                    dailyCost.Remark3B = remarkB;
                    dailyCost.Remark3C = remarkC;
                    dailyCost.Cost3A = amountA;
                    dailyCost.Cost3B = amountB;
                    dailyCost.Cost3C = amountC;
                    break;
                case "4":
                    dailyCost.Remark4A = remarkA;
                    dailyCost.Remark4B = remarkB;
                    dailyCost.Remark4C = remarkC;
                    dailyCost.Cost4A = amountA;
                    dailyCost.Cost4B = amountB;
                    dailyCost.Cost4C = amountC;
                    break;
                case "5":
                    dailyCost.Remark5A = remarkA;
                    dailyCost.Remark5B = remarkB;
                    dailyCost.Remark5C = remarkC;
                    dailyCost.Cost5A = amountA;
                    dailyCost.Cost5B = amountB;
                    dailyCost.Cost5C = amountC;
                    break;
                case "6":
                    dailyCost.Remark6A = remarkA;
                    dailyCost.Remark6B = remarkB;
                    dailyCost.Remark6C = remarkC;
                    dailyCost.Cost6A = amountA;
                    dailyCost.Cost6B = amountB;
                    dailyCost.Cost6C = amountC;
                    break;
                case "7":
                    dailyCost.Remark7A = remarkA;
                    dailyCost.Remark7B = remarkB;
                    dailyCost.Remark7C = remarkC;
                    dailyCost.Cost7A = amountA;
                    dailyCost.Cost7B = amountB;
                    dailyCost.Cost7C = amountC;
                    break;
                case "8":
                    dailyCost.Remark8A = remarkA;
                    dailyCost.Remark8B = remarkB;
                    dailyCost.Remark8C = remarkC;
                    dailyCost.Cost8A = amountA;
                    dailyCost.Cost8B = amountB;
                    dailyCost.Cost8C = amountC;
                    break;
                case "9":
                    dailyCost.Remark9A = remarkA;
                    dailyCost.Remark9B = remarkB;
                    dailyCost.Remark9C = remarkC;
                    dailyCost.Cost9A = amountA;
                    dailyCost.Cost9B = amountB;
                    dailyCost.Cost9C = amountC;
                    break;
                case "10":
                    dailyCost.Remark10A = remarkA;
                    dailyCost.Remark10B = remarkB;
                    dailyCost.Remark10C = remarkC;
                    dailyCost.Cost10A = amountA;
                    dailyCost.Cost10B = amountB;
                    dailyCost.Cost10C = amountC;
                    break;

                default:
                    break;
            }

            currentTextView.setTag(R.id.costDate, dailyCost);

            dialog.dismiss();
        }
    };

    /**
     * Dialog로 예산현황을 보인다.
     */
    private  void ShowBudgetDialog(){
        //Dialog 디자인.
    }
}

package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VehicleRegisterActivity extends BaseActivity {


    TextView tv11;
    TextView tv12;
    TextView tv13;
    TextView tv14;
    TextView tv21;
    TextView tv22;
    TextView tv23;
    TextView tv24;
    TextView tv31;
    TextView tv32;
    TextView tv33;
    TextView tv34;
    TextView tv41;
    TextView tv42;
    TextView tv43;
    TextView tv44;

    private  TextView textViewTitle;
    private String titleBackup;
    private String contentBackup;
    private String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        titleBackup = getIntent().getStringExtra("title");
        contentBackup = getIntent().getStringExtra("content");

        textViewTitle = (TextView)findViewById(R.id.textViewtitle);
        textViewTitle.setText(titleBackup);


        //테이블 레이아웃
        MakeTable();
        MakeArraycontent();

        progressOFF();
    }



    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 5000);
        progressON("Loading...", handler);
    }


    public void mOnClick(View v) {

        switch (v.getId()) {

            case R.id.btnCancel:

                makeResultIntent(contentBackup, 5);

                break;

            case R.id.btnOK:

                MakeStringContent();
        }
    }


    private void MakeArraycontent() {

        String[] arrayContent = contentBackup.split("/");

        if (arrayContent.length > 0) {

            try {

                this.tv11.setText(arrayContent[0]);
                this.tv12.setText(arrayContent[1]);
                this.tv13.setText(arrayContent[2]);
                this.tv14.setText(arrayContent[3]);
                this.tv21.setText(arrayContent[4]);
                this.tv22.setText(arrayContent[5]);
                this.tv23.setText(arrayContent[6]);
                this.tv24.setText(arrayContent[7]);
                this.tv31.setText(arrayContent[8]);
                this.tv32.setText(arrayContent[9]);
                this.tv33.setText(arrayContent[10]);
                this.tv34.setText(arrayContent[11]);
                this.tv41.setText(arrayContent[12]);
                this.tv42.setText(arrayContent[13]);
                this.tv43.setText(arrayContent[14]);
                this.tv44.setText(arrayContent[15]);

            }catch (Exception ex){

            }
        }
    }

    private void MakeStringContent() {

        String result = this.tv11.getText().toString().trim().replace("/", " ") + " /" +
                this.tv12.getText().toString().trim().replace("/"," ") + " /" +
                this.tv13.getText().toString().trim().replace("/", " ") + " /" +
                this.tv14.getText().toString().trim().replace("/", " ") + " /" +
                this.tv21.getText().toString().trim().replace("/", " ") + " /" +
                this.tv22.getText().toString().trim().replace("/", " ") + " /" +
                this.tv23.getText().toString().trim().replace("/", " ") + " /" +
                this.tv24.getText().toString().trim().replace("/", " ") + " /" +
                this.tv31.getText().toString().trim().replace("/", " ") + " /" +
                this.tv32.getText().toString().trim().replace("/", " ") + " /" +
                this.tv33.getText().toString().trim().replace("/", " ") + " /" +
                this.tv34.getText().toString().trim().replace("/", " ") + " /" +
                this.tv41.getText().toString().trim().replace("/", " ") + " /" +
                this.tv42.getText().toString().trim().replace("/", " ") + " /" +
                this.tv43.getText().toString().trim().replace("/", " ") + " /" +
                this.tv44.getText().toString().trim().replace("/"," ");

        makeResultIntent(result, RESULT_OK);
    }

    /**
     *
     * @param content
     * @return
     */
    private void makeResultIntent(String content, int resultCode){

        Intent intent = getIntent();
        intent.putExtra("content", content);
        setResult(resultCode, intent);
        finish();
    }


    private void MakeTable() {

        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1, getResources().getDisplayMetrics());


        TableLayout t2 = (TableLayout) findViewById(R.id.main_table2);

        TableRow tr_head = new TableRow(this);
        //tr_head.setId(10);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr_head.setGravity(Gravity.CENTER);

        //column1
        TextView lb_Seq = new TextView(this);
        //lb_Seq.setId(10);
        lb_Seq.setWidth(20 * dip);
        //lb_Seq.setHeight(LayoutParams.WRAP_CONTENT);
        lb_Seq.setText("순번");
        lb_Seq.setGravity(Gravity.CENTER_HORIZONTAL);
        lb_Seq.setTextColor(Color.WHITE);
        lb_Seq.setPadding(5, 5, 0, 5);
        tr_head.addView(lb_Seq);

        //column1
        TextView lb_Number = new TextView(this);
        //lb_Number.setId(10);
        lb_Number.setWidth(80 * dip);
        lb_Number.setText("출발지");
        lb_Number.setGravity(Gravity.CENTER);
        lb_Number.setTextColor(Color.WHITE);
        lb_Number.setPadding(5, 5, 0, 5);
        tr_head.addView(lb_Number);

        //column2
        TextView lb_Name = new TextView(this);
        //lb_Name.setId(10);
        lb_Name.setWidth(80 * dip);
        lb_Name.setText("도착지");
        lb_Name.setGravity(Gravity.CENTER);
        lb_Name.setTextColor(Color.WHITE);
        lb_Number.setPadding(5, 5, 0, 5);
        tr_head.addView(lb_Name);

        //column3
        TextView lb_Win = new TextView(this);
        //lb_Win.setId(10);
        lb_Win.setWidth(80 * dip);
        lb_Win.setText("Km");
        lb_Win.setGravity(Gravity.CENTER);
        lb_Win.setTextColor(Color.WHITE);
        lb_Win.setPadding(5, 5, 0, 5);
        tr_head.addView(lb_Win);

        //column4F
        TextView lb_loss = new TextView(this);
        //lb_loss.setId(10);
        lb_loss.setWidth(80 * dip);
        lb_loss.setText("비고");
        lb_loss.setGravity(Gravity.CENTER);
        lb_loss.setTextColor(Color.WHITE);
        lb_loss.setPadding(5, 5, 0, 5);

        tr_head.addView(lb_loss);
        t2.addView(tr_head, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        //1행
        TableRow tr1 = new TableRow(this);
        tr1.setGravity(Gravity.CENTER);
        tr1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr1.setBackgroundColor(Color.WHITE);
        //tr1.setId(10);

        TextView tv10 = new TextView(this);
        tv10 = new TextView(this);
        tv10.setHeight(75*dip);
        tv10.setText("1");
        tv10.setWidth(20 * dip);
        tv10.setTextColor(Color.BLACK);
        tv10.setGravity(Gravity.CENTER);
        tv10.setPadding(5, 5, 0, 0);
        tr1.addView(tv10);

        //Add Column
        tv11 = new EditText(this);
        tv11.setWidth(20 * dip);
        tv11.setTextColor(Color.BLACK);
        tv11.setPadding(5, 0, 0, 40);
        tv11.setGravity(Gravity.CENTER);
        tr1.addView(tv11);

        tv12 = new EditText(this);
        tv12.setWidth(20 * dip);
        tv12.setTextColor(Color.BLACK);
        tv12.setGravity(Gravity.CENTER);
        tv12.setPadding(5, 0, 0, 40);
        tr1.addView(tv12);

        tv13 = new EditText(this);
        tv13.setWidth(20 * dip);
        tv13.setTextColor(Color.BLACK);
        tv13.setGravity(Gravity.CENTER);
        tv13.setPadding(5, 5, 0, 40);
        tv13.setInputType(InputType.TYPE_CLASS_NUMBER);
        tr1.addView(tv13);

        tv14 = new EditText(this);
        tv14.setWidth(20 * dip);
        tv14.setTextColor(Color.BLACK);
        tv14.setGravity(Gravity.CENTER);
        tv14.setPadding(5, 5, 0, 40);
        tr1.addView(tv14);

        t2.addView(tr1, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


        //2행
        TableRow tr2 = new TableRow(this);
        tr2.setGravity(Gravity.CENTER);
        tr2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr2.setBackgroundColor(Color.GRAY);
        //tr2.setId(20);

        TextView tv20 = new TextView(this);
        tv20 = new TextView(this);
        tv20.setHeight(75*dip);
        tv20.setText("2");
        tv20.setWidth(20 * dip);
        tv20.setTextColor(Color.WHITE);
        tv20.setGravity(Gravity.CENTER);
        tv20.setPadding(5, 5, 0, 0);
        tr2.addView(tv20);
        //Add Column
        tv21 = new EditText(this);
        tv21.setWidth(20 * dip);
        tv21.setTextColor(Color.WHITE);
        tv21.setGravity(Gravity.CENTER);
        tv21.setPadding(5, 0, 0, 40);
        tr2.addView(tv21);

        tv22 = new EditText(this);
        tv22.setWidth(20 * dip);
        tv22.setTextColor(Color.WHITE);
        tv22.setGravity(Gravity.CENTER);
        tv22.setPadding(5, 0, 0, 40);
        tr2.addView(tv22);

        tv23 = new EditText(this);
        tv23.setWidth(20 * dip);
        tv23.setTextColor(Color.WHITE);
        tv23.setGravity(Gravity.CENTER);
        tv23.setPadding(5, 0, 0, 40);
        tv23.setInputType(InputType.TYPE_CLASS_NUMBER);
        tr2.addView(tv23);


        tv24 = new EditText(this);
        tv24.setWidth(50 * dip);
        tv24.setTextColor(Color.WHITE);
        tv24.setGravity(Gravity.CENTER);
        tv24.setPadding(5, 0, 0, 40);
        tr2.addView(tv24);

        t2.addView(tr2, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        //3행
        TableRow tr3 = new TableRow(this);
        tr3.setGravity(Gravity.CENTER);
        tr3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr3.setBackgroundColor(Color.WHITE);
        //tr3.setId(20);

        TextView tv30 = new TextView(this);
        tv30 = new TextView(this);
        tv30.setHeight(75*dip);
        tv30.setText("3");
        tv30.setWidth(20 * dip);
        tv30.setTextColor(Color.BLACK);
        tv30.setGravity(Gravity.CENTER);
        tv30.setPadding(5, 5, 0, 0);
        tr3.addView(tv30);

        //Add Column
        tv31 = new EditText(this);
        tv31.setWidth(20 * dip);
        tv31.setTextColor(Color.BLACK);
        tv31.setPadding(5, 0, 0, 40);
        tv31.setGravity(Gravity.CENTER);
        tr3.addView(tv31);


        tv32 = new EditText(this);
        tv32.setWidth(20 * dip);
        tv32.setTextColor(Color.BLACK);
        tv32.setPadding(5, 0, 0, 40);
        tv32.setGravity(Gravity.CENTER);
        tr3.addView(tv32);

        tv33 = new EditText(this);
        tv33.setWidth(20 * dip);
        tv33.setTextColor(Color.BLACK);
        tv33.setPadding(5, 0, 0, 40);
        tv33.setGravity(Gravity.CENTER);
        tv33.setInputType(InputType.TYPE_CLASS_NUMBER);
        tr3.addView(tv33);

        tv34 = new EditText(this);
        tv34.setWidth(20 * dip);
        tv34.setTextColor(Color.BLACK);
        tv34.setPadding(5, 0, 0, 40);
        tv34.setGravity(Gravity.CENTER);
        tr3.addView(tv34);

        t2.addView(tr3, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        //4행
        TableRow tr4 = new TableRow(this);
        tr4.setGravity(Gravity.CENTER);
        tr4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr4.setBackgroundColor(Color.GRAY);
        //tr4.setId(20);


        TextView tv40 = new TextView(this);
        tv40 = new TextView(this);
        tv40.setHeight(75*dip);
        tv40.setText("4");
        tv40.setWidth(20 * dip);
        tv40.setTextColor(Color.WHITE);
        tv40.setGravity(Gravity.CENTER);
        tv40.setPadding(5, 5, 0, 0);
        tr4.addView(tv40);


        tv41 = new EditText(this);
        tv41.setWidth(20 * dip);
        tv41.setTextColor(Color.WHITE);
        tv41.setPadding(5, 0, 0, 40);
        tv41.setGravity(Gravity.CENTER);
        tr4.addView(tv41);

        tv42 = new EditText(this);
        tv42.setWidth(20 * dip);
        tv42.setTextColor(Color.WHITE);
        tv42.setPadding(5, 0, 0, 40);
        tv42.setGravity(Gravity.CENTER);
        tr4.addView(tv42);

        tv43 = new EditText(this);
        tv43.setWidth(20 * dip);
        tv43.setTextColor(Color.WHITE);
        tv43.setPadding(5, 0, 0, 40);
        tv43.setGravity(Gravity.CENTER);
        tv43.setInputType(InputType.TYPE_CLASS_NUMBER);
        tr4.addView(tv43);

        tv44 = new EditText(this);
        tv44.setWidth(20 * dip);
        tv44.setTextColor(Color.WHITE);
        tv44.setPadding(5, 0, 0, 40);
        tv44.setGravity(Gravity.CENTER);
        tr4.addView(tv44);
        t2.addView(tr4, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

    }
}


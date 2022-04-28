package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * 1. 해당 클래스는 작업지시 목록을 보인다.
 * 2. 항목을 클릭할 경우, 서버와 통신하여 해당 데이터를 가져온다.
 */
public class SuListViewActivity extends BaseActivity {

    private ListView listView1;
    //private SuWorder[] items;

    TextView textViewUserName;

    String type = "";
    String restURL = "";
    String arrayName = "";
    ArrayList<SuWorder> suWorders;
    SwListVIewAdapter adapter;


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
        setContentView(R.layout.listview);

        textViewUserName=findViewById(R.id.textViewUserName);
        textViewUserName.setText(Users.UserName);
        type = getIntent().getStringExtra("type");
        restURL = getIntent().getStringExtra("url");
        arrayName = getIntent().getStringExtra("arrayName");

        makeSWList();

        listView1 = (ListView) findViewById(R.id.listView1);

        adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, suWorders);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);

        if(Users.BusinessClassCode==11) {//창녕용
            listView1.setOnItemClickListener(mItemClickListener);
        }
        else {//음성용
            listView1.setOnItemClickListener(mItemClickListener2);
            textViewUserName.setVisibility(View.GONE);
        }

        progressOFF();

    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startProgress();

            String key = suWorders.get(position).WorkNo;

            if (type.equals("배정")) {

                Intent intent = new Intent(SuListViewActivity.this, AssignUserActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "배정");
                intent.putExtra("seqNo", position);
                startActivityForResult(intent, 1);
            }
            else if (type.equals("확인")) {
                Intent intent = new Intent(SuListViewActivity.this, ViewActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "확인");
                intent.putExtra("seqNo", position);
                startActivityForResult(intent, 1);
            }
            else {
                Intent intent = new Intent(getBaseContext(), ViewActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "작업");
                intent.putExtra("seqNo", position);
                startActivity(intent);
            }
        }
    };


    /**
     * 음성용 어뎁터 클릭 리스너
     */
    AdapterView.OnItemClickListener mItemClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startProgress();

            String key = suWorders.get(position).WorkNo;

            if (type.equals("배정")) {

                Intent intent = new Intent(SuListViewActivity.this, AssignUserActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "배정");
                intent.putExtra("seqNo", position );
                startActivityForResult(intent, 1);
            }
            else if (type.equals("확인")) {//일보확인: 음성용은 작업요청서 거치지 않고, 바로 작업일보로 간다.
                Intent intent = new Intent(SuListViewActivity.this, RegisterActivity2.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "확인");
                startActivityForResult(intent, 1);
            }
            else {
                Intent intent = new Intent(getBaseContext(), ViewActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("type", "작업");
                intent.putExtra("seqNo", position );
                startActivity(intent);
            }
        }
    };

    private void makeSWList() {

        suWorders = (ArrayList<SuWorder>) getIntent().getSerializableExtra("data");
    }

    /**
     * 결과 인텐트를 받아온다.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            int seqNo = data.getIntExtra("seqNo", -1);
            suWorders.get(seqNo).Supervisor  = data.getStringExtra("SupervisorName");
            //data.getStringExtra("SupervisorCode");
            this.adapter.notifyDataSetChanged();
        }
    }
}

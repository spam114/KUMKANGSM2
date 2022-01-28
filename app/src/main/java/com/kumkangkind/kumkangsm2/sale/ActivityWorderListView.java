package com.kumkangkind.kumkangsm2.sale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.SuWorder;
import com.kumkangkind.kumkangsm2.Users;

import java.util.ArrayList;


/**
 * 1. 해당 클래스는 작업지시 목록을 보인다.
 * 2. 항목을 클릭할 경우, 서버와 통신하여 해당 데이터를 가져온다.
 */
public class ActivityWorderListView extends BaseActivity {

    private ListView listView1;


    String type = "";
    String restURL = "";
    String arrayName = "";
    ArrayList<SuWorder> suWorders;
    AdapterWorderListView adapter;
    TextView textViewUserName;


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

        adapter = new AdapterWorderListView(ActivityWorderListView.this, R.layout.listview_row, suWorders);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);

    }

    public void mOnClick(View v) {

        finish();
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String key = suWorders.get(position).WorkNo;
            Intent intent = new Intent(ActivityWorderListView.this, ActivitySaleView.class);
            intent.putExtra("key", key);
            intent.putExtra("seqNo", position);
            startActivityForResult(intent, 1);
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

            if(seqNo != ListView.INVALID_POSITION) {
                suWorders.remove(seqNo);
                this.adapter.notifyDataSetChanged();
            }
        }
    }
}

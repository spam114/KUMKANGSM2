package com.kumkangkind.kumkangsm2.sale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;

import java.util.ArrayList;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerListActivity extends BaseActivity {

    ArrayList<Customers> customersArrayList;

    ListView listview;
    CustomerListViewAdapter adapter;
    Customers currentItem;
    EditText editTextSearch;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_customer);

        MakeListView();

        listview = (ListView) findViewById(R.id.listViewCustomer);
        View header = (View) getLayoutInflater().inflate(R.layout.listview_customer_row, null);
        listview.addHeaderView(header);

        adapter = new CustomerListViewAdapter(CustomerListActivity.this, R.layout.listview_customer_row, customersArrayList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(mItemClickListener);

        editTextSearch = (EditText)findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editTextSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0)
                return;
            //현재내용을 받아온 후 종료해야함
            currentItem = customersArrayList.get(position - 1);
            Intent i = getIntent();
            i.putExtra("customer",currentItem );
            setResult(RESULT_OK, i);
            finish();
        }
    };

    private ArrayList<Customers> MakeData(){

        ArrayList<Customers> data = new ArrayList<Customers>();
        data.add(new Customers("대우건설(주)", "시흥지구 벽산 1단지", "12345"));
        data.add(new Customers("후식건설(주)", "시흥지구 벽산 2단지 110-704", "123456"));
        data.add(new Customers("보욱건설(주)", "시흥지구 벽산 3단지", "123457"));
        data.add(new Customers("연경건설(주)", "강남 역삼동 다리", "12345-31"));
        data.add(new Customers("현진건설(주)", "강남 역삼동 빌딩", "12345-23"));
        data.add(new Customers("현대건설(주)", "강남 역삼동 롯데타워", "12345-3-1"));
        return data;
    }

    /**
     * ListView will be made
     */
    private void MakeListView(){
//        customersArrayList =  MakeData();

        customersArrayList = (ArrayList<Customers>) getIntent().getSerializableExtra("customers");
    }
}

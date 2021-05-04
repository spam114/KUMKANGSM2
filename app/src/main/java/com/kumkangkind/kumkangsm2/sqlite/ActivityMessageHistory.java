package com.kumkangkind.kumkangsm2.sqlite;

import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityMessageHistory extends BaseActivity{

    DbOpenHelper mHelper;
    Cursor mCursor;
    SimpleCursorAdapter Adapter;

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_message_history);

        //커서
        mHelper = new DbOpenHelper(this);
        //DB
        mHelper.open();
        SQLiteDatabase db = mHelper.mDBHelper.getWritableDatabase();
        //쿼리
        mCursor = db.rawQuery("Select * From mailbox order by time desc ", null);

        startManagingCursor(mCursor);

        Adapter = null;
        Adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_2,
                mCursor, new String[]{"header", "time"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView list = (ListView)findViewById(R.id.ListViewMessage);
        list.setAdapter(Adapter);
        list.setOnItemClickListener(mItemClickListener);

        Button button = (Button)findViewById(R.id.btnClear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHelper.deleteAll();
               finish();
            }
        });

        progressOFF();
    }


    /**
     * 아이템을 선택할 시 작동합니다.
     */
    AdapterView.OnItemClickListener mItemClickListener =
            new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    //헤더를 선택할 경우에는 무시한다.
//					if(position == 0)
//						return;
                    mCursor.moveToPosition(position);

                    //선택한 아이템을 표현할 수 있는 메시지를 만든다.
                    String mes =  mCursor.getString(mCursor.getColumnIndex("contents"));
                    String mes1 = mCursor.getString(mCursor.getColumnIndex("time"));
                    String mes2 = mCursor.getString(mCursor.getColumnIndex("header"));

                    //메시지를 보여준다.
                    //Extra를 넣은 후에 폼을 종료합니다.


                    new AlertDialog.Builder(ActivityMessageHistory.this).setMessage(mes)
                            .setTitle(mes2).show();

                    /*
                    Intent i =  getIntent();


                    i.putExtra("contents", mes );
                    i.putExtra("time", mes1 );
                    i.putExtra("header", mes2 );
                    finish();

*/
                }

            };
}

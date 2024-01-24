package com.kumkangkind.kumkangsm2.sqlite;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kumkangkind.kumkangsm2.ActivityStockInCertificateDetail;
import com.kumkangkind.kumkangsm2.BaseActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.RegisterActivityReturn;
import com.kumkangkind.kumkangsm2.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


public class ActivityMessageHistory extends BaseActivity {

    DbOpenHelper mHelper;
    Cursor mCursor;
    SimpleCursorAdapter Adapter;

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

        ListView list = (ListView) findViewById(R.id.ListViewMessage);
        list.setAdapter(Adapter);
        list.setOnItemClickListener(mItemClickListener);

        Button button = (Button) findViewById(R.id.btnClear);
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
                    @SuppressLint("Range") String mes = mCursor.getString(mCursor.getColumnIndex("contents"));
                    @SuppressLint("Range") String mes1 = mCursor.getString(mCursor.getColumnIndex("time"));
                    @SuppressLint("Range") String mes2 = mCursor.getString(mCursor.getColumnIndex("header"));
                    @SuppressLint("Range") String certificateNo = mCursor.getString(mCursor.getColumnIndex("certificateNo"));

                    //메시지를 보여준다.
                    //Extra를 넣은 후에 폼을 종료합니다.
                    if (!certificateNo.equals("")) {
                        if (!certificateNo.substring(0, 2).equals("SW")) {
                            //송장 클릭
                            new MaterialAlertDialogBuilder(ActivityMessageHistory.this)
                                    .setTitle(mes2)
                                    .setMessage(mes)
                                    .setCancelable(true)
                                    .setPositiveButton
                                            ("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            }).setNegativeButton("송장보기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getLocationInfoByCertificateNo(certificateNo);
                                            return;
                                        }
                                    }).show();
                        } else {//SW로 시작한다고 본다. 일보번호
                            new MaterialAlertDialogBuilder(ActivityMessageHistory.this)
                                    .setTitle(mes2)
                                    .setMessage(mes)
                                    .setCancelable(true)
                                    .setPositiveButton
                                            ("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            }).setNegativeButton("회수일보", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ActivityMessageHistory.this, RegisterActivityReturn.class);
                                            intent.putExtra("type", "작업");
                                            intent.putExtra("key", certificateNo);
                                            intent.putExtra("inputUser", "-1");
                                            startActivityForResult(intent, 2);
                                            return;
                                        }
                                    }).show();
                        }
                    } else {//작업요청서 클릭
                        new AlertDialog.Builder(ActivityMessageHistory.this).setMessage(mes)
                                .setTitle(mes2).show();
                    }

                    /*
                    Intent i =  getIntent();


                    i.putExtra("contents", mes );
                    i.putExtra("time", mes1 );
                    i.putExtra("header", mes2 );
                    finish();

*/
                }

            };

    private void getLocationInfoByCertificateNo(String certificateNo) {
        String url = getString(R.string.service_address) + "getLocationInfoByCertificateNo";
        ContentValues values = new ContentValues();
        values.put("CertificateNo", certificateNo);
        GetLocationInfoByCertificateNo gsod = new GetLocationInfoByCertificateNo(url, values);
        gsod.execute();
    }

    public class GetLocationInfoByCertificateNo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetLocationInfoByCertificateNo(String url, ContentValues values) {
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
            try {
                //WoImage image;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //stockArrayList = new ArrayList<>();
                String certificateNo = "";
                String customerLocationName = "";
                String locationNo = "";
                String supervisorCode = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityMessageHistory.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    certificateNo = child.getString("CertificateNo");
                    customerLocationName = child.getString("CustomerLocationName");
                    locationNo = child.getString("LocationNo");
                    supervisorCode = child.getString("SupervisorCode");
                }

                Intent i;
                i = new Intent(getBaseContext(), ActivityStockInCertificateDetail.class);//todo
                i.putExtra("certificateNo", certificateNo);
                i.putExtra("customerLocationName", customerLocationName);
                i.putExtra("locationNo", locationNo);
                i.putExtra("supervisorCode", supervisorCode);

                startActivity(i);
            } catch (Exception e) {

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

}

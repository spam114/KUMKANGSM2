package com.kumkangkind.kumkangsm2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * 해당 액티비티는 등록 창에서 EditText를 누를때 이곳으로
 * 이동하여 글씨를 입력하기 편하도록 만든 액티비티이다
 */
public class EditTextActivity extends BaseActivity {

    TextView textViewTitle;
    EditText editTextContent;
    private String titleBackup;
    private String contentBackup;
    private int resultCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edittext);


        textViewTitle = (TextView) findViewById(R.id.textViewtitle);
        editTextContent = (EditText) findViewById(R.id.editTextContent);
        titleBackup = getIntent().getStringExtra("title");
        contentBackup = getIntent().getStringExtra("content");
        textViewTitle.setText(titleBackup);
        editTextContent.setText(contentBackup);

        progressOFF();
    }

    @Override
    protected void attachBaseContext(Context newBase) {//글씨체 적용
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }


    public void mOnClick(View v) {

        switch (v.getId()) {

            case R.id.btnCancel:
                makeResultIntent(contentBackup, 5);
                break;

            case R.id.btnOK:
                makeResultIntent(editTextContent.getText().toString(), RESULT_OK);
                break;
        }
    }

    /**
     * @param content
     * @return
     */
    private void makeResultIntent(String content, int resultCode) {

        Intent intent = getIntent();
        intent.putExtra("content", editTextContent.getText().toString());
        setResult(resultCode, intent);
        finish();
    }
}

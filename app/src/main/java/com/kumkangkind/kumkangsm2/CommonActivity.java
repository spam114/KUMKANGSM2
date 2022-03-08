package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
import java.util.ArrayList;



public class CommonActivity extends BaseActivity {
    String contractNo;
    private static HorizontalScrollView Scroll_Horizontal;
    private static ScrollView Scroll_Vertical;
    protected static int currentX = 0;
    protected static int currentY = 0;

    Button btnSave;
    Button btnCancel;

    String Message = "";//UPDATE시 오류 확인용
    String ResultCode = "";

    LinearLayout dynamicLayout;
    TextView addressText;
    ArrayList<ProgressInfo> progressInfoArrayList;
    int size;


    TextView tvDong;//동명
    ArrayList<TextView> dongList;

    TextView tvProgressFloor;//현재층수(진행층)
    ArrayList<TextView> progressFloorList;

    TextView tvGangFormDate;//갱폼시공일
    ArrayList<TextView> gangFormDateList;

    TextView tvSettingStart;//셋팅시작일
    ArrayList<TextView> settingStartList;

    TextView tvSettingEnd;//셋팅종료일
    ArrayList<TextView> settingEndList;

    TextView tvSettingStart2;//셋팅시작일(계단피로티)
    ArrayList<TextView> settingStart2List;

    TextView tvSettingEnd2;//셋팅종료일(계단피로티)
    ArrayList<TextView> settingEnd2List;

    TextView tvWallProcess;//WALL공정
    ArrayList<TextView> wallProcessList;

    TextView tvSLProcess;//SLAB공정
    ArrayList<TextView> slProcessList;

    TextView tvSteelProcess;//STEEL공정
    ArrayList<TextView> steelProcessList;

    String type="";
    String statusFlag="";

    boolean token;//update시 문제가 생기는지 파악하는 token

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        progressInfoArrayList = new ArrayList<>();
        //container.addView(zoomView);
        contractNo = getIntent().getStringExtra("contractNo");
        type=getIntent().getStringExtra("type");
        statusFlag=getIntent().getStringExtra("statusFlag");
        GetData();

        btnCancel=findViewById(R.id.btnCancel);
        btnSave=findViewById(R.id.btnSave);

        if(type.equals("확인") || statusFlag.equals("2")){
            btnCancel.setText("");
            btnCancel.setClickable(false);
            btnSave.setText("");
            btnSave.setClickable(false);
        }

        progressOFF();
    }

    private String GetAbbreviation(String CustomerName) {
        if (CustomerName.contains("주식회사"))
            return CustomerName.replace("주식회사", "").trim();
        else if (CustomerName.contains("(주)"))
            return CustomerName.replace("(주)", "").trim();
        else
            return CustomerName;
    }


    private void GetData() {
        new GetCommonProgressInfoByPost().execute(getString(R.string.service_address)+"getCommonProgressInfo");
    }


    private class GetCommonProgressInfoByPost extends AsyncTask<String, Void, String> {//todo
/*
        String customerLocation="";

        public GetDongByPost(String customerName,String locationName){
            customerLocation=customerName+"-"+locationName;
        }*/

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                String Dong = "";
                String ProgressFloor = "";
                String GangFormDate = "";
                String SettingStart = "";
                String SettingEnd = "";
                String SettingStart2 = "";
                String SettingEnd2 = "";
                String WallProcess = "";
                String SLProcess = "";
                String SteelProcess = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    Dong = child.getString("Dong");
                    ProgressFloor = child.getString("ProgressFloor");
                    GangFormDate = child.getString("GangFormDate");
                    SettingStart = child.getString("SettingStart");
                    SettingEnd = child.getString("SettingEnd");
                    SettingStart2 = child.getString("SettingStart2");
                    SettingEnd2 = child.getString("SettingEnd2");
                    WallProcess = child.getString("WallProcess");
                    SLProcess = child.getString("SLProcess");
                    SteelProcess = child.getString("SteelProcess");
                    progressInfoArrayList.add(MakeData(i, Dong, ProgressFloor, GangFormDate, SettingStart, SettingEnd, SettingStart2, SettingEnd2, WallProcess, SLProcess, SteelProcess));
                }

                DrawView();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public ProgressInfo MakeData(int No, String Dong, String ProgressFloor, String GangFormDate, String SettingStart, String SettingEnd, String SettingStart2, String SettingEnd2,
                                 String WallProcess, String SLProcess, String SteelProcess) {
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.No = No;
        progressInfo.Dong = Dong;
        progressInfo.ProgressFloor = ProgressFloor;
        progressInfo.GangFormDate = GangFormDate;
        progressInfo.SettingStart = SettingStart;
        progressInfo.SettingEnd = SettingEnd;
        progressInfo.SettingStart2 = SettingStart2;
        progressInfo.SettingEnd2 = SettingEnd2;
        progressInfo.WallProcess = WallProcess;
        progressInfo.SLProcess = SLProcess;
        progressInfo.SteelProcess = SteelProcess;
        return progressInfo;

    }


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
            jsonObject.put("ContractNo", contractNo);//계약번호

            json = jsonObject.toString();
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json, "UTF-8");
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the of the content
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
            //.i("JSON", result);
            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }


    public void mOnClick(View v) {
        switch (v.getId()) {

            case R.id.btnSave:
                ShowSaveDialog();
                break;

            case R.id.btnCancel:
                finish();
                break;

        }
    }

    /*
     * 저장 확인 다이얼로그를 부른다.
     * */
    public void ShowSaveDialog() {

        new AlertDialog.Builder(this)

                .setTitle("데이터 적용")
                .setMessage("변경 된 진행정보를 저장하시겠습니까?")
                //.setIcon(R.drawable.ninja)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                startProgress();
                                token=false;

                                for (int i = 0; i < progressInfoArrayList.size(); i++) {

                                    new SetCommonProgressInfoByPost(i).execute(getString(R.string.service_address)+"setCommonProgressInfo");

                                }

                            }
                        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                }).show();

    }

    private class SetCommonProgressInfoByPost extends AsyncTask<String, Void, String> {//todo


        int seqNo;

        public SetCommonProgressInfoByPost(int seqNo) {
            String test=progressInfoArrayList.get(0).ContractNo;
            int n=3;
            this.seqNo = seqNo;
        }

        @Override
        protected String doInBackground(String... urls) {

            return POST_Set_Progress_Info(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    Message = child.getString("Message");
                    ResultCode = child.getString("ResultCode");
                }
                if (ResultCode.equals("false")) {
                    Toast.makeText(CommonActivity.this, Message, Toast.LENGTH_LONG).show();
                    token=true;
                    return;
                }

                if(token==false && seqNo==progressInfoArrayList.size()-1){//문제가없을시 && 마지막 쓰레드
                    Toast.makeText(CommonActivity.this, "등록이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    progressOFF();
                }

            } catch (Exception e) {
            }
        }

        public String POST_Set_Progress_Info(String url) {

            String Dong = progressInfoArrayList.get(seqNo).Dong;

            String ProgressFloor = progressInfoArrayList.get(seqNo).ProgressFloor;
            String GangFormDate = progressInfoArrayList.get(seqNo).GangFormDate;
            String SettingStart = progressInfoArrayList.get(seqNo).SettingStart;
            String SettingEnd = progressInfoArrayList.get(seqNo).SettingEnd;
            String SettingStart2 = progressInfoArrayList.get(seqNo).SettingStart2;
            String SettingEnd2 = progressInfoArrayList.get(seqNo).SettingEnd2;

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
                jsonObject.put("ContractNo", contractNo);
                jsonObject.put("Dong", Dong);
                jsonObject.put("ProgressFloor", ProgressFloor);
                jsonObject.put("GangFormDate", GangFormDate);
                jsonObject.put("SettingStart", SettingStart);
                jsonObject.put("SettingEnd", SettingEnd);
                jsonObject.put("SettingStart2", SettingStart2);
                jsonObject.put("SettingEnd2", SettingEnd2);

                json = jsonObject.toString();
                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json, "UTF-8");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the of the content
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
            return result;
        }

    }


    private void DrawView() {

        TextView tvCustomerLocation;
        tvCustomerLocation=findViewById(R.id.tvCustomerLocation);
        tvCustomerLocation.setText(getIntent().getStringExtra("customerLocation"));

        dongList = new ArrayList<>();
        progressFloorList = new ArrayList<>();
        gangFormDateList = new ArrayList<>();
        settingStartList = new ArrayList<>();
        settingEndList = new ArrayList<>();
        settingStart2List = new ArrayList<>();
        settingEnd2List = new ArrayList<>();
        wallProcessList = new ArrayList<>();
        slProcessList = new ArrayList<>();
        steelProcessList = new ArrayList<>();


        // textBMan.setTextColor(Color.RED); 빨간글자~
        /* hScroll=findViewById(R.id.hsView);*/
        int dip = Math.round(getResources().getDisplayMetrics().density);

        size = progressInfoArrayList.size();
        dynamicLayout = findViewById(R.id.dynamicLayout);


        LinearLayout linearVer = new LinearLayout(this);
        linearVer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearVerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dynamicLayout.addView(linearVer, linearVerParams);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);//TextView에 적용할 params
        textParams.setMargins(0, 40, 40, 0);//글자간의 간격

        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1 * dip);//구분선(가로)에 적용할 params
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#BDBDBD"));

        LinearLayout.LayoutParams lineSeroParams = new LinearLayout.LayoutParams(1 * dip, LinearLayout.LayoutParams.MATCH_PARENT);//구분선(세로)에 적용할 params
        View viewSero = new View(this);
        viewSero.setBackgroundColor(Color.parseColor("#BDBDBD"));

        TextView textProgressFloor = findViewById(R.id.progressFloor);
        TextView textWallProcess = findViewById(R.id.wall);
        TextView textSLProcess = findViewById(R.id.slab);
        TextView textSteelProcess = findViewById(R.id.steel);
        TextView textGangFormDate = findViewById(R.id.gangFormSetting);
        TextView textSettingStart = findViewById(R.id.settingStart);
        TextView textSettingEnd = findViewById(R.id.settingEnd);
        TextView textSettingStart2 = findViewById(R.id.settingStart2);
        TextView textSettingEnd2 = findViewById(R.id.settingEnd2);


        progressFloorList.add(0, textProgressFloor);
        wallProcessList.add(0, textWallProcess);
        slProcessList.add(0, textSLProcess);
        steelProcessList.add(0, textSteelProcess);
        gangFormDateList.add(0, textGangFormDate);
        settingStartList.add(0, textSettingStart);
        settingEndList.add(0, textSettingEnd);
        settingStart2List.add(0, textSettingStart2);
        settingEnd2List.add(0, textSettingEnd2);


        textProgressFloor.setOnClickListener(setRedTextOnClickListener);
        textWallProcess.setOnClickListener(setRedTextOnClickListener);
        textSLProcess.setOnClickListener(setRedTextOnClickListener);
        textSteelProcess.setOnClickListener(setRedTextOnClickListener);
        textGangFormDate.setOnClickListener(setRedTextOnClickListener);
        textSettingStart.setOnClickListener(setRedTextOnClickListener);
        textSettingEnd.setOnClickListener(setRedTextOnClickListener);
        textSettingStart2.setOnClickListener(setRedTextOnClickListener);
        textSettingEnd2.setOnClickListener(setRedTextOnClickListener);
     /*   TextView textDong = new TextView(this);
        textDong.setText("동명");
        textDong.setTag("동명");
        textDong.setTextSize(20);
        textDong.setTextColor(Color.BLUE);
        textDong.setOnClickListener(setRedTextOnClickListener);
        linearVer.addView(textDong, textParams);
        linearVer.addView(view, lineParams);

        TextView textProgressFloor = new TextView(this);
        textProgressFloor.setText("현재층수");
        textProgressFloor.setTag("현재층수");
        textProgressFloor.setPaintFlags(textProgressFloor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textProgressFloor.setTextSize(20);
        textProgressFloor.setOnClickListener(setRedTextOnClickListener);
        progressFloorList.add(0, textProgressFloor);
        linearVer.addView(textProgressFloor, textParams);

        TextView textWallProcess = new TextView(this);
        textWallProcess.setText("WALL공정");
        textWallProcess.setTag("WALL공정");
        textWallProcess.setTextSize(20);
        textWallProcess.setOnClickListener(setRedTextOnClickListener);
        wallProcessList.add(0, textWallProcess);
        linearVer.addView(textWallProcess, textParams);

        TextView textSLProcess = new TextView(this);
        textSLProcess.setText("SLAB공정");
        textSLProcess.setTag("SLAB공정");
        textSLProcess.setTextSize(20);
        textSLProcess.setOnClickListener(setRedTextOnClickListener);
        slProcessList.add(0, textSLProcess);
        linearVer.addView(textSLProcess, textParams);

        TextView textSteelProcess = new TextView(this);
        textSteelProcess.setText("STEEL공정");
        textSteelProcess.setTag("STEEL공정");
        textSteelProcess.setPaintFlags(textSteelProcess.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textSteelProcess.setTextSize(20);
        textSteelProcess.setOnClickListener(setRedTextOnClickListener);
        steelProcessList.add(0, textSteelProcess);
        linearVer.addView(textSteelProcess, textParams);


        TextView textGangFormDate = new TextView(this);
        textGangFormDate.setText("갱폼시공일");
        textGangFormDate.setTag("갱폼시공일");
        textGangFormDate.setTextSize(20);
        textGangFormDate.setOnClickListener(setRedTextOnClickListener);
        gangFormDateList.add(0, textGangFormDate);
        linearVer.addView(textGangFormDate, textParams);

        TextView textSettingStart = new TextView(this);
        textSettingStart.setText("셋팅시작일");
        textSettingStart.setTag("셋팅시작일");
        textSettingStart.setTextSize(20);
        textSettingStart.setOnClickListener(setRedTextOnClickListener);
        settingStartList.add(0, textSettingStart);
        linearVer.addView(textSettingStart, textParams);

        TextView textSettingEnd = new TextView(this);
        textSettingEnd.setText("셋팅종료일");
        textSettingEnd.setTag("셋팅종료일");
        textSettingEnd.setPaintFlags(textSettingEnd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textSettingEnd.setTextSize(20);
        textSettingEnd.setOnClickListener(setRedTextOnClickListener);
        settingEndList.add(0, textSettingEnd);
        linearVer.addView(textSettingEnd, textParams);

        TextView textSettingStart2 = new TextView(this);
        textSettingStart2.setText("셋팅시작일(계단피로티)");
        textSettingStart2.setTag("셋팅시작일(계단피로티)");
        textSettingStart2.setPaintFlags(textSettingStart2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textSettingStart2.setTextSize(20);
        textSettingStart2.setOnClickListener(setRedTextOnClickListener);
        settingStart2List.add(0, textSettingStart2);
        linearVer.addView(textSettingStart2, textParams);

        TextView textSettingEnd2 = new TextView(this);
        textSettingEnd2.setText("셋팅종료일(계단피로티)");
        textSettingEnd2.setTag("셋팅종료일(계단피로티)");
        textSettingEnd2.setPaintFlags(textSettingEnd2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textSettingEnd2.setTextSize(20);
        textSettingEnd2.setOnClickListener(setRedTextOnClickListener);
        settingEnd2List.add(0, textSettingEnd2);
        linearVer.addView(textSettingEnd2, textParams);

        dynamicLayout.addView(viewSero, lineSeroParams);*/
        dynamicLayout.addView(viewSero, lineSeroParams);

        for (int i = 0; i < size; i++) {

            LinearLayout lv = new LinearLayout(this);
            lv.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lvp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            dynamicLayout.addView(lv, lvp);

            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);//TextView에 적용할 params
            //tvParams.setMargins(10*dip, 20*dip, 10*dip, 0);

            LinearLayout.LayoutParams tvProgressParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);//진행층수 TextView에 적용할 params
            //tvProgressParams.setMargins(10*dip, 20*dip, 10*dip, 0);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1 * dip);//구분선에 적용할 params
            View line = new View(this);
            line.setBackgroundColor(Color.parseColor("#BDBDBD"));

            LinearLayout.LayoutParams lsp = new LinearLayout.LayoutParams(1 * dip, LinearLayout.LayoutParams.MATCH_PARENT);//구분선(세로)에 적용할 params
            View lineSero = new View(this);
            lineSero.setBackgroundColor(Color.parseColor("#BDBDBD"));

            Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/sangsang.ttf");//글씨체
            tvDong = new TextView(this);//동
            tvDong.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvDong.setText(progressInfoArrayList.get(i).Dong);
            tvDong.setTextSize(20);
            tvDong.setTextColor(Color.BLUE);
            tvDong.setTypeface(typeFace);
            lv.addView(tvDong, tvParams);
            lv.addView(line, lp);

            tvProgressFloor = new TextView(this);//현재층수
            tvProgressFloor.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
           /* if(Integer.parseInt(progressInfoArrayList.get(i).ProgressFloor)==0) {
                tvProgressFloor.setText("");
            }*/
            tvProgressFloor.setText(ChangeEmptyString(progressInfoArrayList.get(i).ProgressFloor));
            tvProgressFloor.setTextSize(20);
            tvProgressFloor.setTypeface(typeFace);
            tvProgressFloor.setTag("현재층수");
            tvProgressFloor.setTag(R.id.key_1, i);
            tvProgressFloor.setOnClickListener(dataClickListener);
            progressFloorList.add(i + 1, tvProgressFloor);
            lv.addView(tvProgressFloor, tvProgressParams);

            tvWallProcess = new TextView(this);//WALL공정
            tvWallProcess.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvWallProcess.setText(progressInfoArrayList.get(i).WallProcess);
            tvWallProcess.setTextSize(20);
            tvWallProcess.setTypeface(typeFace);
            tvWallProcess.setTag("WALL공정");
            tvWallProcess.setTag(R.id.key_1, i);
            wallProcessList.add(i + 1, tvWallProcess);
            lv.addView(tvWallProcess, tvParams);

            tvSLProcess = new TextView(this);//SLAB공정
            tvSLProcess.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSLProcess.setText(progressInfoArrayList.get(i).SLProcess);
            tvSLProcess.setTextSize(20);
            tvSLProcess.setTypeface(typeFace);
            tvSLProcess.setTag("SLAB공정");
            tvSLProcess.setTag(R.id.key_1, i);
            slProcessList.add(i + 1, tvSLProcess);
            lv.addView(tvSLProcess, tvParams);

            tvSteelProcess = new TextView(this);//STEEL공정
            tvSteelProcess.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSteelProcess.setText(progressInfoArrayList.get(i).SteelProcess);
            tvSteelProcess.setTextSize(20);
            tvSteelProcess.setTypeface(typeFace);
            tvSteelProcess.setTag("STEEL공정");
            tvSteelProcess.setTag(R.id.key_1, i);
            steelProcessList.add(i + 1, tvSteelProcess);
            lv.addView(tvSteelProcess, tvParams);

            tvGangFormDate = new TextView(this);//갱폼셋팅일
            tvGangFormDate.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvGangFormDate.setText(ChangeEmptyString(progressInfoArrayList.get(i).GangFormDate));
            tvGangFormDate.setTextSize(20);
            tvGangFormDate.setTypeface(typeFace);
            tvGangFormDate.setTag("갱폼시공일");
            tvGangFormDate.setTag(R.id.key_1, i);
            tvGangFormDate.setOnClickListener(dataClickListener);
            gangFormDateList.add(i + 1, tvGangFormDate);
            lv.addView(tvGangFormDate, tvParams);

            tvSettingStart = new TextView(this);//알폼시작
            tvSettingStart.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSettingStart.setText(ChangeEmptyString(progressInfoArrayList.get(i).SettingStart));
            tvSettingStart.setTextSize(20);
            tvSettingStart.setTypeface(typeFace);
            tvSettingStart.setTag("셋팅시작일");
            tvSettingStart.setTag(R.id.key_1, i);
            tvSettingStart.setOnClickListener(dataClickListener);
            settingStartList.add(i + 1, tvSettingStart);
            lv.addView(tvSettingStart, tvParams);

            tvSettingEnd = new TextView(this);//알폼종료
            tvSettingEnd.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSettingEnd.setText(ChangeEmptyString(progressInfoArrayList.get(i).SettingEnd));
            tvSettingEnd.setTextSize(20);
            tvSettingEnd.setTypeface(typeFace);
            tvSettingEnd.setTag("셋팅종료일");
            tvSettingEnd.setTag(R.id.key_1, i);
            tvSettingEnd.setOnClickListener(dataClickListener);
            settingEndList.add(i + 1, tvSettingEnd);
            lv.addView(tvSettingEnd, tvParams);

            tvSettingStart2 = new TextView(this);//계단,피로티시작
            tvSettingStart2.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSettingStart2.setText(ChangeEmptyString(progressInfoArrayList.get(i).SettingStart2));
            tvSettingStart2.setTextSize(20);
            tvSettingStart2.setTypeface(typeFace);
            tvSettingStart2.setTag("셋팅시작일(계단피로티)");
            tvSettingStart2.setTag(R.id.key_1, i);
            tvSettingStart2.setOnClickListener(dataClickListener);
            settingStart2List.add(i + 1, tvSettingStart2);
            lv.addView(tvSettingStart2, tvParams);

            tvSettingEnd2 = new TextView(this);//계단,피로티종료
            tvSettingEnd2.setPadding(10 * dip, 20 * dip, 10 * dip, 0);
            tvSettingEnd2.setText(ChangeEmptyString(progressInfoArrayList.get(i).SettingEnd2));
            tvSettingEnd2.setTextSize(20);
            tvSettingEnd2.setTypeface(typeFace);
            tvSettingEnd2.setTag("셋팅종료일(계단피로티)");
            tvSettingEnd2.setTag(R.id.key_1, i);
            tvSettingEnd2.setOnClickListener(dataClickListener);
            settingEnd2List.add(i + 1, tvSettingEnd2);
            lv.addView(tvSettingEnd2, tvParams);


            if(type.equals("확인") || statusFlag.equals("2")){
                tvProgressFloor.setClickable(false);
                tvGangFormDate.setClickable(false);
                tvSettingEnd.setClickable(false);
                tvSettingEnd2.setClickable(false);
                tvSettingStart.setClickable(false);
                tvSettingStart2.setClickable(false);
            }

            dynamicLayout.addView(lineSero, lsp);
        }
    }


    TextView.OnClickListener setRedTextOnClickListener = new View.OnClickListener() {//항목 이름 클릭시 붉게 변화시키기
        @Override
        public void onClick(View view) {

            if (view.getTag().toString().equals("동명")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = dongList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("현재층수")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = progressFloorList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("갱폼시공일")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = gangFormDateList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("셋팅시작일")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = settingStartList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("셋팅종료일")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = settingEndList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("셋팅시작일(계단피로티)")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = settingStart2List.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("셋팅종료일(계단피로티)")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = settingEnd2List.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }
            } else if (view.getTag().toString().equals("WALL공정")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = wallProcessList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }

            } else if (view.getTag().toString().equals("SLAB공정")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = slProcessList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }

            } else if (view.getTag().toString().equals("STEEL공정")) {
                for (int i = 0; i < size + 1; i++) {
                    TextView tv = steelProcessList.get(i);
                    int intColor = tv.getCurrentTextColor();
                    //String hexColor = String.format("#%8X", (0xFFFFFFFF& intColor));
                    if (intColor == -1979711488) {
                        tv.setTextColor(Color.RED);
                    } else if (intColor == -65536) {
                        tv.setTextColor(Color.parseColor("#8A000000"));
                    }
                }

            }

        }
    };


    TextView.OnClickListener dataClickListener = new View.OnClickListener() {//클릭이벤트

        @Override
        public void onClick(View view) {

            final TextView textView = (TextView) view;
            LayoutInflater inflater = getLayoutInflater();
            //res폴더>>layout폴더>>dialog_addmember.xml 레이아웃 리소스 파일로 View 객체 생성
            //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언

            final int seqNo = (Integer) textView.getTag(R.id.key_1);//순번

            if (textView.getTag().toString().equals("현재층수")) {

                final View dialogView = inflater.inflate(R.layout.dialog_input_number, null);
                TextView tv = dialogView.findViewById(R.id.textView);
                final EditText no = dialogView.findViewById(R.id.edtNumber);
                if (textView.getText().toString().equals(getText(R.string.clear_string)))
                    no.setText("0");
                else
                    no.setText(textView.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                tv.setText(view.getTag().toString());
                AlertDialog.Builder buider = new AlertDialog.Builder(view.getContext()); //AlertDialog.Builder 객체 생성
                //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = no.getText().toString();
                        if (inputString.equals(""))
                            inputString = getString(R.string.clear_string);
                        progressInfoArrayList.set(seqNo
                                , MakeData(seqNo
                                        , progressInfoArrayList.get(seqNo).Dong
                                        , no.getText().toString()
                                        , progressInfoArrayList.get(seqNo).GangFormDate
                                        , progressInfoArrayList.get(seqNo).SettingStart
                                        , progressInfoArrayList.get(seqNo).SettingEnd
                                        , progressInfoArrayList.get(seqNo).SettingStart2
                                        , progressInfoArrayList.get(seqNo).SettingEnd2
                                        , progressInfoArrayList.get(seqNo).WallProcess
                                        , progressInfoArrayList.get(seqNo).SLProcess
                                        , progressInfoArrayList.get(seqNo).SteelProcess));
                        textView.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        textView.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();
            } else {
                final View dialogView = inflater.inflate(R.layout.dialog_year_month_day, null);
                TextView tv = dialogView.findViewById(R.id.textView);
                tv.setText(view.getTag().toString());
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider = new AlertDialog.Builder(view.getContext()); //AlertDialog.Builder 객체 생성
                //  buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month = datePicker.getMonth() + 1;
                        String inputString = datePicker.getYear() + "-" + month + "-" + datePicker.getDayOfMonth();
                        if (inputString.equals(""))
                            inputString = getString(R.string.clear_string);


                        if (textView.getTag().toString().equals("갱폼시공일")) {
                            progressInfoArrayList.set(seqNo
                                    , MakeData(seqNo
                                            , progressInfoArrayList.get(seqNo).Dong
                                            , progressInfoArrayList.get(seqNo).ProgressFloor
                                            , ChangeEmptyString2(inputString)
                                            , progressInfoArrayList.get(seqNo).SettingStart
                                            , progressInfoArrayList.get(seqNo).SettingEnd
                                            , progressInfoArrayList.get(seqNo).SettingStart2
                                            , progressInfoArrayList.get(seqNo).SettingEnd2
                                            , progressInfoArrayList.get(seqNo).WallProcess
                                            , progressInfoArrayList.get(seqNo).SLProcess
                                            , progressInfoArrayList.get(seqNo).SteelProcess));
                            textView.setText(inputString);
                        } else if (textView.getTag().toString().equals("셋팅시작일")) {
                            progressInfoArrayList.set(seqNo
                                    , MakeData(seqNo
                                            , progressInfoArrayList.get(seqNo).Dong
                                            , progressInfoArrayList.get(seqNo).ProgressFloor
                                            , progressInfoArrayList.get(seqNo).GangFormDate
                                            , ChangeEmptyString2(inputString)
                                            , progressInfoArrayList.get(seqNo).SettingEnd
                                            , progressInfoArrayList.get(seqNo).SettingStart2
                                            , progressInfoArrayList.get(seqNo).SettingEnd2
                                            , progressInfoArrayList.get(seqNo).WallProcess
                                            , progressInfoArrayList.get(seqNo).SLProcess
                                            , progressInfoArrayList.get(seqNo).SteelProcess));
                            textView.setText(inputString);
                        } else if (textView.getTag().toString().equals("셋팅종료일")) {
                            progressInfoArrayList.set(seqNo
                                    , MakeData(seqNo
                                            , progressInfoArrayList.get(seqNo).Dong
                                            , progressInfoArrayList.get(seqNo).ProgressFloor
                                            , progressInfoArrayList.get(seqNo).GangFormDate
                                            , progressInfoArrayList.get(seqNo).SettingStart
                                            , ChangeEmptyString2(inputString)
                                            , progressInfoArrayList.get(seqNo).SettingStart2
                                            , progressInfoArrayList.get(seqNo).SettingEnd2
                                            , progressInfoArrayList.get(seqNo).WallProcess
                                            , progressInfoArrayList.get(seqNo).SLProcess
                                            , progressInfoArrayList.get(seqNo).SteelProcess));
                            textView.setText(inputString);
                        } else if (textView.getTag().toString().equals("셋팅시작일(계단피로티)")) {
                            progressInfoArrayList.set(seqNo
                                    , MakeData(seqNo
                                            , progressInfoArrayList.get(seqNo).Dong
                                            , progressInfoArrayList.get(seqNo).ProgressFloor
                                            , progressInfoArrayList.get(seqNo).GangFormDate
                                            , progressInfoArrayList.get(seqNo).SettingStart
                                            , progressInfoArrayList.get(seqNo).SettingEnd
                                            , ChangeEmptyString2(inputString)
                                            , progressInfoArrayList.get(seqNo).SettingEnd2
                                            , progressInfoArrayList.get(seqNo).WallProcess
                                            , progressInfoArrayList.get(seqNo).SLProcess
                                            , progressInfoArrayList.get(seqNo).SteelProcess));
                            textView.setText(inputString);
                        } else if (textView.getTag().toString().equals("셋팅종료일(계단피로티)")) {
                            progressInfoArrayList.set(seqNo
                                    , MakeData(seqNo
                                            , progressInfoArrayList.get(seqNo).Dong
                                            , progressInfoArrayList.get(seqNo).ProgressFloor
                                            , progressInfoArrayList.get(seqNo).GangFormDate
                                            , progressInfoArrayList.get(seqNo).SettingStart
                                            , progressInfoArrayList.get(seqNo).SettingEnd
                                            , progressInfoArrayList.get(seqNo).SettingStart2
                                            , ChangeEmptyString2(inputString)
                                            , progressInfoArrayList.get(seqNo).WallProcess
                                            , progressInfoArrayList.get(seqNo).SLProcess
                                            , progressInfoArrayList.get(seqNo).SteelProcess));
                            textView.setText(inputString);
                        }

                    }
                });

                buider.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        textView.setText(getString(R.string.clear_string));
                    }
                });

                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();

            }


        }
    };

    /*
     * 비어있는 문자를 clear_string으로 교체
     * */
    private String ChangeEmptyString(String inputString) {
        if (inputString.equals(""))
            return getString(R.string.clear_string);
        else
            return inputString;
    }


    /*
     * clear_string을 ""로 교체
     * */
    private String ChangeEmptyString2(String inputString) {
        if (inputString.equals(getString(R.string.clear_string)))
            return "";
        else
            return inputString;
    }


}

package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.sale.WorkType;

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

public class SupportDialog extends Dialog implements BaseActivityInterface{

    String contractNo;
    String customerName;
    String locationName;

    TextView txtCustomer;
    TextView txtLocation;

    Button btnCancel;
    Button btnOK;
    Context context;

    private ArrayList<WorkType> workTypeList;

    private Spinner spinnerWorkType;
    private Spinner spinnerSupervisor;

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }



    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) getOwnerActivity(), message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }


    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }


    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }

    public SupportDialog(Context context, String contractNo, String customerName, String locationName) {
        super(context);
        this.context=context;
        this.contractNo=contractNo;
        this.customerName=customerName;
        this.locationName=locationName;

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_support);

        startProgress();
        WindowManager.LayoutParams lp = getWindow().getAttributes( ) ;

        WindowManager wm = ((WindowManager)context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE)) ;

        lp.width =  (int)( wm.getDefaultDisplay().getWidth( ) * 0.9 );

        getWindow().setAttributes( lp ) ;
        btnCancel=findViewById(R.id.btnCancel);
        btnOK=findViewById(R.id.btnOK);
        spinnerWorkType = (Spinner) findViewById(R.id.spinnerWorkType);
        spinnerSupervisor = (Spinner) findViewById(R.id.superVisorSpinner);
        txtCustomer = findViewById(R.id.txtCustomer);
        txtLocation = findViewById(R.id.txtLocation);

        txtCustomer.setText(customerName);
        txtLocation.setText(locationName);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restURL = "";
                restURL = context.getString(R.string.service_address)+"insertsupervisorworder";
                new SetRequestWorder().execute(restURL);
                SupportDialog.this.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportDialog.this.dismiss();
            }
        });

        //슈퍼바이저 명단+작업구분 가져온다.

        GetSupervisorWorkTypeAndSupervisorList();
    }

    private class SetRequestWorder extends AsyncTask<String, Void, String> {

        String woNo ="";
        String message= "";
        String resultCode = "";

        @Override
        protected String doInBackground(String... urls) {

            return PostForSetRequestWorder(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            if(resultCode.equals("true")){
                Toast.makeText(context,"지원 요청이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                //에러메시지를 띄운다.
                Toast.makeText(context, message + "("+ resultCode  +")", Toast.LENGTH_SHORT).show();
            }

        }

        public String PostForSetRequestWorder(String url){
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

                //Save
                //jsonObject.put("WorkDate", suWorder2.WorkDate);

                String supervisorCode[]=spinnerSupervisor.getSelectedItem().toString().split("-");
                String workTypeCode[]=spinnerWorkType.getSelectedItem().toString().split("-");

                jsonObject.put("SupervisorCode", supervisorCode[0]);//spinner 선택한 사람의 supervisorCode

                jsonObject.put("SaleEmployeeCode", Users.USER_ID);//본인 SupervisorCode: 서버단에서 등록된 EmplyoeeCode로 바꿔준다.
                jsonObject.put("ContractNo", contractNo);

                jsonObject.put("WorkTypeCode", workTypeCode[0]);//spinner 선택한 작업구분

                jsonObject.put("Remark","");
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

                if(inputStream != null)
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
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            woNo ="";
            message= "";
            resultCode = "";

            try {
                Log.i("JSON", result );

                JSONArray jsonArray = new JSONArray(result);


                message = jsonArray.getJSONObject(0).getString("Message");
                resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

                if(resultCode.equals("true"))
                    woNo = message;
            }
            catch (Exception ex){
            }

            inputStream.close();
            return message + "("+ resultCode  +")";
        }


    }

    private void GetSupervisorWorkTypeAndSupervisorList(){
        String restURL = context.getString(R.string.service_address)+"worktypelistByBusinessClassCode";
        new GetWorkTypeList().execute(restURL);
    }





    //POST
    private class GetWorkTypeList extends AsyncTask<String, Void, String> {
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

                workTypeList = new ArrayList<WorkType>();
                String WorkTypeCode ="";
                String WorkTypeName = "";
                String SeqNo = "";
                int No;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    WorkTypeCode = child.getString("WorkTypeCode");
                    WorkTypeName  = child.getString("WorkTypeName");
                    SeqNo = child.getString("SeqNo");
                    No=i;
                    workTypeList.add(new WorkType( WorkTypeCode, WorkTypeName, SeqNo, No));
                }

                String[] workTypes = new String[workTypeList.size()];

                for(int i = 0 ; i < workTypeList.size(); i++){

                    workTypes[i] = workTypeList.get(i).WorkTypeCode+"-"+workTypeList.get(i).WorkTypeName;
                }

                ArrayAdapter<String> workTypeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, workTypes);
                spinnerWorkType.setAdapter(workTypeAdapter);

                String[] userList= new String[Users.supervisorList.length];
                int j=0;
                for (int i=0;i<Users.supervisorList.length;i++) {
                    if(Users.supervisorList[i].equals("16001-미지정")){//미지정 제외
                        continue;
                    }

                    userList[j]=Users.supervisorList[i];
                    j++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context, android.R.layout.simple_list_item_1,
                        userList);
                spinnerSupervisor.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
            progressOFF();

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
                // message = jsonArray.getJSONObject(0).getString("Message");
                //  resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
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
                jsonObject.put("BusinessClassCode", Users.BusinessClassCode);//사업장번호

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
            return result;
        }
    }
}

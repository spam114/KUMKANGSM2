package com.kumkangkind.kumkangsm2;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchEmployeeDialog extends Dialog implements TextWatcher {

    OnDialogResult mDialogResult;
    Button btnCancel;
    String addItemNo;
    AutoCompleteTextView act;

    ArrayList<String> stringArrayList;


    public void setDialogResult(OnDialogResult dialogResult){

        mDialogResult=dialogResult;
    }

    public interface OnDialogResult{
        void finish(String employeeNo, String employeeName);
    }


    public SearchEmployeeDialog(Context context, String addItemNo) {
        super(context);
        this.addItemNo=addItemNo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_search_employee);

        btnCancel= findViewById(R.id.btnItemCancel);
        act=findViewById(R.id.myautocomplete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        act.addTextChangedListener(this);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(act.getText().toString().equals("")){

        }
        else{
            stringArrayList= new ArrayList<>();
            //new GetEmployeeName(act.getText().toString()).execute(getString(R.string.service_address)+"getEmployeeInfo");

            AutoCompleteAdapter adapter= new AutoCompleteAdapter (getContext(),
                    android.R.layout.simple_dropdown_item_1line,act,this, addItemNo);
            //adapter.notifyDataSetChanged();

            act.setAdapter(adapter);
            act.setTextColor(Color.WHITE);



        }

        /*stringArrayList= new ArrayList<>();
        stringArrayList.add("고기");
        stringArrayList.add("고장난고장난고장난고장난고장고장고장조가조가조갖고자고작");
        stringArrayList.add("고래");


        act.setAdapter(new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                stringArrayList));
        act.setTextColor(Color.RED);*/



    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    String addItemNo;
    AutoCompleteTextView act;
    SearchEmployeeDialog searchEmployeeDialog;
    private ArrayList<String> data;
    private final String server = getContext().getString(R.string.service_address)+"getEmployeeSuggestions/";

    AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource, AutoCompleteTextView act, SearchEmployeeDialog searchEmployeeDialog, String addItemNo) {
        super(context, resource);
        this.addItemNo=addItemNo;
        this.searchEmployeeDialog=searchEmployeeDialog;
        this.data = new ArrayList<>();
        this.act=act;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final View view = super.getView(position, convertView, parent);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            view.getApplicationWindowToken(), 0);
                }
                return false;
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv;
                tv=(TextView) v;
                String info=tv.getText().toString();
                String[] array;
                array=info.split("/");
                String name=array[0];
                String dept=array[1];
                String employeeNo=array[2];

                new UpdateAddItemMasterEmployeeByPost(employeeNo,name).execute(getContext().getString(R.string.service_address)+"updateAddItemMasterEmployee");

            }
        });
        return view;
    }


    private class UpdateAddItemMasterEmployeeByPost extends AsyncTask<String, Void, String> {

        String employeeNo;
        String name;

        public UpdateAddItemMasterEmployeeByPost(String employeeNo, String name){
            this.employeeNo=employeeNo;
            this.name=name;
        }

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

                String resultCode = "";
                String message="";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    resultCode=child.getString("ResultCode");
                    message = child.getString("Message");
                }

                if(resultCode.equals("false"))
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                else{
                    act.setText(name);
                    searchEmployeeDialog.mDialogResult.finish(employeeNo,name);
                    Toast.makeText(getContext(),"수신인 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    searchEmployeeDialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
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
                jsonObject.put("AddItemNo", addItemNo);//추가분번호
                jsonObject.put("ReceiptEmployeeCode", employeeNo);//수신자사원번호
                jsonObject.put("SupervisorCode", Users.USER_ID);//수신자사원번호
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




    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {


                    HttpURLConnection conn = null;
                    InputStream input = null;

                    try {



                        URL url = new URL(server + constraint.toString());
                        conn = (HttpURLConnection) url.openConnection();
                        input = conn.getInputStream();
                        InputStreamReader reader = new InputStreamReader(input, "UTF-8");



                        BufferedReader buffer = new BufferedReader(reader, 8192);
                        StringBuilder builder = new StringBuilder();
                        String line;


                        while ((line = buffer.readLine()) != null) {
                            builder.append(line);
                        }


                        JSONObject jsonObject = new JSONObject(builder.toString());
                        JSONArray terms = jsonObject.optJSONArray("GetEmployeeSuggestionsResult");


                        ArrayList<String> suggestions = new ArrayList<>();

                        for (int ind = 0; ind < terms.length(); ind++) {
                            String term = terms.getString(ind);
                            suggestions.add(term);
                        }

                        results.values = suggestions;
                        results.count = suggestions.size();
                        data = suggestions;


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                    finally {
                        if (input != null) {
                            try {
                                input.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (conn != null) conn.disconnect();


                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }


/*
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
    }*/
}
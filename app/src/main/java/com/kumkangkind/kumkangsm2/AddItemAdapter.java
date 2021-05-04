package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import java.util.ArrayList;

public class AddItemAdapter extends ArrayAdapter<AddItem> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    Handler mHandler;
    String type;
    String statusFlag;

    public AddItemAdapter(Context context, int layoutResourceID, ArrayList data, String type, String statusFlag) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.type=type;
        this.statusFlag=statusFlag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        AddItem item = (AddItem) data.get(position);
        if (item != null) {
            String ReceiptEmployeeName=((AddItem) data.get(position)).ReceiptEmployeeName;
            if(ReceiptEmployeeName.equals(""))
                ReceiptEmployeeName="없음";
            String HoppingDate=((AddItem) data.get(position)).HoppingDate;
            if(HoppingDate.equals(""))
                HoppingDate="없음";
            TextView textViewDong = (TextView) row.findViewById(R.id.tvDong);
            final TextView textViewSupervisorName = (TextView) row.findViewById(R.id.tvSupervisorName);
            TextView textViewEmployeeName = (TextView) row.findViewById(R.id.tvEmployeeName);
            TextView textViewRequestDate = (TextView) row.findViewById(R.id.tvRequestDate);
            TextView textViewHoppingDate = (TextView) row.findViewById(R.id.tvHoppingDate);
            ImageView deleteImg = (ImageView) row.findViewById(R.id.imgDelete);

            textViewDong.setText(((AddItem) data.get(position)).Dong);
            textViewSupervisorName.setText(((AddItem) data.get(position)).SupervisorName);
            textViewEmployeeName.setText(ReceiptEmployeeName);
            textViewRequestDate.setText(((AddItem) data.get(position)).RequestDate);
            textViewHoppingDate.setText(HoppingDate);

            if(type.equals("확인") || statusFlag.equals("2")) {
                deleteImg.setVisibility(View.INVISIBLE);
            }
            else{
                deleteImg.setImageResource(R.drawable.delete);

                deleteImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!Users.UserName.equals(textViewSupervisorName.getText().toString())){
                            Toast.makeText(getContext(), "본인의 추가분 정보만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ShowDeleteDialog();

                    }

                    private void ShowDeleteDialog() {
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle("추가분 삭제")
                                .setMessage("추가분 정보를 삭제 하시겠습니까? \n (주의: 추가분 세부 정보까지 함께 삭제 됩니다.)")
                                .setCancelable(true)
                                .setPositiveButton
                                        ("YES", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                new AddItemDelete(((AddItem) data.get(position)).AddItemNo,position).execute(context.getString(R.string.service_address)+"deleteAddItem");

                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                });
            }


        }
        return row;
    }

    private class AddItemDelete extends AsyncTask<String, Void, String> {

        int position;
        String addItemNo;

        public AddItemDelete(String addItemNo, int position){
            this.addItemNo=addItemNo;
            this.position=position;
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

                String ResultCode = "";
                String Message = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);

                }
                if(ResultCode.equals(false)){
                    Toast.makeText(getContext(), "삭제 실패: 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
                }
                else{//성공시
                    data.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "추가분 삭제가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
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
                jsonObject.put("AddItemNo",addItemNo);//추가분 번호

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

}


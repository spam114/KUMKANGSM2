package com.kumkangkind.kumkangsm2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;





public class ComplaintImageActivity extends BaseActivity {
    int removePosition=-1;
    String ItemNo;
    int RESULT_GALLERY2 = 0;
    int intent2, GALLERY_KITKAT_INTENT = 1;
    Context context;
    private ListView listView1;
    Button btnTakePicture;
    Button btnLoad;
    Button btnExit;
    ComplaintImageAdapter adapter;
    String imgPath = "";
    byte[] byteArray= null;

    int type;//추가분이냐 AS냐 구분 1: 추가분 2: AS
    ArrayList<ComplaintImage> imageList;

    private ComplaintImage[] items;
    ComplaintImage currentImage = null;

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
        setContentView(R.layout.dialog_complaint_image);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.9); //Display 사이즈의 70%

        int height = (int) (display.getHeight() * 0.87);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        type = Integer.parseInt(getIntent().getStringExtra("type"));


        ItemNo = getIntent().getStringExtra("ItemNo");//ASNo 또는 추가분 리스트번호+순번
        makeImageList();
        if(ItemNo.equals("X")) {//ItemNo 가 "X" 이면 등록하면서 같이 하는것이다. Temp를 사용
            adapter = new ComplaintImageAdapter(this, R.layout.listview_imagerow2, Temp.tempList);
        }
        else{
            adapter = new ComplaintImageAdapter(this, R.layout.listview_imagerow2, imageList);
        }

        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnLoad = findViewById(R.id.btnLoad);
        btnExit = findViewById(R.id.btnExit);

        listView1 = findViewById(R.id.listViewImage);
        View header = getLayoutInflater().inflate(R.layout.listview_imageheader, null);
        listView1.addHeaderView(header);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);
        listView1.setFocusable(false);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {

                    Intent intent2 = new Intent();
                    intent2.setType("image/*");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent2, "Select Picture"), RESULT_GALLERY2);
                } else {
                    Intent intent2 = new Intent();
                    intent2.setType("image/*");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent2, "Select Picture"), GALLERY_KITKAT_INTENT);
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private class HttpAsyncTaskDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            imageList.remove(removePosition);
            mHandler2.sendEmptyMessage(0);
        }
    }

    private void makeImageList() {

        imageList = (ArrayList<ComplaintImage>) getIntent().getSerializableExtra("data");
        items = new ComplaintImage[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            items[i] = new ComplaintImage(
                    imageList.get(i).ItemNo,
                    imageList.get(i).No,
                    imageList.get(i).Type,
                    imageList.get(i).ImageName,
                    imageList.get(i).ImageFile,
                    imageList.get(i).SmallImageFile
            );
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0)
                return;

            if(ItemNo.equals("X")){
                currentImage = Temp.tempList.get(position - 1);

                if (Temp.tempList.get(position - 1).ImageFile.equals("") || Temp.tempList.get(position - 1).ImageFile.equals("null")) {
                    //서버통신: 그림 하나 가져와서 확대
                    String restURL = getString(R.string.service_address)+"getComplaintImage";
                    new GetComplaintImage().execute(restURL);
                } else {
                    ViewData(currentImage.ImageFile, currentImage.ImageName);
                }
            }
            else{
                currentImage = imageList.get(position - 1);

                if (imageList.get(position - 1).ImageFile.equals("") || imageList.get(position - 1).ImageFile.equals("null")) {
                    //서버통신: 그림 하나 가져와서 확대
                    String restURL = getString(R.string.service_address)+"getComplaintImage";
                    new GetComplaintImage().execute(restURL);
                } else {
                    ViewData(currentImage.ImageFile, currentImage.ImageName);
                }
            }






        }
    };

    private void ViewData(String imageString, String imageName) {

        String photostring = imageString;

        try {
            byte[] array5 = Base64.decode(photostring, Base64.DEFAULT);
            Dialog dialog = new Dialog(this);
            dialog.setTitle(imageName);
            dialog.setContentView(R.layout.dialog_image);
            ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView1);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(array5, 0, array5.length));

            /*
            Button saveButton = (Button)dialog.findViewById(R.id.buttonImageSave);
            saveButton.setText("이미지 저장");
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    byte[] byteArray = Base64.decode(currentImage.ImageFile, Base64.DEFAULT);
                    saveBitmaptoJpeg( BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length), "KUMKANG", currentImage.ImageName);
                }

            });
            */

            dialog.show();

        } catch (Exception ex) {

            Log.e("에러", "비트맵 에러 " + ex.getMessage().toString());
        }
    }



    private class GetComplaintImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return PostForComplaintImage(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    currentImage.ItemNo = child.getString("ItemNo");
                    currentImage.No = child.getString("No");
                    currentImage.Type = child.getString("Type");
                    currentImage.ImageName = child.getString("ImageName");
                    currentImage.ImageFile = child.getString("ImageFile");
                    ViewData(currentImage.ImageFile, currentImage.ImageName);
                }


            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "항목 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                e.printStackTrace();
            }
        }

        public String PostForComplaintImage(String url) {
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


                jsonObject.put("ItemNo", ItemNo);
                jsonObject.put("No", currentImage.No);


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
                Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            Log.i("result", result.toString());
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
            } catch (Exception ex) {
            }

            inputStream.close();
            return result;
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_OK && null != data) {
            startProgress();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndex(proj[0]);
            cursor.moveToFirst();
            imgPath = cursor.getString(column_index);
            imgPath = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            Uri uri = data.getData();

            UpdateImage(uri);

        } else if (requestCode == GALLERY_KITKAT_INTENT && resultCode == RESULT_OK && null != data) {
            startProgress();

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndex(proj[0]);
            cursor.moveToFirst();
            imgPath = cursor.getString(column_index);
            Uri uri = data.getData();
            imgPath = getPath(getBaseContext(), uri);
            imgPath = imgPath.substring(imgPath.lastIndexOf("/") + 1);

            UpdateImage(uri);
        }
    }

    private  void UpdateImage(Uri uri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);

            byteArray = bStream.toByteArray();

            //POST 명령어 호출(업데이트를 적용한다)

            currentImage = new ComplaintImage();
            currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
            currentImage.ImageName = imgPath;
            currentImage.Type=Integer.toString(type);
            currentImage.No = "0";
            currentImage.SmallImageFile = currentImage.ImageFile;

            if(ItemNo.equals("X")){//신규일때는 서버 전송 X, temp리스트에 저장

                currentImage.ItemNo ="X";//아직까지는 비어있다.
                currentImage.No = Integer.toString(Temp.tempList.size()+1);
                Temp.tempList.add(currentImage);
                mHandler2.sendEmptyMessage(0);
                //adapter.notifyDataSetChanged();

            }
            else{
                currentImage.ItemNo =ItemNo;
                new HttpAsyncTask().execute(getString(R.string.service_address)+"setComplaintImage");
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private  String compressImage2(String jsonString){


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; // 1/4배율로 읽어오게 하는 방법

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        int targetWidth = 1000; // your arbitrary fixed limit
        int targetHeight = (int) (decodedByte.getHeight() * targetWidth / (double) decodedByte.getWidth());
        /*
        options = new BitmapFactory.Options();
        options.outHeight = targetHeight;
        options.outWidth = 480;
        */
        //decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        Bitmap resized = Bitmap.createScaledBitmap( decodedByte, targetWidth, targetHeight, true );
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80,  bStream);
        byte[] byteArray = bStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            imageList.add(currentImage);
            mHandler2.sendEmptyMessage(0);
        }
    }



    Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            //Log.i("mHandler2", Integer.toString(Temp.tempList.size()));
            progressOFF();
            //onResume();
        }
    };

    public String POST(String url){
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

            if(currentImage.No.equals("0"))
            {
                //Insert
                jsonObject.put("ItemNo", currentImage.ItemNo);
                jsonObject.put("No", currentImage.No);
                jsonObject.put("Type", currentImage.Type);
                jsonObject.put("ImageName", currentImage.ImageName);
                jsonObject.put("ImageFile", currentImage.ImageFile);
                jsonObject.put("SupervisorCode", Users.USER_ID);
            }
            else{
                //Delete
                jsonObject.put("ItemNo", currentImage.ItemNo);
                jsonObject.put("No", currentImage.No);
            }

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
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        Log.i("result", result.toString());
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        String message= "";
        String resultCode = "";

        try {
            Log.i("JSON", result );

            JSONArray jsonArray = new JSONArray(result);
            message = jsonArray.getJSONObject(0).getString("Message");
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");

            if(!resultCode.equals("false"))
                currentImage.No = resultCode;
        }
        catch (Exception ex){
        }

        inputStream.close();
        return message;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;
        Log.i("URI",uri+"");
        String result = uri+"";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {

            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");

            final String docId = dat[1];
            final String type = dat[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {

            } else if ("audio".equals(type)) {
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    dat[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        else
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public void DeleteComplaintImage(int position){
        if(ItemNo.equals("X")){//추가분 등록하면서 함께 삭제하는 것은, Temp 데이터만 삭제 디비 X
            currentImage = Temp.tempList.get(position);
            Toast.makeText(getBaseContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            Temp.tempList.remove(position);
            mHandler2.sendEmptyMessage(0);
        }
        else{
            removePosition=position;
            currentImage = imageList.get(removePosition);
            new HttpAsyncTaskDelete().execute(getString(R.string.service_address)+"deleteComplaintImage");
        }
    }
}





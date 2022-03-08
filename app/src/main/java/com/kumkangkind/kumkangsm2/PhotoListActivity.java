package com.kumkangkind.kumkangsm2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * 1. 해당 클래스는 작업지시 목록을 보인다.
 * 2. 항목을 클릭할 경우, 서버와 통신하여 해당 데이터를 가져온다.
 */
public class PhotoListActivity extends BaseActivity {

    WoImage currentImage=  null;
    private ListView listView1;
    private WoImage[] items;
    TextView textViewUserName;
    int RESULT_GALLERY2 = 0;
    int RESULT_MULTI_PICTURE = 3;
    int intent2, GALLERY_KITKAT_INTENT = 1;
    ArrayList<WoImage> imageList;
    ImageAdapter adapter;
    String key = "";
    ProgressDialog mProgressDialog;
    byte[] byteArray= null;
    String imgPath = "";
    int removePosition = 0;
    String FIXFLAG = "0";
    String type="";
    String customer ="";
    Button addButton;
    Dialog dialog;
    EditText textViewphotoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_image);

        key = getIntent().getStringExtra("key");
        customer = getIntent().getStringExtra("customer");

        FIXFLAG = getIntent().getStringExtra("fix");
        type = getIntent().getStringExtra("type");
        if(type==null)
            type="";

        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(customer);
        addButton = (Button)findViewById(R.id.btnAdd);
        addButton.setText("+사진추가");

        makeImageList();

        adapter = new ImageAdapter(this, R.layout.listview_imagerow, imageList);

        //ListView
        listView1 = (ListView) findViewById(R.id.listViewImage);
        View header = (View) getLayoutInflater().inflate(R.layout.listview_imageheader, null);
        listView1.addHeaderView(header);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);
        listView1.setFocusable(false);

        progressOFF();
    }



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

    public void mOnClick(View v) {

        if(FIXFLAG.equals("2") || type.equals("확인") )
                return;

        switch (v.getId()) {


            case R.id.btnAdd: //추가
                //이미지 갤러리
                if(Build.VERSION.SDK_INT < 19) {

                    Intent intent2 = new Intent();
                    intent2.setType("image/*");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent2, "Select Picture"), RESULT_GALLERY2);
                }
                else {
                    /*Intent intent2 = new Intent();
                    intent2.setType("image/*");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent2, "Select Picture"), GALLERY_KITKAT_INTENT);*/

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_MULTI_PICTURE);
                }
                break;

            /*case R.id.buttonDelete:

                final View v2 = v;
                new AlertDialog.Builder(this).setMessage("삭제할까요?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //POST 명령어 호출(업데이트를 적용한다)

                        mHandler = new Handler();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog = ProgressDialog.show(PhotoListActivity.this, "",
                                        "잠시만 기다려 주세요.", true);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 5000);
                            }
                        });

                        int position = (int)v2.getTag();
                        int position2 = listView1.getCheckedItemPosition();

                        Log.i("Position1", String.valueOf(position));
                        Log.i("Position2", String.valueOf(position2));

                        if(position != ListView.INVALID_POSITION) {
                            removePosition = position;
                                currentImage = imageList.get(position);

                            new HttpAsyncTaskDelete().execute(getString(R.string.service_address)+"deleteimage");
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;*/
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0)
                return;

            currentImage = imageList.get(position - 1);

            if (imageList.get(position - 1).ImageFile.equals("") || imageList.get(position - 1).ImageFile.equals("null")) {
                //서버통신
                String restURL = getString(R.string.service_address)+"getimage/" + items[position - 1].WoNo + "/" + items[position - 1].SeqNo;
                new GetImageFeedTask().execute(restURL);
            } else {
                ViewData(currentImage.ImageFile, currentImage.ImageName);
            }
        }
    };

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

        if (requestCode == RESULT_GALLERY2 && resultCode == RESULT_OK && null != data) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndex(proj[0]);
            cursor.moveToFirst();
            imgPath = cursor.getString(column_index);
            imgPath = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            Uri uri = data.getData();

            //파일이름설정
            MakePhotoSettingDialog(uri);
            //UpdateImage(uri);
        } else if (requestCode == RESULT_MULTI_PICTURE && resultCode == RESULT_OK && null != data) {

           /* String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
            int column_index = cursor.getColumnIndex(proj[0]);
            cursor.moveToFirst();
            imgPath = cursor.getString(column_index);
            Uri uri = data.getData();
            imgPath = getPath(getBaseContext(), uri);
            imgPath = imgPath.substring(imgPath.lastIndexOf("/") + 1);

            //파일이름설정
            MakePhotoSettingDialog(uri);
           // UpdateImage(uri);*/

            ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                //adapter = new MultiImageAdapter(uriList, getApplicationContext());
                //recyclerView.setAdapter(adapter);
                //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                        }
                    }
                    //adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    //recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
            updateImageMulti(uriList);
        }
    }

    private void updateImageMulti(ArrayList<Uri> uriList) {

        String url = getString(R.string.service_address) + "updateImageMulti";
        ContentValues values = new ContentValues();
        values.put("SupervisorWoNo", key);

        UpdateImageMulti gsod = new UpdateImageMulti(url, values, uriList);
        gsod.execute();

    }











    public class UpdateImageMulti extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        ArrayList<Uri> list;

        UpdateImageMulti(String url, ContentValues values, ArrayList<Uri> list) {
            this.url = url;
            this.values = values;
            this.list = list;
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
            result = requestHttpURLConnection.request2(url, values, list, PhotoListActivity.this);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                imageList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(PhotoListActivity.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }

                    imageList.add(new WoImage(child.getString("SupervisorWoNo"),
                            child.getString("SeqNo"),
                            child.getString("ImageName"),
                            "",
                            child.getString("Imagefile")));

                    //tempSaleOrderNo = child.getString("SaleOrderNo");
                    //fixDivision = child.getString("FixDivision");
                }

                remakeImageList();
                adapter = new ImageAdapter(PhotoListActivity.this, R.layout.listview_imagerow, imageList);

                //ListView
                //listView1 = (ListView) findViewById(R.id.listViewImage);
                //View header = (View) getLayoutInflater().inflate(R.layout.listview_imageheader, null);
                //listView1.addHeaderView(header);
                listView1.setAdapter(adapter);
                listView1.setOnItemClickListener(mItemClickListener);
                listView1.setFocusable(false);




            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }













    private void MakePhotoSettingDialog(final Uri uri){

        dialog = new Dialog(PhotoListActivity.this);
        dialog.setContentView(R.layout.dialog_photo);
        dialog.setTitle("사진 추가");

        textViewphotoName = (EditText) dialog.findViewById(R.id.editTextPhotoName);
        textViewphotoName.setText(imgPath);

        Button okButton = (Button) dialog.findViewById(R.id.btnItemOK);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                imgPath = textViewphotoName.getText().toString();
                UpdateImage(uri);
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.btnItemCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private  void UpdateImage(Uri uri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            byteArray = bStream.toByteArray();

            //POST 명령어 호출(업데이트를 적용한다)

            mHandler = new Handler();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog = ProgressDialog.show(PhotoListActivity.this, "",
                            "잠시만 기다려 주세요.", true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 10000);
                }
            });
            currentImage = new WoImage();
            currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
            currentImage.ImageName = imgPath;
            currentImage.WoNo = key;
            currentImage.SeqNo = "0";
            currentImage.SmallImageFile = currentImage.ImageFile;
            new HttpAsyncTask().execute(getString(R.string.service_address)+"insertimage");

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


    Handler mHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              adapter.notifyDataSetChanged();
              Log.i("mHandler", "mHandler");
              onResume();
          }
      };

    Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            Log.i("mHandler", "mHandler");
            onResume();
        }
    };

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

            if(currentImage.SeqNo.equals("0"))
            {
                //Insert
                jsonObject.put("SupervisorWoNo", currentImage.WoNo);
                jsonObject.put("SeqNo", currentImage.SeqNo);
                jsonObject.put("ImageName", currentImage.ImageName);
                jsonObject.put("Imagefile", currentImage.ImageFile);
            }
            else{
                //Delete
                jsonObject.put("SupervisorWoNo", currentImage.WoNo);
                jsonObject.put("SeqNo", currentImage.SeqNo);
                jsonObject.put("ImageName", currentImage.ImageName);
                jsonObject.put("Imagefile", "");
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
        mProgressDialog.dismiss();
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
                currentImage.SeqNo = resultCode;
        }
        catch (Exception ex){
        }

        inputStream.close();
        return message + "("+ resultCode  +")";
    }


    private class GetImageFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                Log.i("GetImageFeedTask", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("GetImageResult");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    currentImage.WoNo = child.getString("SupervisorWoNo");
                    currentImage.SeqNo = child.getString("SeqNo");
                    currentImage.ImageName = child.getString("ImageName");
                    currentImage.ImageFile = child.getString("Imagefile");
                    ViewData(currentImage.ImageFile, currentImage.ImageName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  그림을 보인다.
     * @param imageString
     */
    private void ViewData(String imageString, String imageName){

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

    public String readJSONFeed(String URL) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");

            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }

        return stringBuilder.toString();
    }

    private void makeImageList() {

        imageList = (ArrayList<WoImage>) getIntent().getSerializableExtra("data");
        items = new WoImage[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            items[i] = new WoImage(imageList.get(i).WoNo,
                    imageList.get(i).SeqNo,
                    imageList.get(i).ImageName,
                    imageList.get(i).ImageFile,
                    imageList.get(i).SmallImageFile);
        }
    }
    private void remakeImageList() {

        items = new WoImage[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            items[i] = new WoImage(imageList.get(i).WoNo,
                    imageList.get(i).SeqNo,
                    imageList.get(i).ImageName,
                    imageList.get(i).ImageFile,
                    imageList.get(i).SmallImageFile);
        }
    }

    /**
     * Image SDCard Save (input Bitmap -> saved file JPEG)
     * Writer intruder(Kwangseob Kim)
     * @param bitmap : input bitmap file
     * @param folder : input folder name
     * @param name   : output file name
     */
    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}

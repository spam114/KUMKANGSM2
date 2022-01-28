package com.kumkangkind.kumkangsm2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class RequestHttpURLConnection {

    Context context;

    public String request(String _url, ContentValues _params) {

        HttpURLConnection urlConn = null;

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            // [2-1]. urlConn 설정.

            urlConn = (HttpURLConnection) url.openConnection();
            //Request 형식 설정
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");

            //request에 JSON data 준비
            urlConn.setDoOutput(true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));
            //commands라는 JSONArray를 담을 JSONObject 생성
            JSONObject commands = new JSONObject();

            // URL 뒤에 붙여서 보낼 파라미터.
            // 파라미터 키와 값.
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                commands.put(key, value);
            }


            //request에 쓰기
            bw.write(commands.toString());
            bw.flush();
            bw.close();

            //보내고 결과값 받기
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 400) {
                System.out.println("400:: 해당 명령을 실행할 수 없음 (실행할 수 없는 상태일 때, 엘리베이터 수와 Command 수가 일치하지 않을 때, 엘리베이터 정원을 초과하여 태울 때)");
            } else if (responseCode == 401) {
                System.out.println("401:: X-Auth-Token Header가 잘못됨");
            } else if (responseCode == 500) {
                System.out.println("500:: 서버 에러, 문의 필요");
            }

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }












    public String request2(String _url, ContentValues _params, ArrayList<Uri> arrayList, Context context) {
        this.context=context;
        HttpURLConnection urlConn = null;

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            // [2-1]. urlConn 설정.

            urlConn = (HttpURLConnection) url.openConnection();
            //Request 형식 설정
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");

            //request에 JSON data 준비
            urlConn.setDoOutput(true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));
            //commands라는 JSONArray를 담을 JSONObject 생성
            JSONObject commands = new JSONObject();

            // URL 뒤에 붙여서 보낼 파라미터.
            // 파라미터 키와 값.
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                commands.put(key, value);
            }

            JSONArray jArray = new JSONArray();//배열이 필요할때
            for (int i = 0; i < arrayList.size(); i++) {

                byte[] byteArray= null;
                String imageName=getFileName(arrayList.get(i));
                String imagefile="";

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), arrayList.get(i));
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byteArray = bStream.toByteArray();

                imagefile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
                //currentImage.ImageName = imgPath;
                //currentImage.SmallImageFile = currentImage.ImageFile;
                //new HttpAsyncTask().execute(getString(R.string.service_address)+"insertimage");

                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                sObject.put("ImageName", imageName);
                sObject.put("Imagefile", imagefile);
                jArray.put(sObject);
            }
            commands.put("PhotoList", jArray);

            //request에 쓰기
            bw.write(commands.toString());
            bw.flush();
            bw.close();

            //보내고 결과값 받기
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 400) {
                System.out.println("400:: 해당 명령을 실행할 수 없음 (실행할 수 없는 상태일 때, 엘리베이터 수와 Command 수가 일치하지 않을 때, 엘리베이터 정원을 초과하여 태울 때)");
            } else if (responseCode == 401) {
                System.out.println("401:: X-Auth-Token Header가 잘못됨");
            } else if (responseCode == 500) {
                System.out.println("500:: 서버 에러, 문의 필요");
            }

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
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






}

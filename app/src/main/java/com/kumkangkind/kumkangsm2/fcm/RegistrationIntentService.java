package com.kumkangkind.kumkangsm2.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.Users;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RegistrationIntentService extends IntentService { //RegistrationIntentService: GCM Client 등록을 위한 IntentService

    //IntentService는 백그라운드 스레드에서 처리하며 처리가 끝나면 자기 자신을 완료하는 클래스이다.
//IntentService는 비동기로 실행되는 쓰레드인데, 여러번 실행되었을 경우에는 Queue로 처리된다.
    //즉, 먼저 처리중인 IntentService가 있다면 다음 차례를 기다리게 된다.


    private static final String TAG = "RegIntentService";
    // private static final String[] TOPICS = {"global"};
    private String mToken = "";

    public RegistrationIntentService() {
        super(TAG);
    }

    //서비스가 종료되는지 확인
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "서비스 종료 ");

    }

    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    Log.i(TAG, "FCM Registration Token : " + token);
                    sendRegistrationToServer(token);//서버에 토큰을 보내서 갱신한다.
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();//여기서 true로 설정한다음에 SearchActivity에있는 동적 broadCastReceiver를 사용하여 textView형태로 토큰이 성공적으로 바뀌었다고, 출력
                }

            });
            //deprecated
            //String token = FirebaseInstanceId.getInstance().getToken();
            //Log.i(TAG, "FCM Registration Token : " + token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);//$$$인텐트에 문자열을 넣어서 방송
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {

        //내 서버에 폰에서 읽어온 토큰을 전송한다(token을 관리하기 위해서)
        mToken = token;
        String address = Users.ServiceAddress + "setfcm";
        new HttpAsyncTask().execute(address);
    }

   /* private void subscribeTopics(String token) throws IOException {//주제 구독 하려면  https://firebase.google.com/docs/cloud-messaging/admin/manage-topic-subscriptions?hl=ko 를 참고


        List<String> registrationTokens = Arrays.asList(token);
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(
                registrationTokens, topic).get();
// See the TopicManagementResponse reference documentation
// for the contents of response.
        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");

    }*/

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            //
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
            jsonObject.put("Supervisor", Users.USER_ID);
            jsonObject.put("Type1", mToken);

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
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        try {

            Log.i(TAG, result);

        } catch (Exception ex) {
        }

        inputStream.close();
        return result;
    }
}

package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 리스트 뷰를 구현하기 위한 어댑터 클래스다.
 */
public class ImageAdapter extends ArrayAdapter<WoImage> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    //WoImage[] data;
    ArrayList data;
    Handler mHandler;
    ProgressDialog mProgressDialog;
    WoImage currentImage;
    public int removePosition;

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

    public ImageAdapter(Context context, int layoutResourceID, ArrayList data) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_imagerow, null);
        }

        WoImage image = (WoImage) data.get(position);
        if (image != null) {

            TextView textViewSeqNo = (TextView) row.findViewById(R.id.textViewImageSeqNo);
            TextView textViewImageName = (TextView) row.findViewById(R.id.textViewImageName);
            Button deleteButton = (Button) row.findViewById(R.id.buttonDelete);
            ImageView imageView = (ImageView) row.findViewById(R.id.imageViewSmall);

            deleteButton.setFocusable(false);

            if (textViewImageName != null)
                textViewImageName.setText(((WoImage) (data.get(position))).ImageName);
            if (textViewSeqNo != null)
                textViewSeqNo.setText(String.valueOf(position + 1));

            deleteButton.setTag(position);

            try {
                byte[] array5 = Base64.decode(((WoImage) data.get(position)).SmallImageFile, Base64.DEFAULT);
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(array5, 0, array5.length));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("사진 삭제")
                            .setMessage("사진을 삭제하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteImage2(image.WoNo, image.SeqNo, position);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            });

        }
        return row;
    }

    private void deleteImage2(String SupervisorWoNo, String SeqNo, int position) {
        String url = context.getString(R.string.service_address) + "deleteImage2";
        ContentValues values = new ContentValues();
        values.put("SupervisorWoNo", SupervisorWoNo);
        values.put("SeqNo", SeqNo);
        DeleteImage2 gsod = new DeleteImage2(url, values, position);
        gsod.execute();
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity) context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity) context, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) context, message, handler);
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

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }


    public class DeleteImage2 extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        int position;

        DeleteImage2(String url, ContentValues values, int position) {
            this.url = url;
            this.values = values;
            this.position=position;
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
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //String tempSaleOrderNo = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(context, ErrorCheck, 2);
                        return;
                    }
                    //tempSaleOrderNo = child.getString("SaleOrderNo");
                }
                data.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                //((Activity) context).finish();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    /**
     * 그림을 보인다.
     *
     * @param imageString
     */
    private void ViewData(String imageString) {

        String photostring = imageString;

        try {


        } catch (Exception ex) {

            Log.e("에러", "비트맵 에러 " + ex.getMessage().toString());
        }
    }

}


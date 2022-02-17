package com.kumkangkind.kumkangsm2.ProgressFloor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BackPressEditText;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Dong;
import com.kumkangkind.kumkangsm2.MyWatcher;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProgressFloorReturnViewAdapter extends ArrayAdapter<Dong> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    LinearLayout layoutTop;
    ListView listview;
    com.kumkangkind.kumkangsm2.BackPressEditText edtProgressFloor;
    String contractNo;
    String fromDate;
    com.google.android.material.textfield.TextInputLayout textInputLayout;

    public ProgressFloorReturnViewAdapter(Context context, int layoutResourceID, ArrayList data, LinearLayout layoutTop, ListView listview, String contractNo, String fromDate) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.layoutTop = layoutTop;
        this.listview = listview;
        this.contractNo = contractNo;
        this.fromDate = fromDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.progress_floor_return_row, null);
           // edtProgressFloor = row.findViewById(R.id.edtProgressFloor);
            //edtProgressFloor.addTextChangedListener(new MyWatcher(edtProgressFloor));
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        Dong item = (Dong) data.get(position);
        if (item != null) {

            TextView textViewDong = row.findViewById(R.id.tvDong);
            TextView textViewConstructionEmployee = row.findViewById(R.id.tvConstructionEmployee);
            TextView textViewExYearMonth = row.findViewById(R.id.tvExYearMonth);
            TextView textViewExProgressFloor = row.findViewById(R.id.tvExProgressFloor);
            TextView textViewYearMonth = row.findViewById(R.id.tvYearMonth);
            textInputLayout=row.findViewById(R.id.textInputLayout);
            edtProgressFloor = row.findViewById(R.id.edtProgressFloor);
            edtProgressFloor.setOnBackPressListener(new BackPressEditText.OnBackPressListener() {
                @Override
                public void onBackPress() {
                    layoutTop.requestFocus();
                    notifyDataSetChanged();
                }
            });
            edtProgressFloor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) { // IME_ACTION_SEARCH , IME_ACTION_GO
                        //edtProgressFloor.clearFocus();
                        //저장
                        if (!v.getText().toString().equals("")) {
                            setDongProgressFloorReturn(item.Dong, v, item, v.getText().toString());
                        } else {
                            Toast.makeText(context, "진행층을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }
            });

            textViewDong.setText(item.Dong);
            textViewConstructionEmployee.setText(item.CollectEmployee);
            //textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressDate);
            textViewExProgressFloor.setText(item.ExProgressFloor);
            //.setText(((Dong) data.get(position)).ProgressDate);
            edtProgressFloor.setTag(item);
            edtProgressFloor.setText(item.ProgressFloor);
            String exProgressDate = item.ExProgressDate;
            String progressDate = item.ProgressDate;


            if (!exProgressDate.equals(""))
                textViewExYearMonth.setText(exProgressDate.substring(0, 4) + "\n" + exProgressDate.substring(5));
            else
                textViewExYearMonth.setText("");
            textViewExProgressFloor.setText(item.ExProgressFloor);
            if (!progressDate.equals(""))
                textViewYearMonth.setText(progressDate.substring(0, 4) + "\n" + progressDate.substring(5));
            else
                textViewYearMonth.setText("");

            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                   /* if(!item.UserCode.equals(Users.UserID)){
                        return false;
                    }*/

                    if (item.ProgressDate.equals("")) {
                        return false;
                    }

                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("진행층 삭제")
                            .setMessage("동: " + item.Dong + "\n" +
                                    "진행일: " + item.ProgressDate + "\n" + "진행층을 삭제하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteDongProgressFloorReturn(item.Dong, item);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return false;
                }
            });
        }

        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtProgressFloor.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                layoutTop.requestFocus();
                notifyDataSetChanged();
                HideKeyBoard(context);
                return false;
            }
        });

        return row;
    }

    public void deleteDongProgressFloorReturn(String dong, Dong item) {
        String url = context.getString(R.string.service_address) + "deleteDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        DeleteDongProgressFloorReturn gsod = new DeleteDongProgressFloorReturn(url, values, item);
        gsod.execute();
    }

    public class DeleteDongProgressFloorReturn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        Dong item;

        DeleteDongProgressFloorReturn(String url, ContentValues values, Dong item) {
            this.url = url;
            this.values = values;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                layoutTop.requestFocus();
                item.ProgressFloor = "";
                item.ProgressDate = "";
                notifyDataSetChanged();
                HideKeyBoard(context);
                Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }


    public void setDongProgressFloorReturn(String dong, TextView v, Dong item, String progressFloor) {
        String url = context.getString(R.string.service_address) + "setDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        values.put("ProgressFloor", progressFloor);
        SetDongProgressFloorReturn gsod = new SetDongProgressFloorReturn(url, values, v, item, fromDate);
        gsod.execute();
    }

    public class SetDongProgressFloorReturn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String fromDate;
        TextView v;
        Dong item;

        SetDongProgressFloorReturn(String url, ContentValues values, TextView v, Dong item, String fromDate) {
            this.url = url;
            this.values = values;
            this.fromDate = fromDate;
            this.v = v;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                layoutTop.requestFocus();
                item.ProgressFloor = v.getText().toString();
                item.ProgressDate = this.fromDate;
                notifyDataSetChanged();
                HideKeyBoard(context);
                Toast.makeText(context, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }

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
    public void progressON() {

    }

    @Override
    public void progressON(String message) {

    }

    @Override
    public void progressON(String message, Handler handler) {

    }

    @Override
    public void progressOFF(String className) {

    }

    @Override
    public void progressOFF2(String className) {

    }

    @Override
    public void progressOFF() {

    }

    @Override
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }
}


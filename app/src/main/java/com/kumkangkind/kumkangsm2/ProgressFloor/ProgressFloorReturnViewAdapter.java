package com.kumkangkind.kumkangsm2.ProgressFloor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BackPressEditText;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Dong;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.RequestHttpURLConnection;
import com.kumkangkind.kumkangsm2.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ProgressFloorReturnViewAdapter extends RecyclerView.Adapter<ProgressFloorReturnViewAdapter.ViewHolder> implements BaseActivityInterface {

    Context context;
    LinearLayout layoutTop;
    String contractNo;
    String fromDate;
    ArrayList<Dong> items = new ArrayList<>();

    public int tyear;
    public int tmonth;
    public int tdate;

    public ProgressFloorReturnViewAdapter(Context context, LinearLayout layoutTop, String contractNo, String fromDate) {
        super();
        this.context = context;
        this.layoutTop = layoutTop;
        this.contractNo = contractNo;
        this.fromDate = fromDate;

        final Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.progress_floor_return_row, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Dong item = items.get(position);
        viewHolder.setItem(item, position); //?????????
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //????????? ViewHolder??? Static ?????? ??????.
    //???????????? ?????????, ?????? ?????????
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDong;
        TextView textViewConstructionEmployee;
        TextView textViewExYearMonth;
        TextView textViewExProgressFloor;
        TextView textViewYearMonth;
        LinearLayout tvYearMonthLayout;
        TextView textFloorInfo;
        com.kumkangkind.kumkangsm2.BackPressEditText edtProgressFloor;
        com.google.android.material.textfield.TextInputLayout textInputLayout;
        View row;

        public ViewHolder(View itemVIew) {
            super(itemVIew);
            this.row = itemVIew;
            textViewDong = itemVIew.findViewById(R.id.tvDong);
            textViewConstructionEmployee = itemVIew.findViewById(R.id.tvConstructionEmployee);
            textViewExYearMonth = itemVIew.findViewById(R.id.tvExYearMonth);
            textViewExProgressFloor = itemVIew.findViewById(R.id.tvExProgressFloor);
            textViewYearMonth = itemVIew.findViewById(R.id.tvYearMonth);
            tvYearMonthLayout = itemVIew.findViewById(R.id.tvYearMonthLayout);
            edtProgressFloor = itemVIew.findViewById(R.id.edtProgressFloor);
            textInputLayout = itemVIew.findViewById(R.id.textInputLayout);
            textFloorInfo = itemVIew.findViewById(R.id.tvFloorInfo);
        }

        public void setItem(Dong item, int position) {
            int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1, context.getResources().getDisplayMetrics());
            textViewDong.setText(item.Dong);
            textViewConstructionEmployee.setText(item.CollectEmployee);
            //textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressDate);
            textViewExProgressFloor.setText(item.ExProgressFloor);
            //.setText(((Dong) data.get(position)).ProgressDate);
            edtProgressFloor.setTag(item);
            edtProgressFloor.setText(item.ProgressFloor);
            String exProgressDate = item.ExProgressDate;
            String inPlanDate = item.InPlanData;
            textFloorInfo.setText(item.BaseFloor+"\n"+item.TotalFloor);
            //String progressDate = item.ProgressDate;


            if (!exProgressDate.equals(""))
                textViewExYearMonth.setText(exProgressDate.substring(0, 4) + "\n" + exProgressDate.substring(5));
            else
                textViewExYearMonth.setText("");
            textViewExProgressFloor.setText(item.ExProgressFloor);
            if (!inPlanDate.equals("")){
                textViewYearMonth.setText(inPlanDate.substring(0, 4) + "\n" + inPlanDate.substring(5));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                // params.height = XX;
                // params.width = XX;
                textViewYearMonth.setLayoutParams(params);
                //textViewYearMonth.setWidth(70*dip);
                textViewYearMonth.setBackgroundResource(0);
            }
            else {
                textViewYearMonth.setText("");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30*dip, LinearLayout.LayoutParams.WRAP_CONTENT);
               // params.height = XX;
               // params.width = XX;
                textViewYearMonth.setLayoutParams(params);
                //textViewYearMonth.setWidth(70*dip);
                textViewYearMonth.setBackgroundResource(R.drawable.outline_more_horiz_24);
            }

            tvYearMonthLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int year;
                    int month;
                    int day;

                    if(item.InPlanData.equals("")){//?????????????????? ???????????? ?????? ?????????
                        year=tyear;
                        month=tmonth+1;
                        day=tdate;
                    }
                    else{
                        String date[] = item.InPlanData.split("-");
                        year = Integer.parseInt(date[0]);
                        month = Integer.parseInt(date[1]);
                        day = Integer.parseInt(date[2]);
                    }

                    showDateTimePicker(year, month, day, textViewYearMonth, item);
                }
            });

            tvYearMonthLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                   /* if(!item.UserCode.equals(Users.UserID)){
                        return false;
                    }*/

                    if (textViewYearMonth.getText().toString().equals("")) {
                        return false;
                    }

                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("??????????????? ??????")
                            .setMessage("???: " + item.Dong + "\n" +
                                    "???????????????: " + item.InPlanData + "\n" + "?????????????????? ?????????????????????????")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            updateInplanData(-1, -1, -1, textViewYearMonth, item);
                                        }
                                    }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return false;
                }
            });

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
                        //??????
                        if (!v.getText().toString().equals("")) {
                            setDongProgressFloorReturn(item.Dong, v, item, v.getText().toString());
                        } else {
                            Toast.makeText(context, "???????????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }
            });

            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                   /* if(!item.UserCode.equals(Users.UserID)){
                        return false;
                    }*/

                    if (edtProgressFloor.getText().toString().equals("")) {
                        return false;
                    }

                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("????????? ??????")
                            .setMessage("???: " + item.Dong + "\n" +
                                    "?????????: " + item.ProgressDate + "\n" + "???????????? ?????????????????????????")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteDongProgressFloorReturn(item.Dong, item);
                                        }
                                    }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return false;
                }
            });

            /*recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    edtProgressFloor.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    layoutTop.requestFocus();
                    notifyDataSetChanged();
                    HideKeyBoard(context);
                    return false;
                }
            });*/

            edtProgressFloor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        notifyItemChanged(position);
                        //edtProgressFloor.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                        //layoutTop.requestFocus();
                        //notifyDataSetChanged();
                        //HideKeyBoard(context);
                    }
                }
            });
        }

        private void showDateTimePicker(int year, int month, int date, TextView textViewYearMonth, Dong item) {
            DatePickerDialog dpd = new DatePickerDialog
                    (context,
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker view,
                                                      int year, int monthOfYear, int dayOfMonth) {
                                    //DATA????????????
                                    updateInplanData(year, monthOfYear, dayOfMonth, textViewYearMonth, item);
                                }
                            }
                            , // ???????????? ???????????? ??? ??????????????? ???????????????
                            //    ????????? ????????? ??????
                            year, month-1, date); // ????????? ?????????
            dpd.show();
        }
    }

    public void addItem(Dong item) {
        items.add(item);
    }

    public void setItems(ArrayList<Dong> items) {
        this.items = items;
    }

    public Dong getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Dong item) {
        items.set(position, item);
    }


    public void updateInplanData(int year, int month, int day, TextView textViewYearMonth, Dong item) {
        String url = context.getString(R.string.service_address) + "updateInplanData";
        String fromDate;
        if(year==-1){
            fromDate="";
        }
        else{
            fromDate = year + "-" + (month + 1) + "-" + day;
        }

        ContentValues values = new ContentValues();

        values.put("ContractNo", contractNo);
        values.put("Dong", item.Dong);
        values.put("InPlanData", fromDate);

        UpdateInplanData gsod = new UpdateInplanData(url, values, year, month, day, textViewYearMonth, item);
        gsod.execute();
    }

    public class UpdateInplanData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        Dong item;
        TextView textViewYearMonth;
        int year;
        int month;
        int day;

        UpdateInplanData(String url, ContentValues values, int year, int month, int day, TextView textViewYearMonth, Dong item) {
            this.url = url;
            this.values = values;
            this.item = item;
            this.textViewYearMonth=textViewYearMonth;
            this.year=year;
            this.month=month;
            this.day=day;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("????????????", "??????/????????????");
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {

                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.InPlanData=child.getString("InPlanData");
                }

                layoutTop.requestFocus();

                if(year==-1){//??????????????? ??????
                    textViewYearMonth.setText("");
                    notifyDataSetChanged();
                    HideKeyBoard(context);
                    Toast.makeText(context, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                else{//??????????????? ??????
                    tyear = year;
                    tmonth = month;
                    tdate = day;
                    String fromDate = tyear + "-" + (tmonth + 1) + "-" + tdate;
                    textViewYearMonth.setText(fromDate.substring(0, 4) + "\n" + fromDate.substring(5));
                    notifyDataSetChanged();
                    HideKeyBoard(context);
                    Toast.makeText(context, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
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

    public void setDongProgressFloorReturn(String dong, TextView v, Dong item, String progressFloor) {
        String url = context.getString(R.string.service_address) + "setDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        values.put("ProgressFloor", progressFloor);
        values.put("UserCode", Users.PhoneNumber);
        SetDongProgressFloorReturn gsod = new SetDongProgressFloorReturn(url, values, v, item, fromDate);
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
            //Log.i("????????????", "??????/????????????");
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {

                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
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
                Toast.makeText(context, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
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
            //Log.i("????????????", "??????/????????????");
            //progress bar??? ???????????? ????????? ??????
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????
            try {

                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
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
                Toast.makeText(context, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

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


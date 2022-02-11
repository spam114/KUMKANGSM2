package com.kumkangkind.kumkangsm2.ProgressFloor;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
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

import com.google.android.material.textfield.TextInputEditText;
import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BackPressEditText;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Dong;
import com.kumkangkind.kumkangsm2.R;

import java.util.ArrayList;

public class ProgressFloorReturnViewAdapter extends ArrayAdapter<Dong> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    LinearLayout layoutTop;
    ListView listview;
    com.kumkangkind.kumkangsm2.BackPressEditText edtProgressFloor;
    public ProgressFloorReturnViewAdapter(Context context, int layoutResourceID, ArrayList data, LinearLayout layoutTop, ListView listview) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.layoutTop=layoutTop;
        this.listview=listview;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.progress_floor_return_row, null);
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        Dong item = (Dong) data.get(position);
        if (item != null) {

            TextView textViewDong = (TextView) row.findViewById(R.id.tvDong);
            TextView textViewConstructionEmployee = (TextView) row.findViewById(R.id.tvConstructionEmployee);
            TextView textViewExYearMonth = (TextView) row.findViewById(R.id.tvExYearMonth);
            TextView textViewExProgressFloor = (TextView) row.findViewById(R.id.tvExProgressFloor);
            TextView textViewYearMonth = (TextView) row.findViewById(R.id.tvYearMonth);
            edtProgressFloor = row.findViewById(R.id.edtProgressFloor);
            edtProgressFloor.setOnBackPressListener(new BackPressEditText.OnBackPressListener() {
                @Override
                public void onBackPress() {
                    layoutTop.requestFocus();
                }
            });
            edtProgressFloor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) { // IME_ACTION_SEARCH , IME_ACTION_GO

                        //edtProgressFloor.clearFocus();
                        //layoutTop.requestFocus();
                        layoutTop.requestFocus();
                        HideKeyBoard(context);
                    }
                    return false;
                }
            });

            textViewDong.setText(((Dong) data.get(position)).Dong);
            textViewConstructionEmployee.setText(((Dong) data.get(position)).CollectEmployee);
            //textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressDate);
            textViewExProgressFloor.setText(((Dong) data.get(position)).ExProgressFloor);
            //.setText(((Dong) data.get(position)).ProgressDate);
            edtProgressFloor.setText(((Dong) data.get(position)).ProgressFloor);
            String exProgressDate = ((Dong) data.get(position)).ExProgressDate;
            String progressDate = ((Dong) data.get(position)).ProgressDate;

            if(!exProgressDate.equals(""))
                textViewExYearMonth.setText(exProgressDate.substring(0,4)+"\n"+exProgressDate.substring(5));
            else
                textViewExYearMonth.setText("");
            textViewExProgressFloor.setText(((Dong) data.get(position)).ExProgressFloor);
            if(!progressDate.equals(""))
                textViewYearMonth.setText(progressDate.substring(0,4)+"\n"+progressDate.substring(5));
            else
                textViewYearMonth.setText("");
        }

        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtProgressFloor.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                //edtOrderQty.clearFocus();
                //edtDiscountRate.clearFocus();
                edtProgressFloor.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                //edtRemark.clearFocus();
                //edtRemark2.clearFocus();

                layoutTop.requestFocus();
                HideKeyBoard(context);
                return false;
            }
        });

        return row;
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


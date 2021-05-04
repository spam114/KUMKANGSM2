package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 리스트 뷰를 구현하기 위한 어댑터 클래스다: 음성용
 */
public class SwListVIewAdapter2 extends ArrayAdapter<SuWorder> {

    Context context;
    int layoutRsourceId;
    //SuWorder[] data;
    ArrayList data;

    public SwListVIewAdapter2(Context context, int layoutResourceID, ArrayList data) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_row2, null);
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        SuWorder item = (SuWorder) data.get(position);
        if (item != null) {


            String dongStr=((SuWorder) data.get(position)).Dong;
            String dongArr[]=dongStr.split(",");
            if(dongArr.length>1)
                dongStr=dongArr[0]+"외";
            else
                dongStr=dongArr[0];

            TextView textViewStartTime = (TextView) row.findViewById(R.id.textViewStartTime);
            TextView textViewDong = (TextView) row.findViewById(R.id.textViewDong);
            TextView textViewLocation = (TextView) row.findViewById(R.id.textViewLocation);
            TextView textViewStatus = (TextView) row.findViewById(R.id.textViewStatus);
            TextView textViewCustomer = (TextView) row.findViewById(R.id.textViewCustomer);
            TextView textViewWorkTypeName = (TextView) row.findViewById(R.id.textViewWorkTypeName);
            TextView textViewSupervisorName = (TextView) row.findViewById(R.id.textViewSupervisorName);

            textViewStartTime.setText(((SuWorder) data.get(position)).StartTime.substring(2, 10).replace("-",""));
            textViewDong.setText(dongStr);
            textViewLocation.setText(((SuWorder) data.get(position)).LocationName);
            textViewCustomer.setText(((SuWorder) data.get(position)).CustomerName);
            textViewWorkTypeName.setText(((SuWorder) data.get(position)).WorkTypeName);
            textViewSupervisorName.setText(((SuWorder) data.get(position)).Supervisor);

            if (((SuWorder) data.get(position)).Status.equals("0")) {
                textViewStatus.setText("요청(확인)");
                textViewStatus.setTextColor(Color.BLACK);
            } else if (((SuWorder) data.get(position)).Status.equals("1")) {
                textViewStatus.setText("작성");
                textViewStatus.setTextColor(Color.BLUE);
            } else if (((SuWorder) data.get(position)).Status.equals("2")) {
                textViewStatus.setText("확정");
                textViewStatus.setTextColor(Color.RED);
            } else if (((SuWorder) data.get(position)).Status.equals("-1")) {
                textViewStatus.setText("요청");
                textViewStatus.setTextColor(Color.BLACK);
            }
        }

        return row;
    }
}


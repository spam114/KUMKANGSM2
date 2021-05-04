package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ASItemAdapter extends ArrayAdapter<ASItem> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    Handler mHandler;
    ProgressDialog mProgressDialog;

    public ASItemAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.listview_asitem_row, null);
        }

        ASItem item = (ASItem) data.get(position);
        if (item != null) {

            TextView textViewDong = (TextView) row.findViewById(R.id.textViewDong);
            TextView textViewSupervisor = (TextView) row.findViewById(R.id.textViewSupervisor);
            TextView textViewLocation = (TextView) row.findViewById(R.id.textViewLocation);
            TextView textViewDivision = (TextView) row.findViewById(R.id.textViewDivision);
            TextView textViewPartName = (TextView) row.findViewById(R.id.textViewPartName);
            TextView textViewPartSpec = (TextView) row.findViewById(R.id.textViewPartSpec);
            TextView textViewQty = (TextView) row.findViewById(R.id.textViewQty);
            TextView textViewCause = (TextView) row.findViewById(R.id.textViewCause);
            TextView textViewASDivision = (TextView) row.findViewById(R.id.textViewASDivision);
            TextView textViewComplain = (TextView) row.findViewById(R.id.textViewComplain);
            TextView textViewMeasure = (TextView) row.findViewById(R.id.textViewMeasure);
            TextView textViewMeasurer = (TextView) row.findViewById(R.id.textViewMeasurer);



            textViewDong.setText(((ASItem) data.get(position)).Dong);
            textViewSupervisor.setText(((ASItem) data.get(position)).SupervisorName);
            textViewSupervisor.setTag(((ASItem) data.get(position)).SupervisorCode);//태그에 supervisorCode 저장
            textViewLocation.setText(((ASItem) data.get(position)).Ho+" "+((ASItem) data.get(position)).HoLocation);
            textViewDivision.setText(((ASItem) data.get(position)).ItemType);
            textViewPartName.setText(((ASItem) data.get(position)).Item);
            textViewPartSpec.setText(((ASItem) data.get(position)).ItemSpecs);
            textViewQty.setText(((ASItem) data.get(position)).Quantity);
            textViewCause.setText(((ASItem) data.get(position)).Reason);
            textViewASDivision.setText(((ASItem) data.get(position)).AsType);
            textViewComplain.setText(((ASItem) data.get(position)).Remark);
            textViewMeasure.setText(((ASItem) data.get(position)).Actions);
            textViewMeasurer.setText(((ASItem) data.get(position)).ActionEmployee);

        }
        return row;
    }


}


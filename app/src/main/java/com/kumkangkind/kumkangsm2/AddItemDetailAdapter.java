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

public class AddItemDetailAdapter extends ArrayAdapter<AddItemDetail> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    Handler mHandler;
    ProgressDialog mProgressDialog;

    public AddItemDetailAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.listview_additem_detail_row, null);
        }

        AddItemDetail item = (AddItemDetail) data.get(position);
        if (item != null) {

            TextView textViewHo = (TextView) row.findViewById(R.id.textViewHo);
            TextView textViewLocation = (TextView) row.findViewById(R.id.textViewLocation);
            TextView textViewDivision = (TextView) row.findViewById(R.id.textViewDivision);
            TextView textViewPart = (TextView) row.findViewById(R.id.textViewPart);
            TextView textViewQTY = (TextView) row.findViewById(R.id.textViewQTY);
            TextView textViewRemark = (TextView) row.findViewById(R.id.textViewRemark);


            textViewHo.setText(((AddItemDetail) data.get(position)).Ho);
            textViewLocation.setText(((AddItemDetail) data.get(position)).HoLocation);
            textViewDivision.setText(((AddItemDetail) data.get(position)).AddType);
            textViewPart.setText(((AddItemDetail) data.get(position)).Part);
            textViewQTY.setText(((AddItemDetail) data.get(position)).Qty);
            textViewRemark.setText(((AddItemDetail) data.get(position)).Remark);

        }
        return row;
    }


}


package com.kumkangkind.kumkangsm2.ProgressFloor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.Dong;
import com.kumkangkind.kumkangsm2.R;

import java.util.ArrayList;

public class ProgressFloorViewAdapter extends ArrayAdapter<Dong> {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    public ProgressFloorViewAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.progress_floor_row, null);
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        Dong item = (Dong) data.get(position);
        if (item != null) {

            TextView textViewDong = (TextView) row.findViewById(R.id.tvDong);
            TextView textViewConstructionEmployee = (TextView) row.findViewById(R.id.tvConstructionEmployee);
            TextView textViewExYearMonth = (TextView) row.findViewById(R.id.tvExYearMonth);
            TextView textViewExProgressFloor = (TextView) row.findViewById(R.id.tvExProgressFloor);
            TextView textViewYearMonth = (TextView) row.findViewById(R.id.tvYearMonth);
            TextView textViewProgressFloor = (TextView) row.findViewById(R.id.tvProgressFloor);

            textViewDong.setText(((Dong) data.get(position)).Dong);
            textViewConstructionEmployee.setText(((Dong) data.get(position)).ConstructionEmployee);
            textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressYearMonth);
            textViewExProgressFloor.setText(((Dong) data.get(position)).ExProgressFloor);
            textViewYearMonth.setText(((Dong) data.get(position)).ProgressYearMonth);
            textViewProgressFloor.setText(((Dong) data.get(position)).ProgressFloor);
        }

        return row;
    }
}


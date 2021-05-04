package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AssignmentViewAdapter extends ArrayAdapter<Dong> {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    public AssignmentViewAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.assignment_row, null);
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        Dong item = (Dong) data.get(position);
        if (item != null) {

            TextView textViewDong = (TextView) row.findViewById(R.id.tvDong);
            TextView textViewConstructionEmployee = (TextView) row.findViewById(R.id.tvConstructionEmployee);

            textViewDong.setText(((Dong) data.get(position)).Dong);
            textViewConstructionEmployee.setText(((Dong) data.get(position)).ConstructionEmployee);
        }

        return row;
    }
}


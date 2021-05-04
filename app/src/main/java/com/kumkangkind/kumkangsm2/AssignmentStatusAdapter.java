package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AssignmentStatusAdapter extends ArrayAdapter<Dong> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    ImageView imageView;

    public AssignmentStatusAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.assignment_status_row, null);
            // row = inflater.inflate(layoutRsourceId, parent, false);

        }

        Dong item = (Dong) data.get(position);
        if (item != null) {

            final String dong=((Dong) data.get(position)).Dong;
            TextView textViewDong = (TextView) row.findViewById(R.id.tvDong);
            TextView textViewConstructionEmployee = (TextView) row.findViewById(R.id.tvConstructionEmployee);
            imageView= row.findViewById(R.id.imb);

            textViewDong.setText(dong);
            textViewConstructionEmployee.setText(((Dong) data.get(position)).ConstructionEmployee);
            if(((Dong) data.get(position)).ConstructionEmployee.equals("미배정")) {//추가버튼
                imageView.setImageResource(R.drawable.add);
                imageView.setOnClickListener(new View.OnClickListener() {//리스너
                    @Override
                    public void onClick(View v) {
                        ((AssignmentStatusActivity)context).ShowAddDialog(dong, position);
                    }
                });
            }
            else {//삭제버튼

                imageView.setImageResource(R.drawable.delete);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AssignmentStatusActivity)context).ShowDeleteDialog(dong, position);
                    }
                });
            }
        }

        return row;
    }
}


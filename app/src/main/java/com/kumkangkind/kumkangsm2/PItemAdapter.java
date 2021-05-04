package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 리스트 뷰를 구현하기 위한 어댑터 클래스다.
 */
public class PItemAdapter extends ArrayAdapter<WoItem> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    Handler mHandler;
    ProgressDialog mProgressDialog;
    WoItem currentItem;
    public int removePosition;

    public PItemAdapter(Context context, int layoutResourceID, ArrayList data) {

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
            row = inflater.inflate(R.layout.listview_itemrow, null);
        }

        WoItem item = (WoItem) data.get(position);
        if (item != null) {

            TextView textViewItemSeqNo = (TextView) row.findViewById(R.id.textViewItemSeqNo);
            TextView textViewItemName = (TextView) row.findViewById(R.id.textViewItemName);
            TextView textViewPerosnCount = (TextView) row.findViewById(R.id.textViewPersonCount);
            TextView textViewQuantity = (TextView) row.findViewById(R.id.textViewQuantity);
            Button deleteButton = (Button) row.findViewById(R.id.buttonDelete);

            deleteButton.setFocusable(false);

            if (textViewItemSeqNo != null)
                textViewItemSeqNo.setText( String.valueOf(position + 1));
            if (textViewItemName != null)
                textViewItemName.setText(((WoItem) (data.get(position))).ItemName);
            if (textViewPerosnCount != null)
                textViewPerosnCount.setText(((WoItem) (data.get(position))).PersonCount);
            if (textViewQuantity != null)
                textViewQuantity.setText(((WoItem) (data.get(position))).Quantity);

            deleteButton.setTag(position);
        }
        return row;
    }
}


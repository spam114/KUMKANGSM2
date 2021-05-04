package com.kumkangkind.kumkangsm2.sale;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.R;

import java.util.ArrayList;
import java.util.Locale;

public class CustomerListViewAdapter extends ArrayAdapter<Customers> {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    private ArrayList<Customers> arraylist;

    public CustomerListViewAdapter(Context context, int layoutResourceID, ArrayList data) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;

        this.arraylist = new ArrayList<Customers>();
        this.arraylist.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_customer_row, null);
        }

        Customers item = (Customers) data.get(position);
        if (item != null) {

            TextView textViewLocation = (TextView) row.findViewById(R.id.textViewLocation);
            TextView textViewCustomer = (TextView) row.findViewById(R.id.textViewCustomer);
            TextView textViewProject = (TextView) row.findViewById(R.id.textViewProject);

            textViewLocation.setText(((Customers) data.get(position)).LocationName);
            textViewCustomer.setText(((Customers) data.get(position)).CustomerName);
            textViewProject.setText(((Customers) data.get(position)).ContractMasterNo);
        }
        return row;
    }



    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        data.clear();

        if (charText.length() == 0) {
            data.addAll(arraylist);
        }
        else
        {
            for (Customers cus : arraylist)
            {
                if (cus.LocationName.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    data.add(cus);
                }
            }
        }
        notifyDataSetChanged();
    }
}

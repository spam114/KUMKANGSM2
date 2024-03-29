package com.kumkangkind.kumkangsm2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kumkangkind.kumkangsm2.ActivityStockInCertificateDetail;
import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Object.StockInCertificate;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.RegisterActivity2;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockInCertificateAdapter extends RecyclerView.Adapter<StockInCertificateAdapter.ViewHolder> implements BaseActivityInterface, Filterable {

    Context context;
    ArrayList<StockInCertificate> items = new ArrayList<>();

    ArrayList<StockInCertificate> unFilteredlist;//for filter
    ArrayList<StockInCertificate> filteredList;//for filter

    public StockInCertificateAdapter(ArrayList<StockInCertificate> items, Context context) {
        super();
        this.context = context;
        this.items = items;
        this.unFilteredlist = items;
        this.filteredList = items;
    }

    public void updateAdapter(ArrayList<StockInCertificate> newCountries) {
        items.clear();
        items.addAll(newCountries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.stock_in_certificate_row, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        StockInCertificate item = filteredList.get(position);//for filter
        viewHolder.setItem(item, position); //왜오류
    }

    @Override
    public int getItemCount() {
        return filteredList.size();//for filter
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<StockInCertificate> filteringList = new ArrayList<>();
                    for (StockInCertificate sic : unFilteredlist) {
                        if (sic.LocationName.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(sic);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<StockInCertificate>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //보통은 ViewHolder를 Static 으로 쓴다.
    //범용성을 위해서, 나는 제거함
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvNo;
        TextView tvCustomer;
        TextView tvLocation;
        TextView tvCarNo;
        TextView tvName;
        TextView tvActualWeight;
        View row;

        public ViewHolder(View itemVIew) {
            super(itemVIew);
            this.row = itemVIew;
            tvDate = itemVIew.findViewById(R.id.tvDate);
            tvNo = itemVIew.findViewById(R.id.tvNo);
            tvCustomer = itemVIew.findViewById(R.id.tvCustomer);
            tvLocation = itemVIew.findViewById(R.id.tvLocation);
            tvCarNo = itemVIew.findViewById(R.id.tvCarNo);
            tvName = itemVIew.findViewById(R.id.tvName);
            tvActualWeight = itemVIew.findViewById(R.id.tvActualWeight);
        }

        public void setItem(StockInCertificate item, int position) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            tvDate.setText(item.StartTime);
            tvNo.setText(item.CertificateNo);
            tvCustomer.setText(item.CustomerName);
            //.setText(((Dong) data.get(position)).ProgressDate);
            //tvLocation.setTag(item);
            tvLocation.setText(item.LocationName);
            tvCarNo.setText(item.CarNo);
            tvName.setText(item.InsertUser);
            tvActualWeight.setText(myFormatter.format(Double.parseDouble(item.ActualWeight)));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i;
                    i = new Intent(context, ActivityStockInCertificateDetail.class);
                    i.putExtra("certificateNo", item.CertificateNo);
                    i.putExtra("customerLocationName",item.CustomerName + "(" +item.LocationName+")");
                    i.putExtra("supervisorCode",item.SupervisorCode);
                    context.startActivity(i);
                }
            });

           /* if (!exProgressDate.equals(""))
                textViewExYearMonth.setText(exProgressDate.substring(0, 4) + "\n" + exProgressDate.substring(5));
            else
                textViewExYearMonth.setText("");
            textViewExProgressFloor.setText(item.ExProgressFloor);
            if (!inPlanDate.equals(""))
                textViewYearMonth.setText(inPlanDate.substring(0, 4) + "\n" + inPlanDate.substring(5));
            else
                textViewYearMonth.setText("");*/

        }
    }

    public void addItem(StockInCertificate item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<StockInCertificate> items) {
        this.items = items;
    }

    public StockInCertificate getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, StockInCertificate item) {
        items.set(position, item);
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


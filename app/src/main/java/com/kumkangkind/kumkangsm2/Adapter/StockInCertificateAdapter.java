package com.kumkangkind.kumkangsm2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StockInCertificateAdapter extends RecyclerView.Adapter<StockInCertificateAdapter.ViewHolder> implements BaseActivityInterface {

    Context context;
    ArrayList<StockInCertificate> items = new ArrayList<>();

    public StockInCertificateAdapter(Context context) {
        super();
        this.context = context;
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
        StockInCertificate item = items.get(position);
        viewHolder.setItem(item, position); //왜오류
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    /*public void setDongProgressFloorReturn(String dong, TextView v, Dong item, String progressFloor) {
        String url = context.getString(R.string.service_address) + "setDongProgressFloorReturn";
        ContentValues values = new ContentValues();
        values.put("ContractNo", contractNo);
        values.put("Dong", dong);
        values.put("FromDate", fromDate);
        values.put("ProgressFloor", progressFloor);
        SetDongProgressFloorReturn gsod = new SetDongProgressFloorReturn(url, values, v, item, fromDate);
        gsod.execute();
    }*/

    /*public class SetDongProgressFloorReturn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String fromDate;
        TextView v;
        Dong item;

        SetDongProgressFloorReturn(String url, ContentValues values, TextView v, Dong item, String fromDate) {
            this.url = url;
            this.values = values;
            this.fromDate = fromDate;
            this.v = v;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //Log.i("순서확인", "미납/재고시작");
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {

                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //layoutTop.requestFocus();
                item.ProgressFloor = v.getText().toString();
                item.ProgressDate = this.fromDate;
                notifyDataSetChanged();
                HideKeyBoard(context);
                Toast.makeText(context, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
    }*/

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


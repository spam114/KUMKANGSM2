package com.kumkangkind.kumkangsm2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Object.SaleOrder;
import com.kumkangkind.kumkangsm2.ProductionProgressActivity;
import com.kumkangkind.kumkangsm2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SaleOrderAdapter extends ArrayAdapter<SaleOrder> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private ArrayList<SaleOrder> listViewItemList = new ArrayList<SaleOrder>() ;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<SaleOrder> filteredItemList;
    Filter listFilter;

    TextView txtSaleOrderNo;
    TextView txtDong;
    TextView txtRequestTypeName;
    TextView txtReceiptFlagName;
    TextView txtRemark;
    String customerName;
    String locationName;


   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    int checkedQty = 0;


    public SaleOrderAdapter(Context context, int layoutResourceID, ArrayList data, ListView listView, String customerName, String locationName) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.listViewItemList = data;
        this.filteredItemList=listViewItemList;
        this.customerName = customerName;
        this.locationName = locationName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        SaleOrder item = (SaleOrder) filteredItemList.get(position);
        if (item != null) {
            String worderRequestNo = item.WorderRequestNo;
            String saleOrderNo = item.SaleOrderNo;
            String remark= item.Remark;
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            //ImageView imvRemove;
            //imvRemove= row.findViewById(R.id.imvRemove);

            txtSaleOrderNo = row.findViewById(R.id.txtSaleOrderNo);
            txtDong = row.findViewById(R.id.txtDong);
            txtRequestTypeName = row.findViewById(R.id.txtRequestTypeName);
            txtReceiptFlagName = row.findViewById(R.id.txtReceiptFlagName);
            txtRemark = row.findViewById(R.id.txtRemark);
            txtSaleOrderNo.setText(saleOrderNo);
            txtDong.setText(item.Dong);
            txtRequestTypeName.setText(item.RequestTypeName);
            txtReceiptFlagName.setText(item.ReceiptFlagName);
            txtRemark.setText(item.Remark);

            /*edtDiscountRate.setTag(item);
            edtOrderQty.setTag(item);
            txtOrderPrice.setTag(item);*/

            //edtOrderQty.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //edtDiscountRate.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            //edtOrderQty.setText(item.orderQty);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //worderRequestNo

                    Intent i;
                    i = new Intent(context, ProductionProgressActivity.class);//todo

                    i.putExtra("saleOrderNo", saleOrderNo);
                    i.putExtra("remark", remark);
                    i.putExtra("customerName", customerName);
                    i.putExtra("locationName", locationName);
                    i.putExtra("worderRequestNo", worderRequestNo);
                    context.startActivity(i);

                }
            });
        }
        return row;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public SaleOrder getItem(int position) {
        return filteredItemList.get(position) ;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity) context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity) context, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) context, message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }

    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }

        return listFilter ;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;
            } else {
                ArrayList<SaleOrder> itemList = new ArrayList<SaleOrder>() ;

                for (SaleOrder item : listViewItemList) {
                    if(constraint.toString().equals("전체")){
                        itemList.add(item) ;
                    }
                    else{
                        if (item.Dong.toUpperCase().equals(constraint.toString().toUpperCase()) ){
                       /* if (item.PartName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase()))*/
                            itemList.add(item) ;
                        }
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<SaleOrder>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
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
}


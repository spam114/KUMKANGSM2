package com.kumkangkind.kumkangsm2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Object.LocationProgress;
import com.kumkangkind.kumkangsm2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LocationProgressAdapter extends ArrayAdapter<LocationProgress> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private ArrayList<LocationProgress> listViewItemList = new ArrayList<LocationProgress>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<LocationProgress> filteredItemList;
    //Filter listFilter;

    TextView txtCustomerLocation;
    TextView txt19;
    TextView txt20;
    TextView txt21;
    TextView txt22;
    TextView txt23;
    TextView txt24;
    //TextView txt25;
    TextView txt26;
    TextView txt27;
    TextView txt28;
    TextView txt29;
    TextView txt30;
    TextView txt31;
    TextView txt32;
    TextView txt33;
    TextView txt34;
    TextView txt35;
    TextView txt36;
    TextView txt37;
    TextView txt38;
    TextView txt39;
    TextView txt40;
    TextView txt41;
    TextView txt42;
    TextView txt43;
    TextView txt44;
    TextView txt45;
    TextView txt46;
    TextView txt47;
    TextView txt48;
    TextView txt49;
    TextView txt50;
    TextView txt51;
    TextView txt52;
    int type;

   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    int checkedQty = 0;


    public LocationProgressAdapter(Context context, int layoutResourceID, ArrayList data, int type) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.listViewItemList = data;
        this.type = type;
        //this.filteredItemList=listViewItemList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        LocationProgress item = (LocationProgress) listViewItemList.get(position);
        if (item != null) {
          /*  String worderRequestNo = item.WorderRequestNo;
            String LocationProgressNo = item.LocationProgressNo;
            String remark= item.Remark;
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            //ImageView imvRemove;
            //imvRemove= row.findViewById(R.id.imvRemove);*/
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            txtCustomerLocation = row.findViewById(R.id.txtCustomerLocation);
            txt19 = row.findViewById(R.id.txt19);
            txt20 = row.findViewById(R.id.txt20);
            txt21 = row.findViewById(R.id.txt21);
            txt22 = row.findViewById(R.id.txt22);
            txt23 = row.findViewById(R.id.txt23);
            txt24 = row.findViewById(R.id.txt24);
            //txt25 = row.findViewById(R.id.txt25);
            txt26 = row.findViewById(R.id.txt26);
            txt27 = row.findViewById(R.id.txt27);
            txt28 = row.findViewById(R.id.txt28);
            txt29 = row.findViewById(R.id.txt29);
            txt30 = row.findViewById(R.id.txt30);
            txt31 = row.findViewById(R.id.txt31);
            txt32 = row.findViewById(R.id.txt32);
            txt33 = row.findViewById(R.id.txt33);
            txt34 = row.findViewById(R.id.txt34);
            txt35 = row.findViewById(R.id.txt35);
            txt36 = row.findViewById(R.id.txt36);
            txt37 = row.findViewById(R.id.txt37);
            txt38 = row.findViewById(R.id.txt38);
            txt39 = row.findViewById(R.id.txt39);
            txt40 = row.findViewById(R.id.txt40);
            txt41 = row.findViewById(R.id.txt41);
            txt42 = row.findViewById(R.id.txt42);
            txt43 = row.findViewById(R.id.txt43);
            txt44 = row.findViewById(R.id.txt44);
            txt45 = row.findViewById(R.id.txt45);
            txt46 = row.findViewById(R.id.txt46);
            txt47 = row.findViewById(R.id.txt47);
            txt48 = row.findViewById(R.id.txt48);
            txt49 = row.findViewById(R.id.txt49);
            txt50 = row.findViewById(R.id.txt50);
            txt51 = row.findViewById(R.id.txt51);
            txt52 = row.findViewById(R.id.txt52);

            setFieldVisibleState();

            txtCustomerLocation.setText(item.CustomerName + "\n" + item.LocationName);
            String content = txtCustomerLocation.getText().toString(); //텍스트 가져옴.
            SpannableString spannableString = new SpannableString(content); //객체 생성
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), item.CustomerName.length(), item.CustomerName.length() + item.LocationName.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtCustomerLocation.setText(spannableString);

            //알폼
            txt19.setText(myFormatter.format(Double.parseDouble(item.AlOutWeight)));
            txt20.setText(myFormatter.format(Double.parseDouble(item.AlOutWeightG)));
            txt21.setText(myFormatter.format(Double.parseDouble(item.AlInWeight)));
            txt22.setText(myFormatter.format(Double.parseDouble(item.Alminus)));
            txt23.setText(item.AlRate);
            txt24.setText(myFormatter.format(Double.parseDouble(item.AlOver)));

            //스틸
            //txt25.setText(myFormatter.format(Double.parseDouble(item.StOutWeightG)));
            txt26.setText(myFormatter.format(Double.parseDouble(item.StOutWeight)));
            txt27.setText(myFormatter.format(Double.parseDouble(item.StNewInWeight)));
            txt28.setText(myFormatter.format(Double.parseDouble(item.StInWeight)));
            txt29.setText(item.StRate);

            //부속철물
            txt30.setText(myFormatter.format(Double.parseDouble(item.OtOutWeightG)));
            txt31.setText(myFormatter.format(Double.parseDouble(item.OtInWeight)));
            txt32.setText(item.OtRate);

            //알서포트
            txt33.setText(myFormatter.format(Double.parseDouble(item.SpOutQty)));
            txt34.setText(myFormatter.format(Double.parseDouble(item.SpInQty)));
            txt35.setText(item.SpQRate);
            txt36.setText(myFormatter.format(Double.parseDouble(item.SpOutWeightG)));
            txt37.setText(myFormatter.format(Double.parseDouble(item.SpOutWeight)));
            txt38.setText(myFormatter.format(Double.parseDouble(item.SpInWeightG)));
            txt39.setText(item.SpWRate);

            //KD서포트
            txt40.setText(myFormatter.format(Double.parseDouble(item.KDSpOutQty)));
            txt41.setText(myFormatter.format(Double.parseDouble(item.KDSpInQty)));
            txt42.setText(item.KDSpQRate);
            txt43.setText(myFormatter.format(Double.parseDouble(item.KDSpOutWeightG)));
            txt44.setText(myFormatter.format(Double.parseDouble(item.KDSpOutWeight)));
            txt45.setText(myFormatter.format(Double.parseDouble(item.KDSpInWeightG)));
            txt46.setText(item.KDSpWRate);

            //KSBEAM
            txt47.setText(myFormatter.format(Double.parseDouble(item.KBOutQty)));
            txt48.setText(myFormatter.format(Double.parseDouble(item.KBInQty)));
            txt49.setText(item.KBQRate);
            txt50.setText(myFormatter.format(Double.parseDouble(item.KBOutWeightG)));
            txt51.setText(myFormatter.format(Double.parseDouble(item.KBInWeight)));
            txt52.setText(item.KBWRate);

            /*edtDiscountRate.setTag(item);
            edtOrderQty.setTag(item);
            txtOrderPrice.setTag(item);*/

            //edtOrderQty.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //edtDiscountRate.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            //edtOrderQty.setText(item.orderQty);

            /*row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //worderRequestNo

                    Intent i;
                    i = new Intent(context, ProductionProgressActivity.class);//todo

                    i.putExtra("LocationProgressNo", LocationProgressNo);
                    i.putExtra("remark", remark);
                    i.putExtra("customerName", customerName);
                    i.putExtra("locationName", locationName);
                    i.putExtra("worderRequestNo", worderRequestNo);
                    context.startActivity(i);

                }
            });*/


        }
        return row;
    }

    private void setFieldVisibleState() {
        txt19.setVisibility(View.GONE);
        txt20.setVisibility(View.GONE);
        txt21.setVisibility(View.GONE);
        txt22.setVisibility(View.GONE);
        txt23.setVisibility(View.GONE);
        txt24.setVisibility(View.GONE);
        //txt25.setVisibility(View.GONE);
        txt26.setVisibility(View.GONE);
        txt27.setVisibility(View.GONE);
        txt28.setVisibility(View.GONE);
        txt29.setVisibility(View.GONE);
        txt30.setVisibility(View.GONE);
        txt31.setVisibility(View.GONE);
        txt32.setVisibility(View.GONE);
        txt33.setVisibility(View.GONE);
        txt34.setVisibility(View.GONE);
        txt35.setVisibility(View.GONE);
        txt36.setVisibility(View.GONE);
        txt37.setVisibility(View.GONE);
        txt38.setVisibility(View.GONE);
        txt39.setVisibility(View.GONE);
        txt40.setVisibility(View.GONE);
        txt41.setVisibility(View.GONE);
        txt42.setVisibility(View.GONE);
        txt43.setVisibility(View.GONE);
        txt44.setVisibility(View.GONE);
        txt45.setVisibility(View.GONE);
        txt46.setVisibility(View.GONE);
        txt47.setVisibility(View.GONE);
        txt48.setVisibility(View.GONE);
        txt49.setVisibility(View.GONE);
        txt50.setVisibility(View.GONE);
        txt51.setVisibility(View.GONE);
        txt52.setVisibility(View.GONE);

        if (type == 0) {
            txt19.setVisibility(View.VISIBLE);
            txt20.setVisibility(View.VISIBLE);
            txt21.setVisibility(View.VISIBLE);
            txt22.setVisibility(View.VISIBLE);
            txt23.setVisibility(View.VISIBLE);
            txt24.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            //txt25.setVisibility(View.VISIBLE);
            txt26.setVisibility(View.VISIBLE);
            txt27.setVisibility(View.VISIBLE);
            txt28.setVisibility(View.VISIBLE);
            txt29.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            txt30.setVisibility(View.VISIBLE);
            txt31.setVisibility(View.VISIBLE);
            txt32.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            txt33.setVisibility(View.VISIBLE);
            txt34.setVisibility(View.VISIBLE);
            txt35.setVisibility(View.VISIBLE);
            txt36.setVisibility(View.VISIBLE);
            txt37.setVisibility(View.VISIBLE);
            txt38.setVisibility(View.VISIBLE);
            txt39.setVisibility(View.VISIBLE);
        } else if (type == 4) {
            txt40.setVisibility(View.VISIBLE);
            txt41.setVisibility(View.VISIBLE);
            txt42.setVisibility(View.VISIBLE);
            txt43.setVisibility(View.VISIBLE);
            txt44.setVisibility(View.VISIBLE);
            txt45.setVisibility(View.VISIBLE);
            txt46.setVisibility(View.VISIBLE);
        } else if (type == 5) {
            txt47.setVisibility(View.VISIBLE);
            txt48.setVisibility(View.VISIBLE);
            txt49.setVisibility(View.VISIBLE);
            txt50.setVisibility(View.VISIBLE);
            txt51.setVisibility(View.VISIBLE);
            txt52.setVisibility(View.VISIBLE);
        }
    }

  /*  @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public LocationProgress getItem(int position) {
        return filteredItemList.get(position) ;
    }*/

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
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
    }

  /*  @Override
    public int getCount() {
        return filteredItemList.size() ;
    }*/

   /* @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }

        return listFilter ;
    }*/

    /*private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;
            } else {
                ArrayList<LocationProgress> itemList = new ArrayList<LocationProgress>() ;

                for (LocationProgress item : listViewItemList) {
                    if(constraint.toString().equals("전체")){
                        itemList.add(item) ;
                    }
                    else{
                        if (item.Dong.toUpperCase().equals(constraint.toString().toUpperCase()) ){
                       *//* if (item.PartName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase()))*//*
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
            filteredItemList = (ArrayList<LocationProgress>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }*/

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


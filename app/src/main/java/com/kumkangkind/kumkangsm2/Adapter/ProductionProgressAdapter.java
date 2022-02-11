package com.kumkangkind.kumkangsm2.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kumkangkind.kumkangsm2.Application.ApplicationClass;
import com.kumkangkind.kumkangsm2.BaseActivityInterface;
import com.kumkangkind.kumkangsm2.Object.ProductionProgress;
import com.kumkangkind.kumkangsm2.ProductionProgressActivity;
import com.kumkangkind.kumkangsm2.R;
import com.kumkangkind.kumkangsm2.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductionProgressAdapter extends ArrayAdapter<ProductionProgress> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    ListView listView;

    TextView txtOrderQty;
    TextView txtAllocQty;
    TextView txtProcessQty;
    TextView txtInstructionQty;
    TextView txtProductionQty;
    TextView txtPackingQty;
    TextView txtOutQty;
    TextView txtPartName;
    TextView txtPartSpec;
    TextView txtMspec;
    String WorderRequestNo;


   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    //Filter listFilter;
    int checkedQty = 0;


    public ProductionProgressAdapter(Context context, int layoutResourceID, ArrayList data, ListView listView, String WorderRequestNo) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.listView = listView;
        this.WorderRequestNo=WorderRequestNo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        ProductionProgress item = (ProductionProgress) data.get(position);
        if (item != null) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            //ImageView imvRemove;
            //imvRemove= row.findViewById(R.id.imvRemove);

            txtOrderQty = row.findViewById(R.id.txtOrderQty);
            txtAllocQty = row.findViewById(R.id.txtAllocQty);
            txtProcessQty = row.findViewById(R.id.txtProcessQty);
            txtInstructionQty = row.findViewById(R.id.txtInstructionQty);
            txtProductionQty = row.findViewById(R.id.txtProductionQty);
            txtPackingQty = row.findViewById(R.id.txtPackingQty);
            txtOutQty = row.findViewById(R.id.txtOutQty);
            txtPartName=row.findViewById(R.id.txtPartName);
            txtPartSpec=row.findViewById(R.id.txtPartSpec);
            txtMspec = row.findViewById(R.id.txtMspec);

            String strOrderQty = myFormatter.format(Double.parseDouble(item.OrderQty));
            txtOrderQty.setText(strOrderQty);
            String strAllocQty = myFormatter.format(Double.parseDouble(item.AllocQty));
            txtAllocQty.setText(strAllocQty);
            String strProcessQty = myFormatter.format(Double.parseDouble(item.ProcessQty));
            txtProcessQty.setText(strProcessQty);
            String strInstructionQty = myFormatter.format(Double.parseDouble(item.InstructionQty));
            txtInstructionQty.setText(strInstructionQty);
            String strProductionQty = myFormatter.format(Double.parseDouble(item.ProductionQty));
            txtProductionQty.setText(strProductionQty);
            String strPackingQty = myFormatter.format(Double.parseDouble(item.PackingQty));
            txtPackingQty.setText(strPackingQty);
            String strOutQty = myFormatter.format(Double.parseDouble(item.OutQty));
            txtOutQty.setText(strOutQty);

            txtPartName.setText(item.PartName);
            txtPartSpec.setText(item.PartSpec);
            txtMspec.setText(item.Mspec);

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
                    getProductionDate(item.PartName, item.PartSpec, item.Mspec, WorderRequestNo, item.SeqNo);
                }
            });
        }
        return row;
    }

    private void getProductionDate(String PartName, String PartSpec, String Mspec, String WorderRequestNo, String SeqNo) {
        String url = context.getString(R.string.service_address) + "getProductionDate";
        ContentValues values = new ContentValues();
        values.put("WorderRequestNo", WorderRequestNo);
        values.put("SeqNo", SeqNo);
        GetProductionDate gsod = new GetProductionDate(url, values, PartName, PartSpec, Mspec);
        gsod.execute();
    }

    public class GetProductionDate extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String PartName;
        String PartSpec;
        String Mspec;

        GetProductionDate(String url, ContentValues values, String PartName, String PartSpec, String Mspec) {
            this.url = url;
            this.values = values;
            this.PartName=PartName;
            this.PartSpec=PartSpec;
            this.Mspec=Mspec;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
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
                String Part = "";
                String OriginalStartDate = "";
                String WorkDate = "";
                String CreateDate = "";
                String OutDate = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    OriginalStartDate = child.getString("OriginalStartDate");
                    WorkDate = child.getString("WorkDate");
                    CreateDate = child.getString("CreateDate");
                    OutDate = child.getString("OutDate");
                }

                if(jsonArray.length()>0){
                    Dialog productionDateDialog = new Dialog(context);
                    productionDateDialog.setContentView(R.layout.dialog_production_date);
                    productionDateDialog.setTitle("거래처 검색");

                    Display display = ((ProductionProgressActivity)(context)).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    Window window = productionDateDialog.getWindow();
                    int x = (int) (size.x * 0.9f);

                    window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT);

                    TextView txtPart = productionDateDialog.findViewById(R.id.txtPart);
                    TextView txtOriginalStartDate = productionDateDialog.findViewById(R.id.txtOriginalStartDate);
                    TextView txtWorkDate = productionDateDialog.findViewById(R.id.txtWorkDate);
                    TextView txtCreateDate = productionDateDialog.findViewById(R.id.txtCreateDate);
                    TextView txtOutDate = productionDateDialog.findViewById(R.id.txtOutDate);
                    Part=this.PartName+"("+this.PartSpec+")";
                    if(!this.Mspec.equals("")){
                        Part+="-"+this.Mspec;
                    }
                    txtPart.setText(Part);
                    txtOriginalStartDate.setText(OriginalStartDate);
                    txtWorkDate.setText(WorkDate);
                    txtCreateDate.setText(CreateDate);
                    txtOutDate.setText(OutDate);
                    /*buttonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //다얄로그 닫는다.
                            productionDateDialog.dismiss();


                        }
                    });*/
                    productionDateDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
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
    public void HideKeyBoard(Context context) {
        ApplicationClass.getInstance().HideKeyBoard((Activity) context);
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

package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class DongPersonListViewActivity extends Activity {
    private ListView listView1;
    //private SuWorder[] items;

    TextView tvCustomerLocation;
    String contractNo = "";
    String type = "";
    String restURL = "";
    String arrayName = "";
    String customerLocation = "";
    TreeMap<String, String> dongTreeMap;
    AssignmentViewAdapter adapter;
    ArrayList<Dong> dongArrayList;
    public HashMap<String, String> dongHashMap;
    String dong = "";
    String yearMonth = "";
    String floor = "";
    String inputYearMonth = "";
    String inputFloor = "";
    int clickPosition=0;
    View clickView=null;
    long clickId=0;
    String constructionEmployee="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_assignment);

        tvCustomerLocation = (TextView) findViewById(R.id.tvCustomerLocation);
        contractNo = getIntent().getStringExtra("contractNo");
        customerLocation = getIntent().getStringExtra("customerLocation");
        tvCustomerLocation.setText(customerLocation);
        dongHashMap = (HashMap<String, String>) (getIntent().getSerializableExtra("dongHashMap"));//todo
        dongTreeMap = new TreeMap<>(dongHashMap);

        // Set<String> keyset = dongHashMap.keySet();//TreeMap ??? ????????? Key????????? ??????
        // Iterator<String> keyIterator = dongTreeMap.keySet().iterator();

        listView1 = (ListView) findViewById(R.id.listView1);
        dongArrayList = new ArrayList<>();
        Dong dong= null;

        for (Map.Entry<String,String> dongEntry:dongTreeMap.entrySet()) {
            dong= new Dong(dongEntry.getKey(),dongEntry.getValue());
            dongArrayList.add(dong);
        }


        adapter = new AssignmentViewAdapter(DongPersonListViewActivity.this, R.layout.assignment_row, dongArrayList);
        //adapter = new SwListVIewAdapter(SuListViewActivity.this, R.layout.listview_row, items);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(mItemClickListener);

    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            dong = dongArrayList.get(position).Dong;
            constructionEmployee=dongArrayList.get(position).ConstructionEmployee;
            clickPosition=position;

            ShowProgressManagementDialog();



            //  view.setBackgroundColor(Color.parseColor("#DEB887"));

 /*           Intent intent = new Intent(getBaseContext(), ViewActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);*/

        }
    };

    /**
     *
     * @param progressEmployee
     * @return
     */
    private boolean CheckEmployee(String progressEmployee){
        if(!progressEmployee.equals(Users.UserName))
            return false;
        else
            return true;
    }

    private void ShowProgressManagementDialog(){

        //????????? ???????????? ?????? Dialog ?????? ??? ?????????
        LayoutInflater inflater=getLayoutInflater();

        //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
        //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
        final View dialogView= inflater.inflate(R.layout.dialog_progress_management, null);
        AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder ?????? ??????
        //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
        buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

        TextView textView=dialogView.findViewById(R.id.textView);//??????
        final TextView tvHouseHold=dialogView.findViewById(R.id.houseHold);//?????????
        final TextView tvSettingStart=dialogView.findViewById(R.id.settingStart);//???????????????
        final TextView tvSettingEnd=dialogView.findViewById(R.id.settingEnd);//???????????????
        final TextView tvSettingStartStair=dialogView.findViewById(R.id.settingStartStair);//??????????????????????????????
        final TextView tvSettingEndStair=dialogView.findViewById(R.id.settingEndStair);//??????????????????????????????
        final TextView tvHouseHoldFloor=dialogView.findViewById(R.id.houseHoldFloor);//??????????????????
        final TextView tvHighestFloor=dialogView.findViewById(R.id.highestFloor);//?????????
        final TextView tvMakeDate=dialogView.findViewById(R.id.makeDate);//?????????
        final TextView tvGangFormDate=dialogView.findViewById(R.id.gangFormDate);//???????????????
        final TextView tvOutDate=dialogView.findViewById(R.id.outDate);//???????????????
        final TextView tvSettingFloor=dialogView.findViewById(R.id.settingFloor);//?????????

        SetDialogText(tvHouseHold,tvSettingStart,tvSettingEnd,tvSettingStartStair,tvSettingEndStair,tvHouseHoldFloor,
                tvHighestFloor, tvMakeDate,tvGangFormDate,tvOutDate,tvSettingFloor);

        textView.setText(customerLocation+"-"+dong+"???");

        tvHouseHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_input_number, null);
                TextView tv= dialogView.findViewById(R.id.textView);
                final EditText no=dialogView.findViewById(R.id.edtNumber);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if(tvHouseHold.getText().toString().equals(getText(R.string.clear_string)))
                    no.setText("0");
                else
                    no.setText(tvHouseHold.getText().toString());
                tv.setText("????????? ??????");
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString=no.getText().toString();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvHouseHold.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvHouseHold.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvSettingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);
                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("??????????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvSettingStart.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSettingStart.setText(getString(R.string.clear_string));
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvSettingEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);

                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("??????????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvSettingEnd.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSettingEnd.setText(getString(R.string.clear_string));
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvSettingStartStair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);

                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("???????????????(??????,?????????) ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvSettingStartStair.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSettingStartStair.setText(getString(R.string.clear_string));
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvSettingEndStair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);

                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("???????????????(??????,?????????) ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvSettingEndStair.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSettingEndStair.setText(getString(R.string.clear_string));
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvHouseHoldFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_input_number, null);
                final EditText no=dialogView.findViewById(R.id.edtNumber);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if(tvHouseHoldFloor.getText().toString().equals(getText(R.string.clear_string)))
                    no.setText("0");
                else
                    no.setText(tvHouseHoldFloor.getText().toString());
                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("?????????????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString=no.getText().toString();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvHouseHoldFloor.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvHouseHoldFloor.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvHighestFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_input_number, null);
                final EditText no=dialogView.findViewById(R.id.edtNumber);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if(tvHighestFloor.getText().toString().equals(getText(R.string.clear_string)))
                    no.setText("0");
                else
                    no.setText(tvHighestFloor.getText().toString());
                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString=no.getText().toString();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvHighestFloor.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvHighestFloor.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvMakeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);

                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvMakeDate.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvMakeDate.setText(getString(R.string.clear_string));
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvGangFormDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);

                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("??????????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvGangFormDate.setText(inputString);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvGangFormDate.setText(getString(R.string.clear_string));
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
//????????? ???????????? ?????? Dialog ?????? ??? ?????????
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_year_month_day, null);
                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("??????????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month=datePicker.getMonth()+1;
                        String inputString=datePicker.getYear()+"-"+month+"-"+datePicker.getDayOfMonth();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvOutDate.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvOutDate.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        tvSettingFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckEmployee(constructionEmployee)){
                    Toast.makeText(DongPersonListViewActivity.this, "????????? ??????????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LayoutInflater inflater=getLayoutInflater();

                //res??????>>layout??????>>dialog_addmember.xml ???????????? ????????? ????????? View ?????? ??????
                //Dialog??? listener?????? ???????????? ?????? final??? ???????????? ??????
                final View dialogView= inflater.inflate(R.layout.dialog_input_number, null);
                final EditText no=dialogView.findViewById(R.id.edtNumber);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                if(tvSettingFloor.getText().toString().equals(getText(R.string.clear_string)))
                    no.setText("0");
                else
                    no.setText(tvSettingFloor.getText().toString());
                TextView tv= dialogView.findViewById(R.id.textView);
                tv.setText("????????? ??????");
                final DatePicker datePicker = dialogView.findViewById(R.id.pickerdate);
                AlertDialog.Builder buider= new AlertDialog.Builder(v.getContext()); //AlertDialog.Builder ?????? ??????
                //  buider.setIcon(android.R.drawable.ic_menu_add); //???????????? ????????? ?????????(????????? ????????? ??????)
                buider.setView(dialogView); //????????? inflater??? ?????? dialogView ?????? ?????? (Customize)

                buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString=no.getText().toString();
                        if(inputString.equals(""))
                            inputString=getString(R.string.clear_string);
                        tvSettingFloor.setText(inputString);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                buider.setNeutralButton("??????", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSettingFloor.setText(getString(R.string.clear_string));
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.cancel();
                    }
                });

                AlertDialog dialog=buider.create();
                //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
                dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
                //Dialog ?????????

                dialog.show();
            }
        });

        buider.setPositiveButton("??????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DongPersonListViewActivity.setProgressInfo( tvHouseHold, tvSettingStart, tvSettingEnd, tvSettingStartStair, tvSettingEndStair, tvHouseHoldFloor,
                        tvHighestFloor, tvMakeDate, tvGangFormDate, tvOutDate, tvSettingFloor).execute(getString(R.string.service_address)+"setProgressInfo");
            }
        });


        buider.setNegativeButton("??????", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

//????????? ????????? AlertDialog ?????? ??????

        AlertDialog dialog=buider.create();
        //Dialog??? ???????????? ???????????? ??? Dialog??? ????????? ??????
        dialog.setCanceledOnTouchOutside(false);//???????????? ????????? ??????
        //Dialog ?????????



        dialog.show();
    }

    private class setProgressInfo extends AsyncTask<String, Void, String> {//todo
        TextView tvHouseHold;
        TextView tvSettingStart;
        TextView tvSettingEnd;
        TextView tvSettingStartStair;
        TextView tvSettingEndStair;
        TextView tvHouseHoldFloor;
        TextView tvHighestFloor;
        TextView tvMakeDate;
        TextView tvGangFormDate;
        TextView tvOutDate;
        TextView tvSettingFloor;


        public setProgressInfo(TextView tvHouseHold,TextView tvSettingStart,TextView tvSettingEnd,TextView tvSettingStartStair,TextView tvSettingEndStair,TextView tvHouseHoldFloor,
                               TextView tvHighestFloor,TextView tvMakeDate,TextView tvGangFormDate,TextView tvOutDate,TextView tvSettingFloor){
            this.tvHouseHold=tvHouseHold;
            this.tvSettingStart=tvSettingStart;
            this.tvSettingEnd=tvSettingEnd;
            this.tvSettingStartStair=tvSettingStartStair;
            this.tvSettingEndStair=tvSettingEndStair;
            this.tvHouseHoldFloor=tvHouseHoldFloor;
            this.tvHighestFloor=tvHighestFloor;
            this.tvMakeDate=tvMakeDate;
            this.tvGangFormDate=tvGangFormDate;
            this.tvOutDate=tvOutDate;
            this.tvSettingFloor=tvSettingFloor;

        }


        @Override
        protected String doInBackground(String... urls) {

            return POST2(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {//????????? ?????? ???, ??????????????? ??????-> ???????????? ?????????????????????.

            String ResultCode="";
            String Message="";
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    ResultCode=child.getString("ResultCode");
                    Message=child.getString("Message");
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ResultCode.equals("true"))
                Toast.makeText(DongPersonListViewActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(DongPersonListViewActivity.this, Message, Toast.LENGTH_LONG).show();
            }

        }


        private String POST2(String url) {

            InputStream inputStream = null;
            String result = "";
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();


                jsonObject.put("ContractNo", contractNo);//????????????
                jsonObject.put("Dong", dong);//???

                jsonObject.put("HoCount",ChangeStringClear(tvHouseHold.getText().toString()));//?????????
                jsonObject.put("TopFloor",ChangeStringClear(tvHouseHoldFloor.getText().toString()));//????????? ?????????
                jsonObject.put("ProductDate",ChangeStringClear(tvMakeDate.getText().toString()));//?????????
                jsonObject.put("GangFormDate",ChangeStringClear(tvGangFormDate.getText().toString()));//???????????????
                jsonObject.put("OutDate",ChangeStringClear(tvOutDate.getText().toString()));//???????????????
                jsonObject.put("SettingStart",ChangeStringClear(tvSettingStart.getText().toString()));//???????????????
                jsonObject.put("SettingEnd",ChangeStringClear(tvSettingEnd.getText().toString()));//???????????????
                jsonObject.put("SettingStart2",ChangeStringClear(tvSettingStartStair.getText().toString()));//??????????????????????????????
                jsonObject.put("SettingEnd2",ChangeStringClear(tvSettingEndStair.getText().toString()));//??????????????????????????????
                jsonObject.put("SettingFloor",ChangeStringClear(tvSettingFloor.getText().toString()));//?????????
                jsonObject.put("TotalFloor",ChangeStringClear(tvHighestFloor.getText().toString()));//?????????



                json = jsonObject.toString();
                // ** Alternative way to convert Person object to JSON string using Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json, "UTF-8");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream

                HttpEntity entity = httpResponse.getEntity();
                inputStream = entity.getContent();
                //inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                //Log.d("InputStream", e.getLocalizedMessage());
            }
            // 11. return result
            //Log.i("result", result.toString());
            return result;
        }
    }

    /*
    ?????? ?????? ????????? ????????????->
    * Dialog??? Text??? Setting ??????.
    * */
    private void SetDialogText(TextView tvHouseHold,TextView tvSettingStart,TextView tvSettingEnd,TextView tvSettingStartStair,TextView tvSettingEndStair,TextView tvHouseHoldFloor,
                               TextView tvHighestFloor,TextView tvMakeDate,TextView tvGangFormDate,TextView tvOutDate,TextView tvSettingFloor){
        //data??? ????????????(contractNo, Dong)-> TextView??? ??????
        //?????? ?????? ?????? '????????? ??????'
        new DongPersonListViewActivity.getProgressInfo( tvHouseHold, tvSettingStart, tvSettingEnd, tvSettingStartStair, tvSettingEndStair, tvHouseHoldFloor,
                 tvHighestFloor, tvMakeDate, tvGangFormDate, tvOutDate, tvSettingFloor).execute(getString(R.string.service_address)+"getProgressInfo");
    }

    private class getProgressInfo extends AsyncTask<String, Void, String> {//todo
        TextView tvHouseHold;
        TextView tvSettingStart;
        TextView tvSettingEnd;
        TextView tvSettingStartStair;
        TextView tvSettingEndStair;
        TextView tvHouseHoldFloor;
        TextView tvHighestFloor;
        TextView tvMakeDate;
        TextView tvGangFormDate;
        TextView tvOutDate;
        TextView tvSettingFloor;


        public getProgressInfo(TextView tvHouseHold,TextView tvSettingStart,TextView tvSettingEnd,TextView tvSettingStartStair,TextView tvSettingEndStair,TextView tvHouseHoldFloor,
                               TextView tvHighestFloor,TextView tvMakeDate,TextView tvGangFormDate,TextView tvOutDate,TextView tvSettingFloor){
            this.tvHouseHold=tvHouseHold;
            this.tvSettingStart=tvSettingStart;
            this.tvSettingEnd=tvSettingEnd;
            this.tvSettingStartStair=tvSettingStartStair;
            this.tvSettingEndStair=tvSettingEndStair;
            this.tvHouseHoldFloor=tvHouseHoldFloor;
            this.tvHighestFloor=tvHighestFloor;
            this.tvMakeDate=tvMakeDate;
            this.tvGangFormDate=tvGangFormDate;
            this.tvOutDate=tvOutDate;
            this.tvSettingFloor=tvSettingFloor;

        }


        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject child = jsonArray.getJSONObject(i);
                    tvHouseHold.setText(child.getString("HoCount").equals("")?getString(R.string.clear_string):child.getString("HoCount"));
                    tvSettingStart.setText(child.getString("SettingStart").equals("")?getString(R.string.clear_string):child.getString("SettingStart"));
                    tvSettingEnd.setText(child.getString("SettingEnd").equals("")?getString(R.string.clear_string):child.getString("SettingEnd"));
                    tvSettingStartStair.setText(child.getString("SettingStart2").equals("")?getString(R.string.clear_string):child.getString("SettingStart2"));
                    tvSettingEndStair.setText(child.getString("SettingEnd2").equals("")?getString(R.string.clear_string):child.getString("SettingEnd2"));
                    tvHouseHoldFloor.setText(child.getString("TopFloor").equals("")?getString(R.string.clear_string):child.getString("TopFloor"));
                    tvHighestFloor.setText(child.getString("TotalFloor").equals("")?getString(R.string.clear_string):child.getString("TotalFloor"));
                    tvMakeDate.setText(child.getString("ProductDate").equals("")?getString(R.string.clear_string):child.getString("ProductDate"));
                    tvGangFormDate.setText(child.getString("GangFormDate").equals("")?getString(R.string.clear_string):child.getString("GangFormDate"));
                    tvOutDate.setText(child.getString("OutDate").equals("")?getString(R.string.clear_string):child.getString("OutDate"));
                    tvSettingFloor.setText(child.getString("SettingFloor").equals("")?getString(R.string.clear_string):child.getString("SettingFloor"));


                    //suWorders.add(MakeData(SupervisorWoNo, LocationName, WorkDate, StatusFlag, CustomerName, Supervisor, WorkTypeName));
                    //SupervisorWoNo = child.getString("SupervisorWoNo");
                }

              //  mHandler.sendEmptyMessage(0);
           //     Intent i = new Intent(getBaseContext(), SuListViewActivity.class);//todo
              //  i.putExtra("data", suWorders);
             //   i.putExtra("type", activityType);
      //          i.putExtra("url", restURL);
        //        startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * ?????? ????????????, ??????????????? ????????? ?????? ??????
    * */
    private boolean CheckDate(){
        return false;
    }

    /*
    * clear_string ??? "" ?????? ?????????.
    * */
    public String ChangeStringClear(String str){
        if(str.equals(getString(R.string.clear_string)))
            return "";
        else
            return str;
    }



    public String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();


            jsonObject.put("ContractNo", contractNo);//????????????
            jsonObject.put("Dong", dong);//???
            jsonObject.put("UserCode", Users.USER_ID);//????????????: ?????????????????????

            json = jsonObject.toString();
            // ** Alternative way to convert Person object to JSON string using Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json, "UTF-8");
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 9. receive response as inputStream

            HttpEntity entity = httpResponse.getEntity();
            inputStream = entity.getContent();
            //inputStream = httpResponse.getEntity().getContent();
            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            //Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        //Log.i("result", result.toString());
        return result;
    }





    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

       /* String message = "";
        String resultCode = "";
        try {
            //.i("JSON", result);
            JSONArray jsonArray = new JSONArray(result);
            resultCode = jsonArray.getJSONObject(0).getString("ResultCode");
            message = jsonArray.getJSONObject(0).getString("Message");

            if(resultCode.equals("true")){
                Toast.makeText(ProgressFloorActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

        }*/

        inputStream.close();
        return result;
    }
}

package com.kumkangkind.kumkangsm2.ProgressFloor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;

import com.kumkangkind.kumkangsm2.R;

import java.util.Calendar;

public class
YearMonthPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;
    String contractNo="";
    String dong="";
    String yearMonth="";
    String floor="";
    public Handler mHandler;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();

    /*
    * 생성자
    * */

    public YearMonthPickerDialog(){

    }


    @SuppressLint("ValidFragment")
    public YearMonthPickerDialog(Handler mHandler,String contractNo, String dong, String yearMonth, String floor){
        this.contractNo=contractNo;
        this.dong=dong;
        this.yearMonth=yearMonth;
        this.floor=floor;
        this.mHandler=mHandler;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_year_month, null);


        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.picker_number);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        final int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);


        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(200);

        if(floor.equals(""))
            numberPicker.setValue(0);
        else
            numberPicker.setValue(Integer.parseInt(floor)+1);

        builder.setView(dialog)
        // Add action buttons
        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Bundle data = new Bundle();

                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);

                String yearMonth="";
                if(monthPicker.getValue()<10){
                    yearMonth=Integer.toString(yearPicker.getValue())+"0"+Integer.toString(monthPicker.getValue());
                }
                else{
                    yearMonth=Integer.toString(yearPicker.getValue())+Integer.toString(monthPicker.getValue());
                }

                Message msg= mHandler.obtainMessage();
                data.putString("yearMonth",yearMonth);
                data.putString("floor",Integer.toString(numberPicker.getValue()));
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        })
        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bundle data = new Bundle();
                Message msg= mHandler.obtainMessage();
                data.putString("yearMonth","취소");
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        });
        return builder.create();
    }
}
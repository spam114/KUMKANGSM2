package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class AddItemDetail implements Serializable {

    public String AddItemNo = "";
    public String SeqNo = "";
    public String Ho = "";
    public String HoLocation = "";
    public String AddType="";
    public String Qty = "";
    public String Remark = "";
    public String Part="";


    public AddItemDetail() {
        super();
    }

    public AddItemDetail(String AddItemNo, String SeqNo, String Ho, String HoLocation, String AddType, String Qty, String Remark, String Part) {

        super();

        this.AddItemNo = AddItemNo;
        this.SeqNo = SeqNo;
        this.Ho = Ho;
        this.HoLocation = HoLocation;
        this.AddType = AddType;
        this.Qty = Qty;
        this.Remark = Remark;
        this.Part=Part;
    }
}
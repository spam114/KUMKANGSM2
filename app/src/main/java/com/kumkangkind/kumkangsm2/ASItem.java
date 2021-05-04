package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class ASItem implements Serializable {

    public String SupervisorCode="";//작성인 표시를 위한 추가
    public String SupervisorName="";

    public String SupervisorASNo = "";
    public String SupervisorWoNo = "";
    public String Dong = "";
    public String Ho = "";
    public String HoLocation = "";
    public String ItemType = "";
    public String Item = "";
    public String ItemSpecs = "";
    public String Quantity = "";
    public String Reason = "";
    public String AsType = "";
    public String Remark = "";
    public String Actions = "";
    public String ActionEmployee = "";


    public ASItem() {
        super();
    }

    public ASItem(String SupervisorCode,String SupervisorName,String SupervisorASNo, String SupervisorWoNo, String Dong, String Ho, String HoLocation, String ItemType, String Item,
                  String ItemSpecs, String Quantity, String Reason, String AsType, String Remark, String Actions, String ActionEmployee) {
        super();


        this.SupervisorCode = SupervisorCode;
        this.SupervisorName = SupervisorName;
        this.SupervisorASNo = SupervisorASNo;
        this.SupervisorWoNo = SupervisorWoNo;
        this.Dong = Dong;
        this.Ho = Ho;
        this.HoLocation = HoLocation;
        this.ItemType = ItemType;
        this.Item = Item;
        this.ItemSpecs = ItemSpecs;
        this.Quantity = Quantity;
        this.Reason = Reason;
        this.AsType = AsType;
        this.Remark = Remark;
        this.Actions = Actions;
        this.ActionEmployee = ActionEmployee;

    }
}

package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class AddItem implements Serializable {

    public String AddItemNo="";
    public String Dong = "";
    public String SupervisorName = "";
    public String ReceiptEmployeeCode="";
    public String ReceiptEmployeeName = "";
    public String RequestDate = "";
    public String HoppingDate = "";
    public String SupervisorWoNo="";


    public AddItem() {
        super();
    }

    public AddItem(String AddItemNo, String Dong, String SupervisorName, String ReceiptEmployeeCode,String ReceiptEmployeeName, String RequestDate, String HoppingDate, String SupervisorWoNo) {

        super();

        this.AddItemNo=AddItemNo;
        this.Dong = Dong;
        this.SupervisorName = SupervisorName;
        this.ReceiptEmployeeCode = ReceiptEmployeeCode;
        this.ReceiptEmployeeName = ReceiptEmployeeName;
        this.RequestDate = RequestDate;
        this.HoppingDate = HoppingDate;
        this.SupervisorWoNo=SupervisorWoNo;
    }
}

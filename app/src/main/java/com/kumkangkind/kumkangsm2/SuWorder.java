package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

/**
 * 리스트 뷰에 담을 내용이다.
 */
public class SuWorder implements Serializable {

    public String WorkNo = "";
    public String WorkDate = "";
    public String StartTime="";
    public String LocationName = "";
    public String Status = "";
    public String CustomerName = "";
    public String Supervisor = "";
    public String WorkTypeName = "";
    public String Dong = "";


    public SuWorder() {
        super();
    }

    public SuWorder(String workNo, String workDate, String startTime, String locationName, String status, String customerName, String supervisor, String workTypeName) {

        super();
        this.WorkNo = workNo;
        this.WorkDate = workDate;
        this.StartTime=startTime;
        this.LocationName = locationName;
        this.Status = status;
        this.CustomerName = customerName;
        this.Supervisor = supervisor;
        this.WorkTypeName = workTypeName;
    }
}

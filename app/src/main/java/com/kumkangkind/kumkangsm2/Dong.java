package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class Dong implements Serializable {
    public String Dong="";
    public String ProgressYearMonth="";//진행년월
    public String ExProgressYearMonth="";//전 달 진행년월
    public String ProgressFloor="";//진행층수
    public String ExProgressFloor="";//전 달 진행층수
    public String ConstructionEmployee="";//시공담당자(슈퍼바이저)

    public Dong() {
        super();

    }

    public Dong(String Dong, String ProgressYearMonth, String ProgressFloor, String ConstructionEmployee) {
        super();

        this.Dong=Dong;
        this.ProgressYearMonth=ProgressYearMonth;
        this.ProgressFloor=ProgressFloor;
        this.ConstructionEmployee=ConstructionEmployee;
    }

    public Dong(String Dong, String ConstructionEmployee) {
        super();

        this.Dong=Dong;
        this.ConstructionEmployee=ConstructionEmployee;
    }

}

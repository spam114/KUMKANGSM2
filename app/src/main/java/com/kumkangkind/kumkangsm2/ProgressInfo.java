package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class ProgressInfo implements Serializable {

    int No;//순번

    /// <summary>
    /// 계약번호
    /// </summary>
    public String ContractNo = "";

    /// <summary>
    /// 동
    /// </summary>
    public String Dong = "";


    /// <summary>
    /// 현재층수(진행층)
    /// </summary>
    public String ProgressFloor = "";


    /// <summary>
    /// 갱폼시공일
    /// </summary>
    public String GangFormDate = "";


    /// <summary>
    /// 셋팅시작일
    /// </summary>
    public String SettingStart = "";

    /// <summary>
    /// 셋팅종료일
    /// </summary>
    public String SettingEnd = "";

    /// <summary>
    /// 셋팅시작일(계단피로티)
    /// </summary>
    public String SettingStart2 = "";

    /// <summary>
    /// 셋팅종료일(계단피로티)
    /// </summary>
    public String SettingEnd2 = "";

    /// <summary>
    /// WALL공정
    /// </summary>
    public String WallProcess = "";

    /// <summary>
    /// SLAB공정
    /// </summary>
    public String SLProcess = "";

    /// <summary>
    /// STEEL공정
    /// </summary>
    public String SteelProcess = "";


    public ProgressInfo() {
        super();
    }

    public ProgressInfo(int No,String ContractNo, String Dong, String ProgressFloor, String GangFormDate, String SettingStart, String SettingEnd, String SettingStart2, String SettingEnd2,
                        String WallProcess, String SLProcess, String SteelProcess) {

        super();
        this.No=No;
        this.ContractNo = ContractNo;

        this.Dong = Dong;

        this.ProgressFloor = ProgressFloor;

        this.GangFormDate = GangFormDate;

        this.SettingStart = SettingStart;

        this.SettingEnd = SettingEnd;

        this.SettingStart2 = SettingStart2;

        this.SettingEnd2 = SettingEnd2;

        this.WallProcess = WallProcess;

        this.SLProcess = SLProcess;

        this.SteelProcess = SteelProcess;
    }
}

package com.kumkangkind.kumkangsm2;

import java.util.ArrayList;

/**
 * 해당 클래스는 현재 사용자의 정보를 가진다.
 */
public  class  Users {

    public static String UserName = "";
    public static String USER_ID = "";
    public static String EmployeeNo="";
    public static String Phone = "010-2705-8632";
    public static String LeaderType = "Y";
    public static String[] supervisorList;
    public static String[] CardList;
    public static String appDownloadUrl = "";
    public static int CurrentVersion;
    public static String DeptName = "";//부서이름
    public static int BusinessClassCode;//사업장

    //LoginDate는 서버시간
    //AppCode는 strings에서
    public static String AndroidID="";
    public static String Model = "";
    public static String PhoneNumber = "";
    public static String DeviceName = "";
    public static String DeviceOS = "";
    //Appversion은 build에서
    public static String Remark = "";

}

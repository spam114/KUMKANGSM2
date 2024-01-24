package com.kumkangkind.kumkangsm2.CustomerLocation;

import java.io.Serializable;

public class Location implements Serializable  {

    public String LocationNo = "";
    public String LocationName = "";
    public String LocationName2="";
    public String ContractNo="";


    public Location() {
        super();
    }

    public Location(String LocationNo, String LocationName, String ContractNo, String LocationName2) {

        super();
        this.LocationNo=LocationNo;
        this.LocationName=LocationName;
        this.ContractNo=ContractNo;
        this.LocationName2 = LocationName2;
    }

    public Location(String LocationNo, String LocationName, String LocationName2) {

        super();
        this.LocationNo=LocationNo;
        this.LocationName=LocationName;
        this.LocationName2 = LocationName2;
    }
}

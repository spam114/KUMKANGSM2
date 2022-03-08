package com.kumkangkind.kumkangsm2.CustomerLocation;

import java.io.Serializable;

public class Location implements Serializable  {

    public String LocationNo = "";
    public String LocationName = "";
    public String ContractNo="";


    public Location() {
        super();
    }

    public Location(String LocationNo, String LocationName, String ContractNo) {

        super();
        this.LocationNo=LocationNo;
        this.LocationName=LocationName;
        this.ContractNo=ContractNo;
    }

    public Location(String LocationNo, String LocationName) {

        super();
        this.LocationNo=LocationNo;
        this.LocationName=LocationName;
    }
}

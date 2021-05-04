package com.kumkangkind.kumkangsm2.sale;

import java.io.Serializable;

/**
 * 리스트 뷰에 담을 내용이다.
 */
public class Customers implements Serializable {

    public String CustomerName = "";
    public String LocationName = "";
    public String ContractMasterNo = "";

    public Customers() {
        super();
    }

    public Customers(String customerName, String locationName, String contractMasterNo) {

        super();

        CustomerName = customerName;
        LocationName = locationName;
        ContractMasterNo = contractMasterNo;
    }
}

package com.kumkangkind.kumkangsm2.CustomerLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Customer implements Serializable, Comparable<Customer> {
    public String CustomerCode = "";
    public String CustomerName = "";
    public HashMap<String, Location> locationHashMap;
    public List<String> confirmList;


    public Customer() {
        super();
    }

    public Customer(String CustomerCode, String CustomerName) {
        super();
        this.locationHashMap= new HashMap<>();
        this.CustomerCode=CustomerCode;
        this.CustomerName = CustomerName;
        confirmList=new ArrayList<>();
    }


    public void addData(String LocationNo,String LocationName,String ContractNo)
    {
        if(confirmList.contains(LocationName)){//현장이 같을시-> 계약번호를 붙여줌
            LocationName=LocationName+"("+ContractNo+")";
        }
        confirmList.add(LocationName);
        String key = ContractNo;
        Location location=null;
        location = new Location(LocationNo,LocationName,ContractNo);
        locationHashMap.put(key,location);


    }

    /*
    * Value로 정렬하기 위한 객체 크기 비교
    * */
    @Override
    public int compareTo(Customer customer) {
        // 같으면 0,
        // 현재 객체가 customer 보다 작으면 -1
        // 현재 객체가 customer 보다 크면 1

        return this. CustomerName.replace("(주)","").replace("(유)","").compareTo(customer. CustomerName.replace("(주)","").replace("(유)",""));
    }
}

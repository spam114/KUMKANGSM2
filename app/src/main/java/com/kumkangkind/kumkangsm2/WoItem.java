package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

/**
 * 리스트 뷰에 담을 내용이다.
 */
public class WoItem implements Serializable {

    public String WoNo = "";
    public String SeqNo = "";
    public String ItemName = "";
    public String PersonCount = "";
    public String Quantity = "";

    public WoItem() {
        super();
    }

    public WoItem(String woNo, String seqNo, String itemName, String personCount, String quantity) {
        super();

        this.WoNo = woNo;
        this.SeqNo = seqNo;
        this.ItemName = itemName;
        this.PersonCount = personCount;
        this.Quantity = quantity;
    }

}

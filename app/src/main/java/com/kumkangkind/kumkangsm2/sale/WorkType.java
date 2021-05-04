package com.kumkangkind.kumkangsm2.sale;

import java.io.Serializable;

/**
 * 리스트 뷰에 담을 내용이다.
 */
public class WorkType implements Serializable {

    public String WorkTypeCode = "";
    public String WorkTypeName = "";
    public String SeqNo = "";
    public int No;//저장된 데이터를 매칭할때 몇번째인지 알기위한 변수

    public WorkType() {
        super();
    }

    public WorkType(String workTypeCode, String workTypeName, String seqNo, int No) {

        super();
        this.WorkTypeCode = workTypeCode;
        this.WorkTypeName = workTypeName;
        this.SeqNo = seqNo;
        this.No = No;
    }
}
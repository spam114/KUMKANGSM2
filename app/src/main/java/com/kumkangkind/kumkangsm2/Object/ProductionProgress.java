package com.kumkangkind.kumkangsm2.Object;

import java.io.Serializable;

public class ProductionProgress implements Serializable {

    public String PartName="";
    public String PartSpec="";
    public String Mspec="";

    public String OrderQty="";
    public String AllocQty="";
    public String ProcessQty="";
    public String InstructionQty="";
    public String ProductionQty="";
    public String PackingQty="";
    public String OutQty="";

    public String SeqNo="";

    public ProductionProgress() {
        super();
    }
}

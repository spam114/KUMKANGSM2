package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

public class ASItemStandard implements Serializable {

    public String StandardNo = "";
    public String Name = "";
    public String Parent = "";


    public ASItemStandard() {
        super();
    }

    public ASItemStandard(String StandardNo, String Name, String Parent) {
        super();

        this.StandardNo = StandardNo;
        this.Name = Name;
        this.Parent = Parent;

    }
}

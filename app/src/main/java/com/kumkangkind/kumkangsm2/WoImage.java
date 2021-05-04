package com.kumkangkind.kumkangsm2;

import java.io.Serializable;

/**
 * 리스트 뷰에 담을 내용이다.
 */
public class WoImage implements Serializable {

    public String WoNo = "";
    public String SeqNo = "";
    public String ImageFile = "";
    public String ImageName = "";
    public String SmallImageFile = "";

    public WoImage()
    {
        super();
    }

    public WoImage(String woNo, String seqNo, String imageName, String imageFile, String smallImageFile)
    {
        super();

        this.WoNo = woNo;
        this.SeqNo = seqNo;
        this.ImageName = imageName;
        this.ImageFile = imageFile;
        this.SmallImageFile = smallImageFile;
    }

}

package com.kumkangkind.kumkangsm2;

import android.os.Parcel;
import android.os.Parcelable;

public class ComplaintImage implements Parcelable {

    public String ItemNo = "";
    public String No = "";
    public String Type = "";
    public String ImageName = "";
    public String ImageFile = "";
    public String SmallImageFile = "";

    public ComplaintImage()
    {
        super();
    }

    public ComplaintImage(String ItemNo, String No, String Type, String ImageName, String ImageFile, String SmallImageFile)
    {
        this.ItemNo = ItemNo;
        this.No = No;
        this.Type = Type;
        this.ImageName = ImageName;
        this.ImageFile = ImageFile;
        this.SmallImageFile=SmallImageFile;
    }

    protected ComplaintImage(Parcel in) {
        ItemNo = in.readString();
        No = in.readString();
        Type = in.readString();
        ImageName = in.readString();
        ImageFile = in.readString();
        SmallImageFile = in.readString();
    }

    public static final Creator<ComplaintImage> CREATOR = new Creator<ComplaintImage>() {
        @Override
        public ComplaintImage createFromParcel(Parcel in) {
            return new ComplaintImage(in);
        }

        @Override
        public ComplaintImage[] newArray(int size) {
            return new ComplaintImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemNo);
        dest.writeString(No);
        dest.writeString(Type);
        dest.writeString(ImageName);
        dest.writeString(ImageFile);
        dest.writeString(SmallImageFile);
    }
}

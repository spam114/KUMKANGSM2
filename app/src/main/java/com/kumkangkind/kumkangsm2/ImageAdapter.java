package com.kumkangkind.kumkangsm2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 리스트 뷰를 구현하기 위한 어댑터 클래스다.
 */
public class ImageAdapter extends ArrayAdapter<WoImage> {

    Context context;
    int layoutRsourceId;
    //WoImage[] data;
    ArrayList data;
    Handler mHandler;
    ProgressDialog mProgressDialog;
    WoImage currentImage;
    public int removePosition;

    public ImageAdapter(Context context, int layoutResourceID, ArrayList data) {

        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_imagerow, null);
        }

        WoImage image = (WoImage) data.get(position);
        if (image != null) {

            TextView textViewSeqNo = (TextView) row.findViewById(R.id.textViewImageSeqNo);
            TextView textViewImageName = (TextView) row.findViewById(R.id.textViewImageName);
            Button deleteButton = (Button) row.findViewById(R.id.buttonDelete);
            ImageView imageView = (ImageView) row.findViewById(R.id.imageViewSmall);

            deleteButton.setFocusable(false);

            if (textViewImageName != null)
                textViewImageName.setText(((WoImage) (data.get(position))).ImageName);
            if (textViewSeqNo != null)
                textViewSeqNo.setText(String.valueOf(position + 1));

            deleteButton.setTag(position);

            try {
                byte[] array5 = Base64.decode(((WoImage) data.get(position)).SmallImageFile, Base64.DEFAULT);
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(array5, 0, array5.length));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return row;
    }

    /**
     * 그림을 보인다.
     *
     * @param imageString
     */
    private void ViewData(String imageString) {

        String photostring = imageString;

        try {


        } catch (Exception ex) {

            Log.e("에러", "비트맵 에러 " + ex.getMessage().toString());
        }
    }

}


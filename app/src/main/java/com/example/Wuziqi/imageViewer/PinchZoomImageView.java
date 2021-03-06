package com.example.Wuziqi.imageViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PinchZoomImageView extends ImageView{

    private Bitmap mBitmap;
    private int mImageWidth;
    private int mImageHeight;

    public PinchZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawBitmap(mBitmap, 0,0, null);
        canvas.restore();
    }

    public void setImageUir(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            float aspecRatio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            mImageWidth = displayMetrics.widthPixels;
            mImageHeight = Math.round(mImageWidth * aspecRatio);
            mBitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false);
            invalidate();
            requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

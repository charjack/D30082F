package com.wedesign.mediaplayer.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/12/13.
 */
public class MyImageView extends ImageView {

    private OnMeasureListener onMeasureListener;
    public MyImageView(Context context){super(context);}
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnMeasureListener(OnMeasureListener onMeasureListener){
        this.onMeasureListener = onMeasureListener;
    }

    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(onMeasureListener!=null) {
            onMeasureListener.onMeasureSize(getMeasuredWidth(),getMeasuredHeight());
        }
    }

    public interface OnMeasureListener{
        public void onMeasureSize(int width, int height);
    }
}

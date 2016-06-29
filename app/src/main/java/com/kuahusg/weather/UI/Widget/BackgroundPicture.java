package com.kuahusg.weather.UI.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.util.LogUtil;

import java.util.Random;

/**
 * Created by kuahusg on 16-6-29.
 * com.kuahusg.weather.UI
 */
public class BackgroundPicture extends LinearLayout implements View.OnClickListener {
    LinearLayout container;
    Context mContext;
    ImageView imageView;
    OnBackgroundPicClickListener listener;

    public BackgroundPicture(Context context) {
        super(context);
        init(context);
    }

    public BackgroundPicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BackgroundPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);


    }

    private void init(Context context) {
        mContext = context;
        container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.background_picture, null);
        imageView = (ImageView) container.findViewById(R.id.back_pic);
        addView(container);



        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        LogUtil.v(this.toString(),"onClick()");
        switch (v.getId()) {
            case R.id.back_pic:
                if (listener != null) {
                    listener.OnClickClickPic(imageView);
                }
                break;
        }
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (listener != null) {
            listener.initBackgroundPic(imageView);
        }
    }



    public void addOnBackgroundPicClickListener(OnBackgroundPicClickListener listener) {
        this.listener = listener;
    }

    public interface OnBackgroundPicClickListener{
        void OnClickClickPic(ImageView pic);

        void initBackgroundPic(ImageView pic);
    }


}

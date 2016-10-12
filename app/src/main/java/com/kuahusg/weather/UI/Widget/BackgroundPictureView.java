package com.kuahusg.weather.UI.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kuahusg.weather.R;
import com.kuahusg.weather.util.LogUtil;

/**
 * Created by kuahusg on 16-6-29.
 * com.kuahusg.weather.UI
 */
public class BackgroundPictureView extends LinearLayout implements View.OnClickListener {
    // TODO: 16-10-12 这个View应该添加更多其他控件,并设计成可复用
    LinearLayout container;
    Context mContext;
    ImageView imageView;
    OnBackgroundPicClickListener listener;

    public BackgroundPictureView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BackgroundPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);


    }

    private void init(Context context) {
        mContext = context;
        container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.include_background_picture, null);
        // TODO: 16-10-12 null?
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

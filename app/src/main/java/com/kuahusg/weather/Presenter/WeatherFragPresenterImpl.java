package com.kuahusg.weather.Presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherMainFragPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherMainView;
import com.kuahusg.weather.util.DateUtil;

import java.util.Random;

/**
 * Created by kuahusg on 16-9-29.
 */

public class WeatherFragPresenterImpl extends BasePresenter implements IWeatherMainFragPresenter {
    private IWeatherMainView mView;

    public WeatherFragPresenterImpl(IBaseView view) {
        super(view);
        mView = (IWeatherMainView) view;


    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void onClickBackgroundPic(ImageView imageViewToLoadImage) {
        int i = getRandomNum(7, 17);
        loadPicture(DateUtil.getDate("yy-MM-dd", i), imageViewToLoadImage);
    }

    @Override
    public void initBackgroundPic(ImageView imageViewToInit) {
        loadPicture(DateUtil.getDate("yy-MM-dd"), imageViewToInit);
    }

    @Override
    public void checkForecastSource(String link, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    private int getRandomNum(int from, int to) {
        Random random = new Random();
        return (random.nextInt(to - from) + from);
    }

    private void loadPicture(String s, ImageView imageView) {
        Glide.with(imageView.getContext()).load("http://s.tu.ihuan.me/bgc/" + s + ".png")
                .placeholder(R.drawable.back)
                .into(imageView);
    }


}

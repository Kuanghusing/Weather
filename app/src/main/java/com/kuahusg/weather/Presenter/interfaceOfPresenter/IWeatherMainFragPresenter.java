package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import android.content.Context;
import android.widget.ImageView;

import com.kuahusg.weather.Presenter.base.IBasePresenter;

/**
 * Created by kuahusg on 16-9-29.
 */

public interface IWeatherMainFragPresenter extends IBasePresenter {
    @Override
    void init();

    @Override
    void start();

    @Override
    void onDestroy();

    void onClickBackgroundPic(ImageView imageViewToLoadImage);

    void checkForecastSource(String link, Context context);

    void initBackgroundPic(ImageView imageViewToInit);


}

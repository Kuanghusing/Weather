package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import com.kuahusg.weather.Presenter.base.IBasePresenter;

/**
 * Created by kuahusg on 16-9-29.
 */

public interface IFutureWeatherFragPresenter extends IBasePresenter {
    @Override
    void init();

    @Override
    void start();

    @Override
    void onDestroy();
}

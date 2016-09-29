package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.model.City;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface IWeatherViewPresenter extends IBasePresenter {


    @Override
    void init();

    @Override
    void start();

    @Override
    void onDestroy();

    void startAutoUpdateService();

    void onClickFab();

    void refreshWeather();

    void refreshWeather(City city);

    void selectCitySuccess(City selectedCity);




}

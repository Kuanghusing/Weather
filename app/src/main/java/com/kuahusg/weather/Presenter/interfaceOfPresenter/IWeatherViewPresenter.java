package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

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

    void requireAnotherCity();

    ForecastInfo requireInfo(String woeid);

    List<Forecast> requireForecase(String woeid);


}

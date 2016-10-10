package com.kuahusg.weather.UI.interfaceOfView;

import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.model.Forecast;

import java.util.List;

/**
 * Created by kuahusg on 16-9-29.
 */

public interface IFutureWeatherFragView extends IBaseView {
    @Override
    void start();

    @Override
    void error(String message);

    @Override
    void finish();

    @Override
    void init();

    void showForecast(List<Forecast> forecastList);

    void scrollToTop();
}

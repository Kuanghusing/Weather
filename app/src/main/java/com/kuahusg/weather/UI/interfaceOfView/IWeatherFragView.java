package com.kuahusg.weather.UI.interfaceOfView;

import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-29.
 */

public interface IWeatherFragView extends IBaseView {

    void showWeather(List<Forecast> forecastList);

    void showForecastInfo(ForecastInfo info);

    void scrollToTop();

}

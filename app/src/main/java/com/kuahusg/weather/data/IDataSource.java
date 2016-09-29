package com.kuahusg.weather.data;

import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface IDataSource {
    void queryWeather(String woeid, RequestWeatherCallback callback);


    void saveWeather(List<Forecast> forecastList, ForecastInfo info);

    void loadAllCity(RequestCityCallback cityCallback);

}

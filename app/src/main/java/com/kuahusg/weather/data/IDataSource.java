package com.kuahusg.weather.data;

import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestCityResultCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface IDataSource {
    void queryWeather(String woeid, RequestWeatherCallback callback);

    void queryWeather(RequestWeatherCallback callback);


    void saveWeather(List<Forecast> forecastList, ForecastInfo info);

    void loadAllCity(RequestCityCallback cityCallback);

    void saveAllCity(List<String> cityList);

    void queryCity(RequestCityResultCallback callback, String cityName);

}

package com.kuahusg.weather.data.local;

import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */


public class LocalForecastDataSource implements IDataSource {

    LinkedList<Forecast> forecastLinkedList = new LinkedList<>();    //本地缓存

    @Override
    public void queryWeather(String woeid, RequestWeatherCallback callback) {

    }

    @Override
    public void saveWeather(List<Forecast> forecastList, ForecastInfo info) {

    }

    public void queryWeather(RequestWeatherCallback callback) {

    }


}

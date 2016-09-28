package com.kuahusg.weather.data.callback;

import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface RequestWeatherCallback {
    void success(List<Forecast> forecasts, ForecastInfo forecastInfo);

    void error(String message);
}

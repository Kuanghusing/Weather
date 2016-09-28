package com.kuahusg.weather.data;

import com.kuahusg.weather.App;
import com.kuahusg.weather.R;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.data.local.LocalForecastDataSource;
import com.kuahusg.weather.data.remote.RemoteForecastDataSource;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.NetwordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public class WeatherDataSource implements IDataSource {
    private RemoteForecastDataSource remote;
    private LocalForecastDataSource local;

    //cache
    private List<Forecast> forecasts = new ArrayList<>();
    private ForecastInfo forecastInfo = null;


    @Override
    public void queryWeather(final String woeid, final RequestWeatherCallback callback) {
        //TODO it means require network to query ??
        clearCache();
        if (NetwordUtil.hasNetwork(App.getContext()))

            remote.queryWeather(woeid, new RequestWeatherCallback() {
                @Override
                public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
                    saveWeather(forecasts, forecastInfo);
                    local.saveWeather(forecasts, forecastInfo);
                    callback.success(forecasts, forecastInfo);
                }

                @Override
                public void error(String message) {
                    callback.error(message);

                }
            });
        else
            callback.error(App.getContext().getString(R.string.no_network));


    }

    @Override
    public void saveWeather(List<Forecast> forecastList, ForecastInfo info) {
        clearCache();
        this.forecastInfo = info;
        this.forecasts = forecastList;

    }

    public void queryWeather(final RequestWeatherCallback callback) {
        //from cache
        if (this.forecastInfo != null && this.forecasts != null && this.forecasts.size() > 0) {
            callback.success(this.forecasts, this.forecastInfo);
            return;
        }

        //else from local database

        else {
            local.queryWeather(null, new RequestWeatherCallback() {
                @Override
                public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
                    saveWeather(forecasts, forecastInfo);
                    callback.success(forecasts, forecastInfo);
                }

                @Override
                public void error(String message) {
//                    callback.error(message);
                    //local db error then get it from remote data source
                    remote.queryWeather(null, callback);
                    // TODO: 16-9-28 get woeid
                }
            });
        }


    }

    private void clearCache() {
        this.forecastInfo = null;
        this.forecasts = null;
    }

}

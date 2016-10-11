package com.kuahusg.weather.data;

import com.kuahusg.weather.App;
import com.kuahusg.weather.R;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestCityResultCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;
import com.kuahusg.weather.util.NetwordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public class WeatherDataSource implements IDataSource {
    private IDataSource remote;
    private IDataSource local;

    //cache
    private List<Forecast> forecastListCache = new ArrayList<>();
    private ForecastInfo forecastInfoCache = null;
    private List<String> cityListCache = new ArrayList<>();


    public WeatherDataSource(IDataSource remoteDatasource, IDataSource localDatasource) {
        this.remote = remoteDatasource;
        this.local = localDatasource;
    }
    @Override
    public void queryWeather(final String woeid, final RequestWeatherCallback callback) {
        clearWeatherCache();
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
        clearWeatherCache();
        this.forecastInfoCache = info;
        this.forecastListCache = forecastList;

    }

    @Override
    public void loadAllCity(final RequestCityCallback cityCallback) {
        if (this.cityListCache != null && this.cityListCache.size() > 0) {
            cityCallback.success(this.cityListCache);

        } else {
            clearCityCache();
            local.loadAllCity(new RequestCityCallback() {
                @Override
                public void success(List<String> cityList) {
                    saveAllCity(cityList);
//                    local.saveAllCity(cityList);
                    cityCallback.success(cityList);
                }

                @Override
                public void error() {
                    remote.loadAllCity(new RequestCityCallback() {
                        @Override
                        public void success(List<String> cityList) {
                            cityCallback.success(cityList);
                            local.saveAllCity(cityList);
                        }

                        @Override
                        public void error() {
                            cityCallback.error();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void saveAllCity(List<String> cityList) {
        clearCityCache();
        this.cityListCache = cityList;
    }

    @Override
    public void queryWeather(final RequestWeatherCallback callback) {
        //from cache
        if (this.forecastInfoCache != null && this.forecastListCache != null && this.forecastListCache.size() > 0) {
            callback.success(this.forecastListCache, this.forecastInfoCache);
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
                }
            });
        }


    }

    @Override
    public void queryCity(RequestCityResultCallback callback, String cityName) {
        remote.queryCity(callback, cityName);
    }

    private void clearWeatherCache() {
        this.forecastInfoCache = null;
        this.forecastListCache = null;
    }

    private void clearCityCache() {
        this.cityListCache = null;
    }

}

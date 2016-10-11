package com.kuahusg.weather.data.local;

import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestCityResultCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

import static com.kuahusg.weather.util.PreferenceUtil.PREF_WOEID;

/**
 * Created by kuahusg on 16-9-28.
 */


public class LocalForecastDataSource implements IDataSource {


    @Override
    public void queryWeather(String woeid, RequestWeatherCallback callback) {

        if (woeid == null)
            woeid = getWoeid();

        assert woeid != null;
        WeatherDB.loadForecastAndInfo(woeid, callback);
    }

    @Override
    public void saveWeather(List<Forecast> forecastList, ForecastInfo info) {
        WeatherDB.saveForecastAndInfo(forecastList, info);
    }

    @Override
    public void loadAllCity(RequestCityCallback cityCallback) {
        WeatherDB.loadAllMainCity(cityCallback);
    }

    @Override
    public void saveAllCity(List<String> cityList) {
        WeatherDB.saveAllMainCity(cityList);
    }

    @Override
    public void queryWeather(RequestWeatherCallback callback) {
        // TODO: 16-10-7 ??
    }

    @Override
    public void queryCity(RequestCityResultCallback callback, String cityName) {
        //I don't care
    }

    private String getWoeid() {
        return PreferenceUtil.getInstance().getSharedPreferences().getString(PREF_WOEID, null);

    }


}

package com.kuahusg.weather.data.remote;

import android.util.Log;

import com.kuahusg.weather.App;
import com.kuahusg.weather.R;
import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.Data.WeatherResult;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kuahusg on 16-9-28.
 */

public class RemoteForecastDataSource implements IDataSource {
    private String queryString = "select * from weather.forecast where woeid = %s and u=\"c\"";
    private String formatMethod = "json";

    @Override
    public void saveWeather(List<Forecast> forecastList, ForecastInfo info) {
        //do nothing
    }

    @Override
    public void loadAllCity(RequestCityCallback cityCallback) {

    }

    @Override
    public void queryWeather(final String woeid, final RequestWeatherCallback callback) {

        Call<WeatherResult> call = RetrofitManager.getWeatherService()
                .queryWeather(String.format(queryString, woeid), formatMethod);

        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                WeatherResult weatherResult = response.body();
                Forecast[] forecasts = null;
                ForecastInfo forecastInfo = null;
                try {
                    forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();
                    forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();
                    String link = weatherResult.getQuery().getResults().getChannel().getLink();
                    String lastBuildDate = weatherResult.getQuery().getResults().getChannel()
                            .getLastBuildDate();
                    String wind_direction = weatherResult.getQuery().getResults().getChannel()
                            .getWind().getDirection();
                    String wind_speed = weatherResult.getQuery().getResults().getChannel().getWind()
                            .getSpeed();
                    String date = weatherResult.getQuery().getResults().getChannel().getItem()
                            .getCondition().getDate();
                    String temp = weatherResult.getQuery().getResults().getChannel().getItem()
                            .getCondition().getTemp();
                    String text = weatherResult.getQuery().getResults().getChannel().getItem()
                            .getCondition().getText();
                    String sunrise = weatherResult.getQuery().getResults().getChannel().getAstronomy()
                            .getSunrise();
                    String sunset = weatherResult.getQuery().getResults().getChannel().getAstronomy()
                            .getSunset();

                    forecastInfo = new ForecastInfo(link, lastBuildDate, wind_direction, wind_speed, date,
                            temp, text, woeid, sunrise, sunset);


                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(call, e);
                    // TODO: 16-9-28 ??
                }
                assert forecasts != null && forecastInfo != null;
                callback.success(Arrays.asList(forecasts), forecastInfo);
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Log.e(getClass().getSimpleName(), t.getMessage());
                callback.error(App.getContext().getString(R.string.error_network));
            }
        });


    }
}

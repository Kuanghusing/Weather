package com.kuahusg.weather.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kuahusg on 16-9-28.
 */

public class RetrofitManager {
    private static final String BASE_URL = "https://query.yahooapis.com";

    private WeatherService service;

    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WeatherService.class);
    }

    public static WeatherService getWeatherService() {
        return InstanceHolder.INSTANCE.service;
    }

    private static final class InstanceHolder {
        static final RetrofitManager INSTANCE = new RetrofitManager();
    }
}

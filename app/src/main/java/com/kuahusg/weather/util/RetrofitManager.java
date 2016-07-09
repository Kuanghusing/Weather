package com.kuahusg.weather.util;

import com.kuahusg.weather.model.Data.WeatherResult;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by kuahusg on 16-7-9.
 */
public class RetrofitManager {
    private Retrofit retrofit;
    public static final String URL = "https://query.yahooapis.com/v1/public/";
    WeatherService weatherService;


    private RetrofitManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherService.class);
    }

    public static RetrofitManager newInstanst() {
        return new RetrofitManager();
    }


    public Observable<WeatherResult> getForecast(String select, String format) {
        return weatherService.getForecast(select, format)
                .subscribeOn(Schedulers.newThread());
    }
}

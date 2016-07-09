package com.kuahusg.weather.util;

import com.kuahusg.weather.model.Data.WeatherResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Path;
import rx.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kuahusg on 16-7-9.
 */
public interface WeatherService {
    //    @GET("yql?q=select * from weather.forecast where woeid = {woeid} and u = \"c\"&format=json")
    @GET("yql")
    Observable<WeatherResult> getForecast(@Query("q") String select,
                                          @Query("format") String format);

}

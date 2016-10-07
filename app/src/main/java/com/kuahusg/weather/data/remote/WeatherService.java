package com.kuahusg.weather.data.remote;

import com.kuahusg.weather.model.Data.WeatherResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface WeatherService {
    @GET("/v1/public/yql")
    Call<WeatherResult> queryWeather(@Query("q") String queryString, @Query("format") String format);

    @GET("Kuanghusing/City_list/master/city-list")
    Call<List<String>> queryAllMainCity();


}

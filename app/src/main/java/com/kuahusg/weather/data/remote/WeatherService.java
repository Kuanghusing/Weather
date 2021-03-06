package com.kuahusg.weather.data.remote;

import com.kuahusg.weather.model.bean.AllCityResult;
import com.kuahusg.weather.model.bean.CitySearchResult;
import com.kuahusg.weather.model.bean.WeatherResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface WeatherService {
    @GET("/v1/public/yql")
    Call<WeatherResult> queryWeather(@Query("q") String queryString, @Query("format") String format);

    @GET()
    Call<List<AllCityResult>> queryAllMainCity(@Url String url);

    @GET("/v1/public/yql")
    Call<CitySearchResult> queryCity(@Query("q") String queryString, @Query("format") String format);


}

package com.kuahusg.weather.data.remote;

import android.util.Log;
import android.widget.Toast;

import com.kuahusg.weather.App;
import com.kuahusg.weather.R;
import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestCityResultCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.bean.AllCityResult;
import com.kuahusg.weather.model.bean.City;
import com.kuahusg.weather.model.bean.CitySearchResult;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;
import com.kuahusg.weather.model.bean.WeatherResult;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kuahusg on 16-9-28.
 */

public class RemoteForecastDataSource implements IDataSource {
    private String queryWeatherString = "select * from weather.forecast where woeid = %s and u=\"c\"";
    private String formatMethod = "json";
    private String getAllMainCityUrl = "https://raw.githubusercontent.com/Kuanghusing/City_list/master/city-list";

    private String queryCityString = "select woeid, name, country.content," +
            "admin1.content, admin2.content, admin3.content from geo.places(1) " +
            "where text=\"%s\" and lang = \"zh-CN\"";

    @Override
    public void saveWeather(List<Forecast> forecastList, ForecastInfo info) {
        //do nothing
    }

    @Override
    public void loadAllCity(final RequestCityCallback cityCallback) {
        RetrofitManager.getWeatherService().queryAllMainCity(getAllMainCityUrl)
                .enqueue(new Callback<List<AllCityResult>>() {
                    @Override
                    public void onResponse(Call<List<AllCityResult>> call, Response<List<AllCityResult>> response) {
                        List<AllCityResult> cityResults = response.body();
                        List<String> cityList = new ArrayList<>();
                        for (AllCityResult cityResult : cityResults) {
                            cityList.add(formatCityName(cityResult.getName(), cityResult.getParent1(), cityResult.getParent2(), cityResult.getParent3()));

                        }

                        if (cityList.size() > 0) {
                            cityCallback.success(cityList);
                        } else
                            cityCallback.error();
                    }

                    @Override
                    public void onFailure(Call<List<AllCityResult>> call, Throwable t) {
                        cityCallback.error();
                    }
                });

    }

    @Override
    public void queryWeather(RequestWeatherCallback callback) {
        queryWeather(PreferenceUtil.getWoeid(), callback);
    }

    @Override
    public void queryWeather(String woeid, final RequestWeatherCallback callback) {
        final String woeid_copy;
        if (woeid == null)
            woeid_copy = PreferenceUtil.getWoeid();
        else
            woeid_copy = woeid;

        Call<WeatherResult> call = RetrofitManager.getWeatherService()
                .queryWeather(String.format(queryWeatherString, woeid_copy), formatMethod);

        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                Log.d(this.getClass().getSimpleName(), response.raw().toString());
                if (response.code() != 200) {
                    callback.error(App.getContext().getString(R.string.error_network) + ":" + response.message());
                    return;
                }
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
                            temp, text, woeid_copy, sunrise, sunset);


                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(call, e);
                    // TODO: 16-9-28 ??
                }
//                assert forecasts != null && forecastInfo != null;
                if (forecasts != null && forecastInfo != null && forecasts.length > 0) {

                    callback.success(Arrays.asList(forecasts), forecastInfo);
                } else
                    callback.error(App.getContext().getString(R.string.error_network));
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Log.e(getClass().getSimpleName(), t.getMessage());
                callback.error(App.getContext().getString(R.string.error_network));
            }
        });


    }

    @Override
    public void saveAllCity(List<String> cityList) {
        //do nothing
    }

    @Override
    public void queryCity(final RequestCityResultCallback callback, String cityName) {
        Call<CitySearchResult> call = RetrofitManager.getWeatherService().queryCity(String.format(queryCityString, cityName), formatMethod);
        call.enqueue(new Callback<CitySearchResult>() {
            @Override
            public void onResponse(Call<CitySearchResult> call, Response<CitySearchResult> response) {
                CitySearchResult citySearchResult = response.body();
                if (citySearchResult == null) {
                    callback.error(App.getContext().getString(R.string.no_result));
                    return;
                }
                String woeid = null;
                String name = null;
                String country = null;
                String admin1 = null;
                String admin2 = null;
                String admin3 = null;
                StringBuilder fullName;
                City result;
                try {
                    woeid = citySearchResult.getQuery().getResults().getPlace().getWoeid();
                    name = citySearchResult.getQuery().getResults().getPlace().getName();
                    country = citySearchResult.getQuery().getResults().getPlace().getCountry();
                    admin1 = citySearchResult.getQuery().getResults().getPlace().getAdmin1();
                    admin2 = citySearchResult.getQuery().getResults().getPlace().getAdmin2();
                    admin3 = citySearchResult.getQuery().getResults().getPlace().getAdmin3();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error(App.getContext().getString(R.string.no_result));
                    return;

                }
                fullName = new StringBuilder();


                if (country != null) {
                    fullName.append(country);
                }
                if (admin1 != null) {
                    fullName.append(admin1);
                }
                if (admin2 != null) {
                    fullName.append(admin2);
                }
                if (admin3 != null) {
                    fullName.append(admin3);
                }
                result = new City(name, woeid, fullName.toString());
                // TODO: 16-10-8 the only one result, I know it's stupid
                List<City> cities = new ArrayList<>();
                cities.add(result);
                callback.success(cities);

            }

            @Override
            public void onFailure(Call<CitySearchResult> call, Throwable t) {
                callback.error(App.getContext().getString(R.string.error_network));
            }
        });
    }

    private String formatCityName(String name, String parent1, String parent2, String parent3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("直辖市".equals(parent2) || "特别行政区".equals(parent2) ? "" : parent2 + " ").append(name != null ? (name.equals(parent1) ? name + " " : parent1 + " " + name) : "");
        return stringBuilder.toString();
    }


}

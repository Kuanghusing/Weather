package com.kuahusg.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.SelectArea;
import com.kuahusg.weather.activities.WeatherActivity;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Data.CitySearchResult;
import com.kuahusg.weather.model.Data.Citys;
import com.kuahusg.weather.model.Data.WeatherResult;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-27.
 */
public class Utility {

    private static Context mContext;
    private static boolean isFromService;


    public static void queryCity(String city_name, final Context context) {
        mContext = context;
        try {
            city_name = URLEncoder.encode(city_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String yql = " select woeid,name,country.content," +
                "admin1.content,admin2.content,admin3.content from geo.places(1) " +
                "where text=\"" + city_name + "\" and lang = \"zh-CN\" &format=json";
        final String new_address = "https://query.yahooapis.com/v1/public/yql?q=" +
                yql.replaceAll(" ", "%20").replaceAll("\"", "%22");
        new QueryCityTask().execute(new_address);

    }

    public static boolean queryWeather(String woeid, final Context context, final boolean isFromService) {
        String infoAddress = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.condition.temp,item.condition.date from weather.forecast where woeid = " +
                woeid + " and u=\"c\"&format=json";
/*        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.forecast.date,item.forecast.low,item.forecast.high,item.forecast.text"
                + " from weather.forecast where woeid = " + woeid + " and u=\"c\"&format=json";*/

        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select * from weather.forecast "
                + "where woeid = " + woeid + " and u=\"c\"&format=json";


        if (isFromService) {
            WeatherDB.getInstance(context);
        }
        WeatherDB.deleteTable("temp");
        WeatherDB.deleteTable("Forecast");
        Utility.mContext = context;
        Utility.isFromService = isFromService;


        new UpdateForecastInfoTask(woeid).execute(infoAddress.replaceAll(" ", "%20").replaceAll("\"", "%22"));
        new UpdateForecastTask(woeid).execute(address.replaceAll(" ", "%20").replaceAll("\"", "%22"));

        return true;

    }

    public static void handleCityList() {

        WeatherDB.deleteTable("city");
        String address = "https://raw.githubusercontent.com/Kuanghusing/City_list/master/city-list";

        new QueryCityTask.HandleCityListTask().execute(address);


    }

    private static JSONObject getJsonObject(JSONObject fromJsonObject, String... strings) throws JSONException {
        JSONObject jsonObject = fromJsonObject;
        for (String s :
                strings) {
            jsonObject = jsonObject.getJSONObject(s);

        }
        return jsonObject;
    }

    public static boolean hasNetwork(Context mContext) {
        if (Utility.mContext == null && Myapplication.getContext() != null) {
            Utility.mContext = Myapplication.getContext();
        } else if (mContext != null) {
            Utility.mContext = mContext;
        } else
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) Utility.mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return info.getState() == NetworkInfo.State.CONNECTED;
        } else
            return false;
    }


    public static ForecastInfo loadForecastInfoFromDatabase(String woeid) {
        return WeatherDB.loadForecastInfo(woeid);
    }




    /*
    * a lot of task ...
     */

    public static List<Forecast> loadForecastFromDatabase(String woeid) {
        return WeatherDB.loadForecast(woeid);
    }


    private static class UpdateForecastTask extends AsyncTask<String, String, WeatherResult> {

        String woeid;

        public UpdateForecastTask(String woeid) {
            this.woeid = woeid;
        }

        @Override
        protected WeatherResult doInBackground(String... params) {
            List<Forecast> forecastList = new ArrayList<>();


            String result;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(mContext.getString(R.string.no_network));
                return null;
            }

            Forecast forecast;
            ForecastInfo forecastInfo;


            /*JSONObject jsonObject = new JSONObject(result);
                JSONObject results = getJsonObject(jsonObject, "query", "results");
                JSONArray item = results.getJSONArray("channel");
                for (int i = 0; i < item.length(); i++) {
                    JSONObject day = item.getJSONObject(i);
                    JSONObject forecast = getJsonObject(day, "item", "forecast");
                    f = new Forecast(forecast.getString("date"), forecast.getString("high"),
                            forecast.getString("low"), forecast.getString("text"));
                    forecastList.add(f);
                }*/


            Gson gson = new Gson();
            WeatherResult weatherResult;
            try {
                weatherResult = gson.fromJson(result, WeatherResult.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                publishProgress(mContext.getString(R.string.no_result));
                return null;
            }


            LogUtil.v(this.getClass().toString(), "slove weather info finish");

            return weatherResult;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (!isFromService) {
                Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
                WeatherActivity.refreshLayout.setRefreshing(false);
            }

        }

        @Override
        protected void onPostExecute(WeatherResult weatherResult) {
            super.onPostExecute(weatherResult);
/*            if (we != null) {
                for (Forecast f :
                        forecastList) {
                    WeatherDB.saveForecast(f);
                }
                if (!isFromService) {
                    WeatherActivity.todayFrag.showWeather(forecastList);
                    WeatherActivity.futureWeatherFrag.refreshWeather(forecastList);
                    WeatherActivity.todayFrag.hideProgressBar();
                    if (WeatherActivity.refreshLayout.isRefreshing()) {
                        WeatherActivity.refreshLayout.setRefreshing(false);

                    }

                    Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.load_finish), Snackbar.LENGTH_LONG).show();
                }
            }*/
            List<Forecast> forecastList = new ArrayList<>();
            ForecastInfo forecastInfo;


            Forecast[] forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();
            for (Forecast f :
                    forecasts) {
                forecastList.add(f);
            }
            /*forecastInfo = new ForecastInfo(link, lastBuildDate, wind_direction, wind_speed, date,
                    temp, text, woeid, sunrise, sunset);*/


            /*
            * show the main weather info
             */


            if (forecastList.size() > 0) {
                for (Forecast f :
                        forecastList) {
                    WeatherDB.saveForecast(f);
                }
            }

            if (!isFromService) {
                WeatherActivity.todayFrag.showWeather(forecastList);
                WeatherActivity.futureWeatherFrag.refreshWeather(forecastList);
                WeatherActivity.todayFrag.hideProgressBar();
                WeatherActivity.refreshLayout.setRefreshing(false);

            }
            Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.load_finish), Snackbar.LENGTH_LONG).show();

            /*
            * show other info
             */







        }


    }


    private static class UpdateForecastInfoTask extends AsyncTask<String, String, WeatherResult> {
        String woeid;

        public UpdateForecastInfoTask(String woeid) {
            this.woeid = woeid;
        }

        @Override
        protected WeatherResult doInBackground(String... params) {

            String result = null;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(mContext.getString(R.string.error_network));
                return null;

            }

            Gson gson = new Gson();

            return gson.fromJson(result, WeatherResult.class);
            /*StringBuffer tempAndDate = new StringBuffer();

            String result = null;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject condition = getJsonObject(jsonObject, "query", "results", "channel",
                        "item", "condition");

                tempAndDate.append(condition.getString("temp")).append("|").append(condition.getString("date"));

            } catch (JSONException e) {
                if (mContext != null) {
                    return null;
                }
                e.printStackTrace();


            }

            return tempAndDate.toString();*/
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (!isFromService) {
                Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
                WeatherActivity.refreshLayout.setRefreshing(false);
            }

        }

        @Override
        protected void onPostExecute(WeatherResult weatherResult) {
            super.onPostExecute(weatherResult);

            ForecastInfo forecastInfo = null;
            List<Forecast> forecastList = new ArrayList<>();
            Forecast[] forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();


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


            for (Forecast f :
                    forecasts) {
                forecastList.add(f);
            }
            forecastInfo = new ForecastInfo(link, lastBuildDate, wind_direction, wind_speed, date,
                    temp, text, woeid, sunrise, sunset);

            WeatherDB.saveForecastInfo(forecastInfo);
            if (!isFromService) {
                WeatherActivity.todayFrag.showForecastInfo(forecastInfo);
            }
            /*String temp;
            String pushDate;
            if (s != null) {
                String t[] = s.split("\\|");
                temp = t[0];
                pushDate = t[1];
                WeatherDB.saveForecastInfo(Integer.valueOf(temp));
                if (!isFromService) {
                    WeatherActivity.todayFrag.showForecastInfo(s);
                }
            }*/

        }


    }


    private static class QueryCityTask extends AsyncTask<String, String, List<City>> {
        @Override
        protected List<City> doInBackground(String... params) {
            String result = "";
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");


            } catch (Exception e) {
                if (mContext != null) {
                    publishProgress(mContext.getString(R.string.no_result));
                    e.printStackTrace();
                    return null;
                }
            }
            List<City> cityList = new ArrayList<>();
            City city;
            Gson gson = new Gson();
            CitySearchResult citySearchResult = gson.fromJson(result, CitySearchResult.class);
            StringBuilder fullName;


            if (!TextUtils.isEmpty(result)) {

                String woeid = citySearchResult.getQuery().getResults().getPlace().getWoeid();
                String name = citySearchResult.getQuery().getResults().getPlace().getName();
                String country = citySearchResult.getQuery().getResults().getPlace().getCountry();
                String admin1 = citySearchResult.getQuery().getResults().getPlace().getAdmin1();
                String admin2 = citySearchResult.getQuery().getResults().getPlace().getAdmin2();
                String admin3 = citySearchResult.getQuery().getResults().getPlace().getAdmin3();
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
                city = new City(name, woeid, fullName.toString());
                LogUtil.v("Utility.queryCity:city", city.getCity_name() + city.getFullNmae() +
                        city.getWoeid());
                cityList.add(city);
                LogUtil.v(this.getClass().getName() + "\tcityList.size()", cityList.size() + "\t");


            }
            return cityList;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            SelectArea.dismissProgress();
            Toast.makeText(mContext, values[0], Toast.LENGTH_LONG).show();

        }


        @Override
        protected void onPostExecute(List<City> cityList) {
            super.onPostExecute(cityList);
            SelectArea.dismissProgress();
            SelectArea.cityList.clear();
            SelectArea.cityL = cityList;
            for (City city :
                    cityList) {
                SelectArea.cityList.add(city.getFullNmae());
            }

            if (SelectArea.cityList.size() > 0) {
                SelectArea.adapter.notifyDataSetChanged();
                SelectArea.cityListView.setSelection(0);
            }
        }


        private static class HandleCityListTask extends AsyncTask<String, String, List<Citys>> {
            @Override
            protected List<Citys> doInBackground(String... params) {
                String result = null;
                try {
                    result = HttpUtil.sendHttpReauest(params[0], "GET");

                } catch (Exception e) {
                    publishProgress();
                }

                List<Citys> list = new ArrayList<>();

                Gson gson;
                try {
                    gson = new Gson();
                    list = gson.fromJson(result, new TypeToken<List<Citys>>() {
                    }.getType());
                } catch (Exception e) {
                    publishProgress();
                }

                return list;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                SelectArea.dismissProgress();
            }


            @Override
            protected void onPostExecute(List<Citys> cityList) {
                super.onPostExecute(cityList);
                List<String> list = new ArrayList<>();
                if (cityList != null) {
                    for (int i = 0; i < cityList.size(); i++) {
                        Citys citys = cityList.get(i);
                        String name = citys.getName();
                        String parent1 = citys.getParent1();
                        String parent2 = citys.getParent2();
                        String parent3 = citys.getParent3();
                        StringBuilder stringInfo = new StringBuilder();

                        if (!(parent3.equals("直辖市") || parent2.equals(parent3))) {
                            stringInfo.append(parent3).append(" " + parent2).append(" " + parent1).append(name);

                        } else {
                            stringInfo.append(parent1).append(name);
                        }
                        list.add(stringInfo.toString());
                        stringInfo.setLength(0);
                    }


                    WeatherDB.saveCity(list);

                    SelectArea.dismissProgress();
                    List<String> loadCity = WeatherDB.loadCity();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putString("hasLoadCity", "OK");
                    editor.apply();
                    SelectArea.hasLoadCityList(loadCity);
                }

            }
        }
    }


}

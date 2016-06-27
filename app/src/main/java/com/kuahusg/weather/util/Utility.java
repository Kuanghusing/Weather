package com.kuahusg.weather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.SelectArea;
import com.kuahusg.weather.activities.WeatherActivity;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.CitySearchResult;
import com.kuahusg.weather.model.Citys;
import com.kuahusg.weather.model.Forecast;

import org.json.JSONArray;
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
        final String new_address = "https://query.yahooapis.com/v1/public/yql?q=" + yql.replaceAll(" ", "%20").replaceAll("\"", "%22");

        HttpUtil.sendHttpRequest(new_address, "GET", new HttpCallBackListener() {
            @Override
            public void onFinish(String respon) {
                List<City> cityList = new ArrayList<>();

                try {
/*                    City city;
                    JSONObject json = new JSONObject(respon);
                    JSONObject results = getJsonObject(json, "query", "results");*/

                    City city;


                    Gson gson = new Gson();
                    CitySearchResult citySearchResult = gson.fromJson(respon, CitySearchResult.class);
                    StringBuilder fullName;


                    if (!TextUtils.isEmpty(respon)) {
/*
                        JSONObject place = results.getJSONObject("place");
                        String woeid = place.getString("woeid");
                        String name = place.getString("name");
                        StringBuilder fullName = new StringBuilder();

                        String country = place.getString("country");
                        String admin1 = place.getString("admin1");
                        String admin2 = place.getString("admin2");
                        String admin3 = place.getString("admin3");
*/


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


                } catch (Exception e) {
                    if (context != null) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, context.getString(R.string.no_result), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    e.printStackTrace();
                }

                SelectArea.dismissProgress();

                Message result = new Message();
                result.what = SelectArea.RESULT_OK;
                result.obj = cityList;
                SelectArea.handler.sendMessage(result);

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d(this.toString() + "\tonError", "onError1:" + e);
                if (SelectArea.editText != null) {
                    Snackbar.make(SelectArea.editText, context.getString(R.string.no_network), Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }

    public static boolean queryWeather(String woeid, final Context context, final boolean isFromService) {
        String tempAndPushDate = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.condition.temp,item.condition.date from weather.forecast where woeid = " +
                woeid + " and u=\"c\"&format=json";
        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.forecast.date,item.forecast.low,item.forecast.high,item.forecast.text"
                + " from weather.forecast where woeid = " + woeid + " and u=\"c\"&format=json";
        if (isFromService) {
            WeatherDB.getInstance(context);
        }
        WeatherDB.deleteTable("temp");
        WeatherDB.deleteTable("Forecast");
        Utility.mContext = context;
        Utility.isFromService = isFromService;


        new UpdateTempAndDateTask().execute(tempAndPushDate.replaceAll(" ", "%20").replaceAll("\"", "%22"));
        new UpdateForecastTask().execute(address.replaceAll(" ", "%20").replaceAll("\"", "%22"));

        return true;

    }

    public static void handleCityList() {

        WeatherDB.deleteTable("city");
        String address = "https://raw.githubusercontent.com/Kuanghusing/City_list/master/city-list";


        HttpUtil.sendHttpRequest(address, "GET", new HttpCallBackListener() {
            @Override
            public void onFinish(String respon) {
                List<String> list = new ArrayList<>();
                try {

                    Gson gson = new Gson();
                    List<Citys> cities = gson.fromJson(respon, new TypeToken<List<Citys>>() {
                    }.getType());

                    for (int i = 0; i < cities.size(); i++) {
                        Citys citys = cities.get(i);
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


                    Message message = new Message();
                    message.what = SelectArea.PROSSDIALOG_DISSMISS;
                    SelectArea.handler.sendMessage(message);
//                    SelectArea.dismissProgress();
                    message = new Message();
                    message.what = SelectArea.UPDATE_CITY_LIST;
                    SelectArea.handler.sendMessage(message);


                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
        });
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

        ConnectivityManager connectivityManager = (ConnectivityManager) Utility.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return info.getState() == NetworkInfo.State.CONNECTED;
        } else
            return false;
    }


    public static String loadTempAndDateFromDatabase() {
        return WeatherDB.loadTempAndDate();
    }


    public static List<Forecast> loadForecastFromDatabase() {
        return WeatherDB.loadForecast();
    }


    private static class UpdateForecastTask extends AsyncTask<String, String, List<Forecast>> {


        @Override
        protected List<Forecast> doInBackground(String... params) {
            List<Forecast> forecastList = new ArrayList<>();


            String result;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(mContext.getString(R.string.no_network));
                return null;
            }
            try {
                Forecast f;
                JSONObject jsonObject = new JSONObject(result);
                JSONObject results = getJsonObject(jsonObject, "query", "results");
                JSONArray item = results.getJSONArray("channel");
                for (int i = 0; i < item.length(); i++) {
                    JSONObject day = item.getJSONObject(i);
                    JSONObject forecast = getJsonObject(day, "item", "forecast");
                    f = new Forecast(forecast.getString("date"), forecast.getString("high"),
                            forecast.getString("low"), forecast.getString("text"));
//                            forecastList.add(f);
                    forecastList.add(f);


                }
                LogUtil.v(this.getClass().toString(), "slove weather info finish");


            } catch (JSONException e) {
                publishProgress(mContext.getString(R.string.no_result));


//                Toast.makeText(mContext,mContext.getString(R.string.no_result),Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return null;
            }
            return forecastList;
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
        protected void onPostExecute(List<Forecast> forecastList) {
            super.onPostExecute(forecastList);
            if (forecastList != null) {
                for (Forecast f :
                        forecastList) {
                    WeatherDB.saveForecast(f);
                }
//            WeatherActivity.updateInfomation(null, forecastList);
                if (!isFromService) {
                    WeatherActivity.todayFrag.showWeather(forecastList);
                    WeatherActivity.futureWeatherFrag.refreshWeather(forecastList);
                    WeatherActivity.todayFrag.hideProgressBar();
                    if (WeatherActivity.refreshLayout.isRefreshing()) {
                        WeatherActivity.refreshLayout.setRefreshing(false);

                    }

                    Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.load_finish), Snackbar.LENGTH_LONG).show();
                }
            }

        }


    }


    private static class UpdateTempAndDateTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            StringBuffer tempAndDate = new StringBuffer();

            String result = null;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
//                publishProgress(mContext.getString(R.string.no_network));
                return null;

            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject condition = getJsonObject(jsonObject, "query", "results", "channel",
                        "item", "condition");
//                                tempAndDate.append(condition.getString("date")).append("|");
//                                tempAndDate.append(condition.getString("temp"));
//                getTempAndDate(condition.getString("date"), condition.getString("temp"));

                tempAndDate.append(condition.getString("temp")).append("|").append(condition.getString("date"));

            } catch (JSONException e) {
                if (mContext != null) {


//                    Toast.makeText(mContext, mContext.getString(R.string.no_result), Toast.LENGTH_LONG).show();
//                    publishProgress(mContext.getString(R.string.no_result));
                    return null;
                }
                e.printStackTrace();


            }

            return tempAndDate.toString();
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String temp;
            String pushDate;
            if (s != null) {
                String t[] = s.split("\\|");
                temp = t[0];
                pushDate = t[1];
                WeatherDB.saveTempAndDate(Integer.valueOf(temp), pushDate);
                if (!isFromService) {
                    WeatherActivity.todayFrag.showTempAndDate(s);
                }
            }

        }


    }

}

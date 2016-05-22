package com.kuahusg.weather.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.SelectArea;
import com.kuahusg.weather.activities.WeatherActivity;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
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


    public static void quaryCity(String city_name, final Context context) {
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
                    City city;
                    JSONObject json = new JSONObject(respon);
                    JSONObject results = getJsonObject(json, "query", "results");
                    if (!TextUtils.isEmpty(respon)) {
                        JSONObject place = results.getJSONObject("place");
                        String woeid = place.getString("woeid");
                        String name = place.getString("name");
                        StringBuilder fullName = new StringBuilder();

                        String country = place.getString("country");
                        String admin1 = place.getString("admin1");
                        String admin2 = place.getString("admin2");
                        String admin3 = place.getString("admin3");

                        if (!"null".equals(country)) {
                            fullName.append(country);
                        }
                        if (!"null".equals(admin1)) {
                            fullName.append(admin1);
                        }
                        if (!"null".equals(admin2)) {
                            fullName.append(admin2);
                        }
                        if (!"null".equals(admin3)) {
                            fullName.append(admin3);
                        }
                        city = new City(name, woeid, fullName.toString());
                        LogUtil.v("Utility.queryCity:city", city.getCity_name() + city.getFullNmae() +
                                city.getWoeid());
                        cityList.add(city);
                        LogUtil.v(this.getClass().getName() + "\tcityList.size()", cityList.size() + "\t");

//                        Utility.cityList = cityList;


                    } else {
                        city = null;
                    }


                } catch (JSONException e) {
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
        String tempAndPushdate = "https://query.yahooapis.com/v1/public/yql?q="
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
        final Message show_weather = new Message();
        show_weather.what = WeatherActivity.SHOW_WEATHER;
        final Message tempAndD = new Message();
        tempAndD.what = WeatherActivity.SHOW_TEMP_DATE;

        HttpUtil.sendHttpRequest(tempAndPushdate.replaceAll(" ", "%20").replaceAll("\"", "%22"),
                "GET", new HttpCallBackListener() {
                    @Override
                    public void onFinish(String respon) {
                        try {
                            JSONObject jsonObject = new JSONObject(respon);
                            JSONObject condition = getJsonObject(jsonObject, "query", "results", "channel",
                                    "item", "condition");
                            String pushDate = condition.getString("date");
                            String temp = condition.getString("temp");


                            WeatherDB.saveTempAndDate(Integer.valueOf(temp), pushDate);


                            if (!isFromService) {
                                WeatherActivity.handler.sendMessage(tempAndD);
                            }

                        } catch (JSONException e) {
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


                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        LogUtil.d("Utility", "onError2" + e);


                    }
                });

        HttpUtil.sendHttpRequest(address.replaceAll(" ", "%20").replaceAll("\"", "%22"), "GET", new HttpCallBackListener() {

            @Override
            public void onFinish(String respon) {
                try {
                    Forecast f;
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONObject results = getJsonObject(jsonObject, "query", "results");
//                    JSONArray item = new JSONArray(channel.toString());
                    JSONArray item = results.getJSONArray("channel");
                    for (int i = 0; i < item.length(); i++) {
                        JSONObject day = item.getJSONObject(i);
                        JSONObject forecast = getJsonObject(day, "item", "forecast");
                        f = new Forecast(forecast.getString("date"), forecast.getString("high"),
                                forecast.getString("low"), forecast.getString("text"));
                        boolean result = WeatherDB.saveForecast(f);


                    }
                    LogUtil.v(this.getClass().toString(), "slove weather info finish");
                    if (!isFromService) {
                        WeatherActivity.handler.sendMessage(show_weather);
                    }


                } catch (JSONException e) {


                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d("Utility", "onError3" + e);
                Looper.prepare();
                if (WeatherActivity.fab != null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(WeatherActivity.fab, context.getString(R.string.no_network), Snackbar.LENGTH_LONG).show();

                        }
                    });
                }


            }
        });
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
                    JSONArray allCityList = new JSONArray(respon);
                    StringBuilder stringInfo = new StringBuilder();

                    for (int i = 0; i < allCityList.length(); i++) {
                        JSONObject cityInfo = allCityList.getJSONObject(i);
                        String name = cityInfo.getString("name");
                        String parent1 = cityInfo.getString("parent1");
                        /*String parent2 = cityInfo.getString("parent2");
                        String parent3 = cityInfo.getString("parent3");*/
/*                        if (!(parent3.equals("直辖市") || parent2.equals(parent3))) {
                            stringInfo.append(parent3).append(" " + parent2).append(" " + parent1).append(name);

                        } else {
                        }*/
                        stringInfo.append(parent1).append(name);
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


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
        });
    }

    public static JSONObject getJsonObject(JSONObject fromJsonObject, String... strings) throws JSONException {
        JSONObject jsonObject = fromJsonObject;
        for (String s :
                strings) {
            jsonObject = jsonObject.getJSONObject(s);

        }
        return jsonObject;
    }


}

package com.kuahusg.weather.util;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

    public static List<City> cityList = new ArrayList<>();
    public static android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Utility.cityList = (List<City>) msg.obj;
                    LogUtil.v("Utility", "static" + cityList.size() + "\t");

                    break;
            }
        }
    };

    /*public static String handleJosn(String data, String objectName) {
        String objectValue;
        try {
            JSONObject all_info = new JSONObject(data);
//            JSONObject info = all_info.getJSONObject("weatherinfo");
            objectValue = all_info.getString(objectName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objectName;

    }*/

    public synchronized static List<City> quaryCity(String city_name) {
        final String yql = " select woeid,name,country.content," +
                "admin1.content,admin2.content,admin3.content from geo.places(1) " +
                "where text=\"" + city_name + "\" and lang = \"zh-CN\" &format=json";
        final String new_address = "https://query.yahooapis.com/v1/public/yql?q=" + yql.replaceAll(" ", "%20").replaceAll("\"", "%22");
//        final String new_address = "https://query.yahooapis.com/v1/public/yql?q=" + URLEncoder.encode(yql, "UTF-8").replaceAll("\\+","%20");

        HttpUtil.sendHttpRequest(new_address, "GET", new HttpCallBackListener() {
            @Override
            public void onFinish(String respon) {
                try {
                    List<City> cityList = new ArrayList<City>();
                    City city;
                    JSONObject json = new JSONObject(respon);
/*                    JSONObject query = json.getJSONObject("query");
                    JSONObject results = query.getJSONObject("results");*/
                    JSONObject results = getJsonObject(json, "query", "results");
                    if (!TextUtils.isEmpty(respon)) {
                        JSONObject place = results.getJSONObject("place");
                        String woeid = place.getString("woeid");
                        String name = place.getString("name");
                        StringBuffer fullNmae = new StringBuffer();

                        String country = place.getString("country");
                        String admin1 = place.getString("admin1");
                        String admin2 = place.getString("admin2");
                        String admin3 = place.getString("admin3");

                        if (!"null".equals(country)) {
                            fullNmae.append(country);
                        }
                        if (!"null".equals(admin1)) {
                            fullNmae.append(admin1);
                        }
                        if (!"null".equals(admin2)) {
                            fullNmae.append(admin2);
                        }
                        if (!"null".equals(admin3)) {
                            fullNmae.append(admin3);
                        }
                        city = new City(name, woeid, fullNmae.toString());
                        LogUtil.v("Utility.queryCity:city", city.getCity_name() + city.getFullNmae() +
                                city.getWoeid());
                        cityList.add(city);
                        LogUtil.v(this.getClass().getName() + "\tcityList.size()", cityList.size() + "\t");
                        /*Message m = new Message();
                        m.what = 1;
                        m.obj = cityList;
                        handler.sendMessage(m);*/
                        Utility.cityList = cityList;
//                        Utility.cityList = cityList;


                    } else {
                        city = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d(this.toString() + "\tonError", "onError1:" + e);
/*                Toast.makeText(Myapplication.getContext(), "看看can not resolve info from server啦了",
                        Toast.LENGTH_LONG).show();*/

            }
        });
        LogUtil.v("Utility#cityList.size()2", cityList.size() + "\t");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Utility.cityList;
    }

    public static boolean queryWeather(String woeid) {
        String tempAndPushdate = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.condition.temp,item.condition.date from weather.forecast where woeid = " +
                woeid + " and u=\"c\"&format=json";
        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.forecast.date,item.forecast.low,item.forecast.high,item.forecast.text"
                + " from weather.forecast where woeid = " + woeid + " and u=\"c\"&format=json";
        HttpUtil.sendHttpRequest(tempAndPushdate.replaceAll(" ", "%20").replaceAll("\"", "%22"), "GET", new HttpCallBackListener() {
            @Override
            public void onFinish(String respon) {
                try {
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONObject condition = getJsonObject(jsonObject, "query", "results", "channel",
                            "item", "condition");
                    String pushDate = condition.getString("date");
                    String temp = condition.getString("temp");


                    WeatherDB.saveTempAndDate(Integer.valueOf(temp), pushDate);


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d("Utility", "onError2");
/*                Toast.makeText(Myapplication.getContext(), "can not query the temp data from server",
                        Toast.LENGTH_LONG).show();*/

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
                        if (result) {

                        }

                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d("Utility", "onError3");
/*                Toast.makeText(Myapplication.getContext(), "can not query weather info from server",
                        Toast.LENGTH_LONG).show();*/

            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;

    }

    public static JSONObject getJsonObject(JSONObject fromJsonObject, String... strings) {
        JSONObject jsonObject = fromJsonObject;
        for (String s :
                strings) {
            try {
                jsonObject = jsonObject.getJSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObject;
    }


}

package com.kuahusg.weather.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

        HttpUtil httpUtil = new HttpUtil();
        httpUtil.sendHttpRequest(new_address, "GET", new HttpCallBackListener() {
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

                        Utility.cityList = cityList;
//                        Utility.cityList = cityList;


                    } else {
                        city = null;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = SelectArea.PROSSDIALOG_DISSMISS;
                SelectArea.handler.sendMessage(message);
                Message result = new Message();
                result.what = SelectArea.RESULT_OK;
                SelectArea.handler.sendMessage(result);

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d(this.toString() + "\tonError", "onError1:" + e);
/*                Toast.makeText(Myapplication.getContext(), "看看can not resolve info from server啦了",
                        Toast.LENGTH_LONG).show();*/

            }
        });
        /*try {
            new Thread(httpUtil).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        LogUtil.v("Utility#cityList.size()2", cityList.size() + "\t");
/*        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return Utility.cityList;
    }

    public static boolean queryWeather(String woeid) {
        String tempAndPushdate = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.condition.temp,item.condition.date from weather.forecast where woeid = " +
                woeid + " and u=\"c\"&format=json";
        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select item.forecast.date,item.forecast.low,item.forecast.high,item.forecast.text"
                + " from weather.forecast where woeid = " + woeid + " and u=\"c\"&format=json";
        final Message message = new Message();
        message.what = WeatherActivity.PROSSDIALOG_DISSMISS;
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


                    WeatherActivity.handler.sendMessage(tempAndD);

                } catch (JSONException e) {
                    e.printStackTrace();


                    //WeatherActivity.handler.sendMessage(message);


                }


            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d("Utility", "onError2" + e);
/*                Toast.makeText(Myapplication.getContext(), "can not query the temp data from server",
                        Toast.LENGTH_LONG).show();*/

                //WeatherActivity.handler.sendMessage(message);


            }
        });
/*        try {
            new Thread(httpUtil).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

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
                    WeatherActivity.handler.sendMessage(show_weather);
                    WeatherActivity.handler.sendMessage(message);


                } catch (JSONException e) {

                    //                   WeatherActivity.handler.sendMessage(message);

                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d("Utility", "onError3" + e);
/*                Toast.makeText(Myapplication.getContext(), "can not query weather info from server",
                        Toast.LENGTH_LONG).show();*/

//                WeatherActivity.handler.sendMessage(message);

            }
        });
/*        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


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

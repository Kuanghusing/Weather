package com.kuahusg.weather.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kuahusg.weather.App;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.NetwordUtil;

import java.util.ArrayList;
import java.util.List;

import static com.kuahusg.weather.data.local.WeatherOpenHelper.FORECAST_DB_NAME;
import static com.kuahusg.weather.data.local.WeatherOpenHelper.INFO_DB_NAME;


/**
 * Created by kuahusg on 16-9-28.
 */

public class WeatherDB {
    private static final int VERSION = 2;
    private static String DB_NAME = "myWeatherDataBase";    //stupid name

    private SQLiteDatabase db = new WeatherOpenHelper(App.getContext().getApplicationContext(), DB_NAME, null, VERSION).getWritableDatabase();

    private WeatherDB() {


    }

    public static void saveAllMainCity(List<String> cityList) {
        SQLiteDatabase db = InstanceHolder.INSTANCE.db;


        if (cityList.size() > 0) {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            for (String city :
                    cityList) {

                contentValues.put("city_name", city);
                db.insert("city", null, contentValues);
            }
            db.setTransactionSuccessful();


        }


        db.endTransaction();
    }

    public synchronized static void loadAllMainCity(RequestCityCallback callback) {

        SQLiteDatabase db = InstanceHolder.INSTANCE.db;
        List<String> cityList = new ArrayList<>();

        try {

            City city;
            Cursor cursor = db.query("City", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
                    cityList.add(city_name);

                }
            }
            cursor.close();
            LogUtil.v("WeatherDB", "loadCity finish!" + cityList.size());
        } catch (Exception e) {
            callback.error();
        }

        callback.success(cityList);

    }


    public static void saveForecastAndInfo(List<Forecast> forecasts, ForecastInfo info) {
        //data source: 这锅我不背

        SQLiteDatabase db = InstanceHolder.INSTANCE.db;


//        deleteTable("Forecast");

        boolean flag = false;
        if (forecasts != null) {
            db.beginTransaction();
            try {
                for (Forecast forecast : forecasts) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("date", forecast.getDate());
                    contentValues.put("high", forecast.getHigh());
                    contentValues.put("low", forecast.getLow());
                    contentValues.put("weatherText", forecast.getText());
                    contentValues.put("woeid", forecast.getWoeid());
                    db.insert(FORECAST_DB_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
//                db.endTransaction();
                Log.e("WeatherDB", e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        ContentValues contentValues = new ContentValues();
        if (info != null) {
            String link = info.getLink();
            String lastBuildDate = info.getLastBuildDate();
            String wind_speed = info.getWindSpeed();
            String wind_direction = info.getWindDirection();
            String sunrise = info.getSunrise();
            String sunset = info.getSunset();
            String condition_date = info.getDate();
            String condition_temp = info.getTemp();
            String condition_text = info.getText();
            String woeid = info.getWoeid();

            contentValues.put("link", link);
            contentValues.put("lastBuildDate", lastBuildDate);
            contentValues.put("wind_speed", wind_speed);
            contentValues.put("wind_direction", wind_direction);
            contentValues.put("sunrise", sunrise);
            contentValues.put("sunset", sunset);
            contentValues.put("condition_date", condition_date);
            contentValues.put("condition_temp", condition_temp);
            contentValues.put("condition_text", condition_text);
            contentValues.put("woeid", woeid);
            db.insert(INFO_DB_NAME, null, contentValues);
        }
    }


    public static void loadForecastAndInfo(String woeid, RequestWeatherCallback callback) {

        SQLiteDatabase db = InstanceHolder.INSTANCE.db;

        List<Forecast> forecastList = new ArrayList<>();
        Cursor cursor = null;
        Forecast forecast;
        try {
            cursor = db.query("Forecast", null, "woeid = ?", new String[]{woeid}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    forecast = new Forecast(
                            cursor.getString(cursor.getColumnIndex("date")),
                            cursor.getString(cursor.getColumnIndex("high")),
                            cursor.getString(cursor.getColumnIndex("low")),
                            cursor.getString(cursor.getColumnIndex("weatherText")),
                            cursor.getString(cursor.getColumnIndex("woeid")));
                    forecastList.add(forecast);
                } while (cursor.moveToNext());

            }


        } catch (SQLException e) {
            callback.error(null);
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        ForecastInfo info = null;

        Cursor cur = null;
        try {
            cur = db.query("info", null, "woeid = ?", new String[]{woeid}, null, null, null);
            if (cur.moveToFirst()) {
                String link = cur.getString(cur.getColumnIndex("link"));
                String lastBuildDate = cur.getString(cur.getColumnIndex("lastBuildDate"));
                String windDirection = cur.getString(cur.getColumnIndex("wind_direction"));
                String windSpeed = cur.getString(cur.getColumnIndex("wind_speed"));
                String date = cur.getString(cur.getColumnIndex("condition_date"));
                String temp = cur.getString(cur.getColumnIndex("condition_temp"));
                String text = cur.getString(cur.getColumnIndex("condition_text"));
                String sunrise = cur.getString(cur.getColumnIndex("sunrise"));
                String sunset = cur.getString(cur.getColumnIndex("sunset"));
                info = new ForecastInfo(link, lastBuildDate, windDirection, windSpeed, date, temp, text, woeid, sunrise, sunset);
/*            db.setTransactionSuccessful();
            db.endTransaction();*/
            }

        } catch (SQLException e) {
            callback.error(null);

        } finally {
            if (cur != null) {
                cur.close();
            }
        }

        callback.success(forecastList, info);


    }

    public static boolean deleteTable(String table) {
        SQLiteDatabase db = InstanceHolder.INSTANCE.db;

        try {
            db.beginTransaction();
            db.execSQL("delete from " + table);
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (NetwordUtil.hasNetwork(App.getContext())) {
                db.setTransactionSuccessful();
                db.endTransaction();
            } else {
                LogUtil.v("WeatherDB", "No Network! transaction fail");
                db.endTransaction();
            }
        }
        return true;
    }


    private static final class InstanceHolder {
        private static final WeatherDB INSTANCE = new WeatherDB();
    }
}

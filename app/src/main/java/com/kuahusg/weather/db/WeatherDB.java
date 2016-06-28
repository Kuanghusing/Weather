package com.kuahusg.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-25.
 */
public class WeatherDB {
    public static final int VERSION = 2;
    public static String DB_NAME = "myWeatherDataBase";
    private static SQLiteDatabase db;
    private static WeatherDB weatherDB;
    private static Context mContext;

    private WeatherDB(Context context) {
        WeatherOpenHelper dbhelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbhelper.getWritableDatabase();


    }

    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        mContext = context;
        return weatherDB;
    }

    public static void saveCity(List<String> cityList) {
        try {


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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public synchronized static List<String> loadCity() {

        List<String> cityList = new ArrayList<>();
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

        return cityList;

    }


    public static boolean saveForecast(Forecast forecast) {
//        deleteTable("Forecast");
        boolean flag = false;
        if (forecast != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", forecast.getDate());
            contentValues.put("high", forecast.getHigh());
            contentValues.put("low", forecast.getLow());
            contentValues.put("weatherText", forecast.getText());
            db.insert("Forecast", null, contentValues);
            flag = true;
        }
        return flag;
    }

    public static List<Forecast> loadForecast(String woeid) {

        List<Forecast> forecastList = new ArrayList<>();
        Cursor cursor;
        Forecast forecast;
        cursor = db.query("Forecast", null, "where woeid = ?", new String[]{woeid}, null, null, null);
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
        cursor.close();
        return forecastList;
    }

    public static boolean saveForecastInfo(ForecastInfo info) {
        boolean flag = false;
        ContentValues contentValues = new ContentValues();
        /*if (!TextUtils.isEmpty(pushDate)) {
            contentValues.put("temp", temp);
            contentValues.put("date", pushDate);
            db.insert("temp", null, contentValues);
            flag = true;
        }*/

        db.beginTransaction();
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
            db.insert("info", null, contentValues);
            flag = true;

        }
        db.setTransactionSuccessful();
        db.endTransaction();

        return flag;
    }

    public static ForecastInfo loadForecastInfo(String woeid) {
        ForecastInfo info = null;
//        StringBuilder tempAndDate = new StringBuilder();
        /*Cursor cursor = db.query("temp", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tempAndDate.append(cursor.getInt(cursor.getColumnIndex("temp")));
                tempAndDate.append("|");
                tempAndDate.append(cursor.getString(cursor.getColumnIndex("date")));

            } while (cursor.moveToNext());
        }
        LogUtil.v("WeatherDB", tempAndDate.toString());*/
        db.beginTransaction();
        Cursor cursor = db.query("info", null, "where woeid = ?", new String[]{woeid}, null, null, null);
        if (cursor.moveToFirst()) {
            String link = cursor.getString(cursor.getColumnIndex("link"));
            String lastBuildDate = cursor.getString(cursor.getColumnIndex("lastBuildDate"));
            String windDirection = cursor.getString(cursor.getColumnIndex("wind_direction"));
            String windSpeed = cursor.getString(cursor.getColumnIndex("wind_speed"));
            String date = cursor.getString(cursor.getColumnIndex("condition_date"));
            String temp = cursor.getString(cursor.getColumnIndex("condition_temp"));
            String text = cursor.getString(cursor.getColumnIndex("condition_text"));
            String sunrise = cursor.getString(cursor.getColumnIndex("sunrise"));
            String sunset = cursor.getString(cursor.getColumnIndex("sunset"));
            info = new ForecastInfo(link, lastBuildDate, windDirection, windSpeed, date, temp, text, woeid, sunrise, sunset);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        cursor.close();
        return info;
    }

    public static boolean deleteTable(String table) {
        try {
            db.beginTransaction();
            db.execSQL("delete from " + table);
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (Utility.hasNetwork(mContext)) {
                db.setTransactionSuccessful();
                db.endTransaction();
            } else {
                LogUtil.v("WeatherDB", "No Network! transaction fail");
                db.endTransaction();
            }
        }
        return true;
    }


}

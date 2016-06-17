package com.kuahusg.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-25.
 */
public class WeatherDB {
    public static final int VERSION = 1;
    public static String DB_NAME = "myWeatherDataBase";
    private static SQLiteDatabase db;
    private static WeatherDB weatherDB;

    private WeatherDB(Context context) {
        WeatherOpenHelper dbhelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbhelper.getWritableDatabase();

    }

    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
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
            contentValues.put("weatherText", forecast.getWeatherText());
            db.insert("Forecast", null, contentValues);
            flag = true;
        }
        return flag;
    }

    public static List<Forecast> loadForecast() {

        List<Forecast> forecastList = new ArrayList<>();
        Cursor cursor;
        Forecast forecast;
        cursor = db.query("Forecast", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                forecast = new Forecast(
                        cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("high")),
                        cursor.getString(cursor.getColumnIndex("low")),
                        cursor.getString(cursor.getColumnIndex("weatherText")));
                forecastList.add(forecast);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return forecastList;
    }

    public static boolean saveTempAndDate(int temp, String pushDate) {
        boolean flag = false;
        ContentValues contentValues = new ContentValues();
        if (!TextUtils.isEmpty(pushDate)) {
            contentValues.put("temp", temp);
            contentValues.put("date", pushDate);
            db.insert("temp", null, contentValues);
            flag = true;
        }

        return flag;
    }

    public static String loadTempAndDate() {

        StringBuilder tempAndDate = new StringBuilder();
        Cursor cursor = db.query("temp", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tempAndDate.append(cursor.getInt(cursor.getColumnIndex("temp")));
                tempAndDate.append("|");
                tempAndDate.append(cursor.getString(cursor.getColumnIndex("date")));

            } while (cursor.moveToNext());
        }
        LogUtil.v("WeatherDB", tempAndDate.toString());
        cursor.close();
        return tempAndDate.toString();
    }

    public static boolean deleteTable(String table) {
        try {
            db.execSQL("delete from " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}

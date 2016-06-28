package com.kuahusg.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kuahusg on 16-4-25.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_WEATHER = "create table Forecast("
            + "id integer primary key autoincrement,"
            + "woeid text,"
            + "temp text,"
            + "date text,"
            + "high int,"
            + "low int,"
            + "weatherText text)";
    public static final String CREATE_TEMP_AND_PUSHDATE = "create table temp("
            + "temp int,"
            + "date text)";
    public static final String CREATE_CITY = "create table city("
            + "id integer primary key autoincrement,"
            + "city_name text)";

    public static final String CREATE_WEATHER_INFO = "create table info("
            + "id integer primary key autoincrement,"
            + "woeid text,"
            + "link text,"
            + "lastBuildDate text,"
            + "wind_direction text,"
            + "wind_speed text,"
            + "sunrise text,"
            + "sunset text"
            + "condition_date text,"
            + "condition_temp text"
            + "condition_text text)";

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("drop table if exits Forecast");
                db.execSQL(CREATE_WEATHER);
                db.execSQL(CREATE_WEATHER_INFO);

            default:
                break;

        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_WEATHER);
        db.execSQL(CREATE_TEMP_AND_PUSHDATE);
        db.execSQL(CREATE_CITY);
    }
}

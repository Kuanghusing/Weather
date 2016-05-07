package com.kuahusg.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kuahusg on 16-4-25.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {
    /*public static final String CREATE_PROVINCE = "create table("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";
    public static final String CREATE_CITY = "create table("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code,"
            + "province_id integer)";*/
    /*    public static final String CREATE_COUNTRY = "create table("
                + "id integer primary key autoincrement,"
                + "country_name text,"
                + "country_code text,"
                + "city_id integer)";*/
    public static final String CREATE_WEATHER = "create table Forecast("
            + "id integer primary key autoincrement,"
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
    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
/*        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);*/
//        db.execSQL(CREATE_COUNTRY);
        db.execSQL(CREATE_WEATHER);
        db.execSQL(CREATE_TEMP_AND_PUSHDATE);
        db.execSQL(CREATE_CITY);
    }
}

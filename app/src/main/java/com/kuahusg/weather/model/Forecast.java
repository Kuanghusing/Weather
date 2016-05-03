package com.kuahusg.weather.model;

/**
 * Created by kuahusg on 16-5-1.
 */
public class Forecast {
    private String date;
    private String high;
    private String low;
    private String weatherText;

    public Forecast(String date, String high, String low, String weatherText) {
        this.date = date;
        this.high = high;
        this.low = low;
        this.weatherText = weatherText;
    }



    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWeatherText() {
        return weatherText;
    }
}

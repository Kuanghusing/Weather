package com.kuahusg.weather.model;

import java.io.Serializable;

/**
 * Created by kuahusg on 16-6-28.
 * com.kuahusg.weather.model
 */
public class ForecastInfo implements Serializable {
    private String link;
    private String lastBuildDate;
    private String windDirection;
    private String windSpeed;
    private String date;
    private String temp;
    private String text;
    private String sunrise;
    private String sunset;
    private String woeid;

    public ForecastInfo(String link, String lastBuildDate, String windDirection, String windSpeed,
                        String date, String temp, String text, String woeid, String sunrise, String sunset) {

        this.link = link;
        this.lastBuildDate = lastBuildDate;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.date = date;
        this.temp = temp;
        this.text = text;
        this.woeid = woeid;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }


    public String getLink() {
        return link;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getDate() {
        return date;
    }

    public String getTemp() {
        return temp;
    }

    public String getText() {

        return text;
    }

    public String getWoeid() {
        return woeid;
    }

    public String getSunset() {
        return sunset;
    }

    public String getSunrise() {
        return sunrise;
    }
}


package com.kuahusg.weather.model.bean;

import java.io.Serializable;

/**
 * Created by kuahusg on 16-4-25.
 */
public class City implements Serializable{
    private String city_name;
    private String woeid;
    private String fullNmae;

    public City(String city_name, String woeid, String fullNmae) {
        this.city_name = city_name;
        this.woeid = woeid;
        this.fullNmae = fullNmae;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getWoeid() {
        return woeid;
    }

    public String getFullNmae() {
        return fullNmae;
    }
}

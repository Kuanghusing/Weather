package com.kuahusg.weather.data.callback;

import com.kuahusg.weather.model.bean.City;

import java.util.List;

public interface RequestCityResultCallback {
    void success(List<City> cityList);

    void error(String message);
}

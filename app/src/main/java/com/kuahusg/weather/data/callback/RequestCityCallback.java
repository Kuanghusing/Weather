package com.kuahusg.weather.data.callback;

import java.util.List;

/**
 * Created by kuahusg on 16-9-28.
 */

public interface RequestCityCallback {
    void success(List<String> cityList);

    void error();
}

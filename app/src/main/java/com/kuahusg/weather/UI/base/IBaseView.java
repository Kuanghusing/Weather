package com.kuahusg.weather.UI.base;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface IBaseView {
    void init();

    void start();

    void error(String message);

    void finish();
}

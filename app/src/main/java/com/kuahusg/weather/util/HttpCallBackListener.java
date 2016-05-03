package com.kuahusg.weather.util;

/**
 * Created by kuahusg on 16-4-27.
 */
public interface HttpCallBackListener {
    void onFinish(String respon);
    void onError(Exception e);
}

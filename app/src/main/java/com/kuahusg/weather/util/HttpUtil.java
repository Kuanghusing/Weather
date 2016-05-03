package com.kuahusg.weather.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kuahusg on 16-4-27.
 */
public class HttpUtil  {
    public static void sendHttpRequest(final String address, final String method, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    StringBuffer respon = new StringBuffer();
                    String tmp;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((tmp = bufferedReader.readLine()) != null) {
                        respon.append(tmp);
                    }
                    LogUtil.v("HttpUtil:respon:",respon.toString());
                    if (!TextUtils.isEmpty(respon)) {
                        if (listener != null) {
                            listener.onFinish(respon.toString());

                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}

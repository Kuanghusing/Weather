package com.kuahusg.weather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kuahusg on 16-7-3.
 */
public class NetworkUtil {
    public static boolean hasNetwork(Context mContext) {

        if (mContext == null)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getState() == NetworkInfo.State.CONNECTED;
    }
}

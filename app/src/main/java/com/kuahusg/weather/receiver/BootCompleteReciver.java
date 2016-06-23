package com.kuahusg.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;

/**
 * Created by kuahusg on 16-6-21.
 * com.kuahusg.weather.receiver
 */
public class BootCompleteReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.v(this.toString(),"boot complete,start service");
        Intent intent1 = new Intent(context,AutoUpdateService.class);
        context.startService(intent);

    }
}

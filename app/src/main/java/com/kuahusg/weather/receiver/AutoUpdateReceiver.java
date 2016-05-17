package com.kuahusg.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kuahusg.weather.service.AutoUpdateService;

/**
 * Created by kuahusg on 16-5-10.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}

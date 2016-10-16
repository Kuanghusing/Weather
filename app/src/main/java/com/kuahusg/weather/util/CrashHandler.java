package com.kuahusg.weather.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.kuahusg.weather.App;
import com.kuahusg.weather.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kuahusg on 16-10-13.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler exceptionHandler;

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (throwable != null && exceptionHandler != null) {
            handleException(throwable);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }
            exceptionHandler.uncaughtException(thread, throwable);
            //这个好像要这个方法处理，不然会调用多次handleException()??
        } else {

            Process.killProcess(Process.myPid());
            System.exit(10);
        }

    }

    private CrashHandler() {

    }

    public void init() {
        InstanceHolder.INSTANCE.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    private void handleException(final Throwable e) {
        final StackTraceElement[] elements = e.getStackTrace();
        final String message = "Message:\n" + e.getMessage() + "\n";
        final String fileName = "crash-" + System.currentTimeMillis() + ".log";


        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : elements) {
            sb.append(element.toString()).append("\n");
        }
        final String stackMessage = "\nTraceMessage: " + sb.toString();


        final String toastMessage = App.getContext().getString(R.string.crash_message);


        setNotification(message + stackMessage + getDeviceInfo());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(App.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                try {
                    final FileOutputStream fos = new FileOutputStream(new File(App.getContext()
                            .getExternalFilesDir(null), fileName), true);
                    fos.write((message).getBytes());

                    fos.write((stackMessage).getBytes());
                    fos.write(getDeviceInfo().getBytes());


                    fos.flush();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Toast.makeText(App.getContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
                }
                Looper.loop();


            }
        }).start();
    }

    public static CrashHandler getInstance() {
        return InstanceHolder.INSTANCE;

    }


    private void setNotification(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"foreverhgx@gmail.com"});
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Crash Report");
            intent.setType("text/plain");


            PendingIntent pendingIntent = PendingIntent.getActivity(App.getContext(), 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification = null;
            Notification.Builder builder;
            builder = new Notification.Builder(App.getContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(App.getContext().getString(R.string.solve_crash_message))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setLargeIcon(Icon.createWithResource(App.getContext(), R.mipmap.ic_launcher));
            }
            notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }

    }

    private String getDeviceInfo() {
        PackageManager packageManager = App.getContext().getPackageManager();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append("\nDevices Info:")
                    .append("\nSDK_INT: ").append(Build.VERSION.SDK_INT)
                    .append("\nDEVICE: ").append(Build.DEVICE)
                    .append("\nPackageVersionName: ").append(packageManager.getPackageInfo(App.getContext().getPackageName(), 0).versionName)
                    .append("\nPackageVersionCode: ").append(packageManager.getPackageInfo(App.getContext().getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private static class InstanceHolder {
        static final CrashHandler INSTANCE = new CrashHandler();
    }
}

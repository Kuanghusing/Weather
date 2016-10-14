package com.kuahusg.weather.util;

import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;
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
                Thread.sleep(4000);
            } catch (InterruptedException e) {

            }
            exceptionHandler.uncaughtException(thread, throwable);
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
        final String message = e.getMessage();
        final String fileName = "crash-" + System.currentTimeMillis() + ".log";


        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(App.getContext(), App.getContext().getString(R.string.crash_message)
                        + App.getContext().getExternalFilesDir(null) + "/" + fileName, Toast.LENGTH_LONG).show();
                try {
                    final FileOutputStream fos = new FileOutputStream(new File(App.getContext()
                            .getExternalFilesDir(null), fileName), true);
                    fos.write(("message:" + message + "\n").getBytes());
                    for (StackTraceElement element : elements) {
                        fos.write((element.toString() + "\n").getBytes());
                    }

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


    private static class InstanceHolder {
        static final CrashHandler INSTANCE = new CrashHandler();
    }
}

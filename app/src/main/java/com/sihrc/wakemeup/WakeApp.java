package com.sihrc.wakemeup;

import android.app.Application;

/**
 * Created by sihrc on 11/19/14.
 */
public class WakeApp extends Application {
    public static AlarmDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new AlarmDBHelper(this);
    }
}

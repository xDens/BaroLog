package com.example.barolog.serviceTools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.barolog.MainActivity;
import com.example.barolog.R;


/**
 * Created by xDens on 8/14/15.
 */
public class ServiceTools {
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already", "running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }

    public static String getCurrentUnits(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentUnits = prefs.getString(context.getResources().getString(R.string.units), "");
        return currentUnits;
    }
}

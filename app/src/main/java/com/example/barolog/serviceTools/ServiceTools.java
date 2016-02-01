package com.example.barolog.serviceTools;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.barolog.R;
import com.example.barolog.constants.BasicConst;

import java.text.DecimalFormat;


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
        return prefs.getString(context.getResources().getString(R.string.units), "");
    }

    public static String convertHPaUnits(float hPaPressureValue, String measureUnitToConvert) {
        DecimalFormat df = new DecimalFormat("###.##");
        double convertedValue;

        if (measureUnitToConvert.contains(BasicConst.MeasureUnits.MMHG)) {
            convertedValue = hPaPressureValue * BasicConst.MeasureUnits.MMHG_MULTIPLIER;
        }
        else if (measureUnitToConvert.contains(BasicConst.MeasureUnits.HPA)) {
            convertedValue = hPaPressureValue;
        }
        else if (measureUnitToConvert.contains(BasicConst.MeasureUnits.ATM)) {
            convertedValue = hPaPressureValue / BasicConst.MeasureUnits.ATM_DIVIDER;
        }
        else {
            convertedValue = hPaPressureValue;
        }
        return String.valueOf(df.format(convertedValue));
    }

    public static long getSelectedInterval(Context context) {
        long intervalValue;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String            timerInterval = prefs.getString(context.getResources().getString(R.string.pressureMeasureInterval), "");
        String[]          intervalValues = context.getResources().getStringArray(R.array.interval_array);

        try {
            if (timerInterval.contains(intervalValues[0])) {
                intervalValue = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            } else if (timerInterval.contains(intervalValues[1])) {
                intervalValue = AlarmManager.INTERVAL_HALF_HOUR;
            } else if (timerInterval.contains(intervalValues[2])) {
                intervalValue = AlarmManager.INTERVAL_HOUR;
            } else {
                intervalValue = 30 * 1000;
            }
        } catch (NullPointerException err) {
            intervalValue = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        }

        Log.i("Setting:", Long.toString(intervalValue));
        return intervalValue;

    }
}

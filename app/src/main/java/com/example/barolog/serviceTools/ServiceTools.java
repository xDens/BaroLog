package com.example.barolog.serviceTools;

import android.app.ActivityManager;
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
        else {
            convertedValue = hPaPressureValue;
        }
        return String.valueOf(df.format(convertedValue));
    }
}

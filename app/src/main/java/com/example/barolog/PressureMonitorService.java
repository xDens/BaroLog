package com.example.barolog;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PressureMonitorService extends Service implements SensorEventListener {
    private static final long DEFAULT_INTERVAL = 15 * 60 * 1000; //15 minutes;
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private Sensor mPressure;
    private SensorManager mSensorManager;
    private DatabaseHelper mDataBaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private Timer mTimer;
    private BarolorTimerTask mTimerTask;

    private double multiplier = 1.0;
    private String currentUnits = "";

    public PressureMonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDataBaseHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "Barolog service has been started", Toast.LENGTH_LONG).show();

        //start timer here;
        if (mTimer != null) {
            mTimer.cancel();
        }

        // re-schedule timer here
        // otherwise, IllegalStateException of
        // "TimerTask is scheduled already"
        // will be thrown
        mTimer = new Timer();
        mTimerTask = new BarolorTimerTask();

        // delay 100ms, repeat in 10000ms
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String            timerInterval = prefs.getString(getString(R.string.pressureMeasureInterval), "");
        long              intervalValue = 0;
        String[]          intervalValues = getResources().getStringArray(R.array.interval_array);

        try {
            if (timerInterval.contains(intervalValues[0])) {
                intervalValue = 15 * 60 * 1000;
            } else if (timerInterval.contains(intervalValues[1])) {
                intervalValue = 30 * 60 * 1000;
            } else if (timerInterval.contains(intervalValues[2])) {
                intervalValue = 60 * 60 * 1000;
            } else {
                intervalValue = 10000;
            }
        } catch (NullPointerException err) {
            intervalValue = DEFAULT_INTERVAL;
        }

        mTimer.schedule(mTimerTask, 100, intervalValue);

        return 0;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Barolog service has been stopped", Toast.LENGTH_LONG).show();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        //mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    public final void startSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            // Success! There's a barometer.
            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
        else {
            // Failure! No barometer.
        }

        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        DecimalFormat df = new DecimalFormat("###.##");
        float millibars_of_pressure = event.values[0];
        if (millibars_of_pressure > 0) {
            mSensorManager.unregisterListener(this);
        }

        //store value in DB:
        ContentValues newValues = new ContentValues();

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        newValues.put(DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN, dateFormat.format(date));
        newValues.put(DatabaseHelper.PRESSURE_VALUE_COLUMN, String.valueOf(df.format(millibars_of_pressure)));

        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE, null, newValues);
    }

    class BarolorTimerTask extends TimerTask {
        @Override
        public void run() {
            startSensor();
        }
    }

}

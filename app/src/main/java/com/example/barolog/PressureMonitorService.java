package com.example.barolog;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PressureMonitorService extends Service implements SensorEventListener {
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private Sensor mPressure;
    private SensorManager mSensorManager;
    private DatabaseHelper mDataBaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private AlarmManagerBroadCastReceiver alarmManager;

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
        alarmManager = new AlarmManagerBroadCastReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        //makeText(this, "Barolog service has been started", Toast.LENGTH_LONG).show();
        alarmManager.SetAlarm(getApplicationContext());

        startSensor();

        return 0;
    }


    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Barolog service has been stopped", Toast.LENGTH_LONG).show();

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mSqLiteDatabase != null) {
            mSqLiteDatabase = null;
        }

        if (mDataBaseHelper != null) {
            mDataBaseHelper = null;
        }
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

        stopSelf();
    }

}

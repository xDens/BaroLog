package com.example.barolog;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.barolog.constants.BasicConst;
import com.example.barolog.serviceTools.FileOperations;
import com.example.barolog.serviceTools.ServiceTools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener{
	private Button btnStartStop;
	private Button btnExportToCSV;
	private TextView txtSensorData;
	private Sensor mPressure;
	private SensorManager mSensorManager;
	private ListView viewValues;
	private ArrayAdapter mAdapter;

	private Button btnCheckDBState;
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDB;


    private double multiplier = 1.0;
    private String currentUnits = "";

	private ArrayList<String> values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtSensorData = (TextView) findViewById(R.id.txtSensorData);

		viewValues = (ListView) findViewById(R.id.viewValues);

		mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
		  // Success! There's a barometer.
			mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		  }
		else {
		  // Failure! No barometer.
		  }

		mDBHelper = new DatabaseHelper(this, DatabaseHelper.DATABASE_NAME, null, 1);
		mDB = mDBHelper.getReadableDatabase();
		
		//fill timer spinner with necessary data;																					
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.interval_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner cmbTimings = (Spinner) this.findViewById(R.id.cmbPeriod);
		cmbTimings.setAdapter(adapter);*/
		
		btnStartStop = (Button) this.findViewById(R.id.btnStartStop);
		btnStartStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnStartStop.getText() == getResources().getText(R.string.start) ) {
					btnStartStop.setText(R.string.stop);
					startService(new Intent(MainActivity.this, PressureMonitorService.class));
				} 
				else {
					btnStartStop.setText(R.string.start);
					stopService(new Intent(MainActivity.this, PressureMonitorService.class));
				}
			}
		});

		btnCheckDBState = (Button) this.findViewById(R.id.btnCheckDBState);
		btnCheckDBState.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				values = new ArrayList<String>();
				String query = "SELECT " + DatabaseHelper._ID + ", " + DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN + ", "
						+ DatabaseHelper.PRESSURE_VALUE_COLUMN + " FROM "
						+ DatabaseHelper.DATABASE_TABLE + " ORDER BY " + DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN + " DESC";

				Cursor cursor = mDB.rawQuery(query, null);


				while (cursor.moveToNext()) {
					String tmp = "";
					int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
					String time = cursor.getString(cursor
							.getColumnIndex(DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN));
					String value = cursor.getString(cursor
							.getColumnIndex(DatabaseHelper.PRESSURE_VALUE_COLUMN));
					tmp += "TIME: " + time + " VALUE: " + value + "\n";
					values.add(tmp);
				}

				mAdapter = new ArrayAdapter(MainActivity.this.getApplicationContext(), android.R.layout.simple_list_item_1, values);
				viewValues.setAdapter(mAdapter);
				cursor.close();

			}
		});

		btnExportToCSV = (Button) this.findViewById(R.id.btnCreateCsv);
		btnExportToCSV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String pressureTimeValues = "Time,Value\n";

				String query = "SELECT " + DatabaseHelper._ID + ", " + DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN + ", "
						+ DatabaseHelper.PRESSURE_VALUE_COLUMN + " FROM "
						+ DatabaseHelper.DATABASE_TABLE + " ORDER BY " + DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN + " DESC";

				Cursor cursor = mDB.rawQuery(query, null);


				while (cursor.moveToNext()) {
					int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
					String time = cursor.getString(cursor
							.getColumnIndex(DatabaseHelper.PRESSURE_MEASUREMENT_TIME_COLUMN));
					String value = cursor.getString(cursor
							.getColumnIndex(DatabaseHelper.PRESSURE_VALUE_COLUMN));

					pressureTimeValues += time + "," + value + "\n";
				}

				File fullpath;
				String foldername, filename;
				foldername = "barolog";
				filename = "barologPressureLog.csv";

				//Сохранение файла на External Storage:
					fullpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
						//+ "/" + foldername;
				if (FileOperations.isExternalStorageWritable())
				{
					FileOperations.SaveFile(fullpath, filename, MainActivity.this, pressureTimeValues);
				}
				else {
					Toast.makeText(MainActivity.this, "Storage is not accessible", Toast.LENGTH_LONG);
					Log.e("SaveFile", "Storage is not accessible");
				}
			}
		});
	}

	
	protected void onResume() {
	    // Register a listener for the sensor.
	    super.onResume();
	    mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentUnits = prefs.getString(getString(R.string.units), "");

        if (currentUnits.contains(BasicConst.MeasureUnits.MMHG)) {
            multiplier = BasicConst.MeasureUnits.MMHG_MULTIPLIER;
        }
        else if (currentUnits.contains(BasicConst.MeasureUnits.HPA)) {
            multiplier = 1.0;
        }
        else {
            multiplier = 1.0;
        }

		//check if service is already running;
		if (ServiceTools.isMyServiceRunning(PressureMonitorService.class, this.getApplicationContext())) {
			btnStartStop.setText(R.string.stop);
		} else {
			btnStartStop.setText(R.string.start);
		}
	}

	  @Override
	  protected void onPause() {
	    // Be sure to unregister the sensor when the activity pauses.
	    super.onPause();
	    mSensorManager.unregisterListener(this);
	  }
	  
	  @Override
	  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // Do something here if sensor accuracy changes.
	  }

	  @Override
	  public final void onSensorChanged(SensorEvent event) {
        DecimalFormat df = new DecimalFormat("###.##");
	    float millibars_of_pressure = event.values[0];
	    txtSensorData.setText(String.valueOf(df.format(millibars_of_pressure * multiplier)) + " " + currentUnits);
	  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
            Log.i("menu", "SETTINGS item has been clicked");
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
		}
        else if (id == R.id.action_about) {
            Log.i("menu", "ABOUT item has been clicked");
        }

		return super.onOptionsItemSelected(item);
	}

}

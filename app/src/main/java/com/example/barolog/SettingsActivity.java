package com.example.barolog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference dialogPreference = getPreferenceScreen().findPreference("clearDatabase");
        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder ad = null;
                ad = new AlertDialog.Builder(SettingsActivity.this);
                ad.setMessage("Are you sure?");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        mDatabaseHelper = new DatabaseHelper(SettingsActivity.this, DatabaseHelper.DATABASE_NAME, null, 1);
                        mDataBase = mDatabaseHelper.getWritableDatabase();

                        mDataBase.execSQL("DELETE FROM " + DatabaseHelper.DATABASE_TABLE);
                        mDataBase.close();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                       //nothing to do..
                    }
                });

                ad.show();
                return true;
            }
        });
    }

}
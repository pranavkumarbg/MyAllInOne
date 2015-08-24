package com.rhcloud.phpnew_pranavkumar.myallinone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pranav on 8/13/2015.
 */
public class MyDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MyDbHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "image.db";
    private static final int DATABASE_VERSION = 12;

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MyContract.MyEntry.TABLE_FLAVORS + "(" + MyContract.MyEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyContract.MyEntry.COLUMN_IMAGE+
                " VARCHAR(200) NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        //Log.i("DB","created");
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyContract.MyEntry.TABLE_FLAVORS);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MyContract.MyEntry.TABLE_FLAVORS + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}

package com.cutsquash.freezeup.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Justin on 26/11/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String TAG = DbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "freezer.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_DB = "CREATE TABLE " + Contract.TABLE_NAME + " (" +
                Contract._ID + " INTEGER PRIMARY KEY, " +
                Contract.COL_ITEM_NAME + " TEXT NOT NULL, " +
                Contract.COL_DATE + " INTEGER NOT NULL, " +
                Contract.COL_QUANTITY + " INTEGER NOT NULL, " +
                Contract.COL_IMAGE + " TEXT NOT NULL );";

        db.execSQL(CREATE_DB);
        Log.d(TAG, CREATE_DB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO deal with upgrade
    }
}

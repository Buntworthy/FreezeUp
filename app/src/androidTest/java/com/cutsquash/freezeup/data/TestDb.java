package com.cutsquash.freezeup.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by Justin on 28/11/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    // Test database creation
    public void testCreateDb() throws Throwable {

        Log.v(TAG, "Testing");

        DbHelper mDbHelper = new DbHelper(this.mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Get the master list of table names
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        final HashSet<String> tableName = new HashSet<String>();
        tableName.add(Contract.TABLE_NAME);

        do {
            tableName.remove(c.getString(0));
            Log.d(TAG, "Database: " + c.getString(0));
        } while(c.moveToNext());

        // verify that the correct table has been created
        assertTrue("Error: Table doesn't have the correct name.",
                tableName.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contract.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(Contract._ID);
        locationColumnHashSet.add(Contract.COL_DATE);
        locationColumnHashSet.add(Contract.COL_IMAGE);
        locationColumnHashSet.add(Contract.COL_ITEM_NAME);
        locationColumnHashSet.add(Contract.COL_QUANTITY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }
}

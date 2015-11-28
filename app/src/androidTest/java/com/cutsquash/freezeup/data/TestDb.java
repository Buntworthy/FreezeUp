package com.cutsquash.freezeup.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public void testAddItem() throws Throwable {

        DbHelper mDbHelper = new DbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.COL_ITEM_NAME, "Tasty meal");
        values.put(Contract.COL_DATE, System.currentTimeMillis());
        values.put(Contract.COL_QUANTITY, 2);
        values.put(Contract.COL_IMAGE, "path_to_image");

        long id = db.insert(Contract.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(id != -1);

        // Get the item back
        Cursor cursor = db.query(
                Contract.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from query", cursor.moveToFirst());

        // Check that the entries are as we expect
        Set<Map.Entry<String, Object>> valueSet = values.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);

            assertFalse("Column '" + columnName + "' not found. ", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " , expectedValue, cursor.getString(idx));

            Log.d(TAG, "Found " + columnName);

        }

        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
    }
}

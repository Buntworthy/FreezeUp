package com.cutsquash.freezeup.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Justin on 28/11/2015.
 */
public class TestUtilities extends AndroidTestCase {


    // Since we want each test to start with a clean slate
    static void deleteTheDatabase(Context mContext) {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);

    }

    static ContentValues createExampleValues() {
        ContentValues values = new ContentValues();
        values.put(Contract.COL_ITEM_NAME, "Tasty meal");
        values.put(Contract.COL_DATE, System.currentTimeMillis());
        values.put(Contract.COL_QUANTITY, 2);
        values.put(Contract.COL_IMAGE, "path_to_image");
        return values;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}

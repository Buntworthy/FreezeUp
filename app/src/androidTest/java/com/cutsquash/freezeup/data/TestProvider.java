package com.cutsquash.freezeup.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Justin on 28/11/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String TAG = TestProvider.class.getSimpleName();

    // Since we want each test to start with a clean slate
    public void setUp() {
        TestUtilities.deleteTheDatabase(mContext);
    }

    public void testInsertAndRead() {
        ContentValues values = TestUtilities.createExampleValues();
        Uri returnUri = getContext().getContentResolver()
                .insert(Contract.BASE_CONTENT_URI, values);

        long locationRowId = ContentUris.parseId(returnUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        Cursor c = getContext().getContentResolver()
                .query(Contract.BASE_CONTENT_URI,
                        null, null, null, null, null);
        TestUtilities.validateCursor(TAG + "-testInsertAndRead", c, values);
    }

}

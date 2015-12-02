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
        Log.d(TAG, "Deleting the database");
    }

    public void tearDown() {
        getContext().getContentResolver().
                acquireContentProviderClient(Contract.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }

    // Test a insert then read operation on the provider
    public void testInsertAndRead() {
        ContentValues values = TestUtilities.createExampleValues();
        Uri returnUri = mContext.getContentResolver()
                .insert(Contract.CONTENT_URI, values);

        long locationRowId = ContentUris.parseId(returnUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        Cursor c = mContext.getContentResolver()
                .query(Contract.CONTENT_URI,
                        null, null, null, null, null);
        TestUtilities.validateCursor(TAG + "-testInsertAndRead", c, values);
    }

    //TODO test update

    public void testDelete() {

        // Create two sets of values
        ContentValues valuesToDelete = TestUtilities.createExampleValues();
        ContentValues valuesToKeep = TestUtilities.createExampleValues();

        Uri uriToDelete = mContext.getContentResolver()
                .insert(Contract.CONTENT_URI, valuesToDelete);
        Uri uriToKeep = mContext.getContentResolver()
                .insert(Contract.CONTENT_URI, valuesToKeep);

        mContext.getContentResolver()
                .delete(uriToDelete, null, null);

        // try and get the uri we deleted
        Cursor c = mContext.getContentResolver()
                .query(uriToDelete,
                        null, null, null, null, null);
        assertFalse("Empty cursor not returned.", c.moveToFirst());
        c.close();

        // get the uri we wanted to keep
        c = mContext.getContentResolver()
                .query(uriToKeep,
                        null, null, null, null, null);
        TestUtilities.validateCursor(TAG + "-testDelete", c, valuesToKeep);
        c.close();
    }
}

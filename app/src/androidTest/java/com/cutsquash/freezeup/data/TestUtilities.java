package com.cutsquash.freezeup.data;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by Justin on 28/11/2015.
 */
public class TestUtilities {


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
}

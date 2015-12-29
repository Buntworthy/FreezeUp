package com.cutsquash.freezeup.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.cutsquash.freezeup.data.Contract;

/**
 * Created by Justin on 29/12/2015.
 */
public class DecrementListener implements View.OnClickListener {

    private final Context mContext;
    private final long itemId;

    public DecrementListener(Context context, long itemId){
        this.mContext = context;
        this.itemId = itemId;
    }

    public void onClick(View v) {
        // Get the current quantity value
        Uri thisUri = ContentUris.withAppendedId(Contract.CONTENT_URI, itemId);
        Cursor c = mContext.getContentResolver()
                .query(thisUri,
                        new String[]{Contract.COL_QUANTITY},
                        null, null, null, null);
        c.moveToFirst();
        int originalQuantity = c.getInt(c.getColumnIndex(Contract.COL_QUANTITY));
        if (originalQuantity > 1){
            // If >1 remaining update values
            int newQuantity = originalQuantity - 1;
            ContentValues values = new ContentValues();
            values.put(Contract.COL_QUANTITY, newQuantity);
            int updatedRows = mContext.getContentResolver().update(thisUri, values, null, null);
        } else {
            // If 1 remaining, ask to delete
            // TODO show confirmation dialog
            int updatedRows = mContext.getContentResolver().delete(thisUri, null, null);

        }
        c.close();
    }
}

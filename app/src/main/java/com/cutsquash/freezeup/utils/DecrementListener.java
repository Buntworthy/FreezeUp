package com.cutsquash.freezeup.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.cutsquash.freezeup.data.Contract;
import com.cutsquash.freezeup.dialogs.DeleteDialog;

/**
 * Created by Justin on 29/12/2015.
 */
public class DecrementListener
        implements
        View.OnClickListener,
        DeleteDialog.DeleteDialogListener {

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final long itemId;

    public DecrementListener(Context context, FragmentManager fragmentManager, long itemId){
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
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
            updateQuantity(thisUri, originalQuantity - 1);

        } else if (originalQuantity == 1 || originalQuantity == -1) {
            // If 1 remaining, ask to delete
            DeleteDialog dialog = new DeleteDialog();
            dialog.setListener(DecrementListener.this);
            dialog.show(mFragmentManager, "deleteConfirm");

        } else {
            // It is a level, (go toward zero by 1)
            updateQuantity(thisUri, originalQuantity + 1);
        }
        c.close();
    }

    @Override
    public void deleteItem() {
        Uri thisUri = ContentUris.withAppendedId(Contract.CONTENT_URI, itemId);
        int updatedRows = mContext.getContentResolver().delete(thisUri, null, null);
    }

    private int updateQuantity(Uri uri, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(Contract.COL_QUANTITY, newQuantity);
        return mContext.getContentResolver().update(uri, values, null, null);
    }
}

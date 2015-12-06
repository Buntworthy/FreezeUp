package com.cutsquash.freezeup;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.cutsquash.freezeup.data.Contract;

/**
 * Class to represent item data and save state
 */
public class Item implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = Item.class.getSimpleName();

    private static final int EDIT_ITEM_LOADER = 1;

    private ItemViewer mItemViewer;
    private Fragment mFragment;
    private Uri mUri;
    private String mName = "test";
    private long mDate = 1324354;
    private int mQuantity = 1;

    public Item(ItemViewer itemViewer, Fragment fragment, Uri uri) {

        // Store reference to ItemViewer which created this object
        mItemViewer = itemViewer;
        // Parent fragment
        mFragment = fragment;
        // Store the uri that refers to this item in the db
        mUri = uri;

    }

    public void loadItem() {

        // if the uri is not null, make the call to initialise the object
        if (mUri != null) {
            mFragment.getLoaderManager().initLoader(EDIT_ITEM_LOADER, null, this);
        } else {
            // if mUri is null we are creating a new item
            Log.d(TAG, "Creating new item");
            mName = null;
            mDate = System.currentTimeMillis();
            mQuantity = 0;
            mItemViewer.updateFields(this);
        }
    }

    public void save() {
        if (mUri == null) {
            ContentValues values = new ContentValues();
            values.put(Contract.COL_ITEM_NAME, mName);
            values.put(Contract.COL_DATE, mDate);
            values.put(Contract.COL_QUANTITY, mQuantity);
            values.put(Contract.COL_IMAGE, "Dummy");
            ContentResolver resolver = mFragment.getActivity().getContentResolver();
            mUri = resolver.insert(Contract.CONTENT_URI, values);
            resolver.notifyChange(mUri, null);

        } else {
            // use update instead
            ContentValues values = new ContentValues();
            values.put(Contract.COL_ITEM_NAME, mName);
            values.put(Contract.COL_DATE, mDate);
            values.put(Contract.COL_QUANTITY, mQuantity);
            values.put(Contract.COL_IMAGE, "Dummy");
            ContentResolver resolver = mFragment.getActivity().getContentResolver();
            int nRows = resolver.update(mUri, values, null, null);
            resolver.notifyChange(mUri, null);
        }
    }

    // Getters and setters

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getQuantityString() {
        return Integer.toString(mQuantity);
    }

    public String getDateString() {
        return Long.toString(mDate);
    }

    // Loader callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mFragment.getContext(),
                    mUri,
                    null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            mName = data.getString(data.getColumnIndex(Contract.COL_ITEM_NAME));
            mDate = data.getLong(data.getColumnIndex(Contract.COL_DATE));
            mQuantity = data.getInt(data.getColumnIndex(Contract.COL_QUANTITY));
        } else {
            Log.e(TAG, "No values from cursor");
        }

        // Update the item from the cursor
        mItemViewer.updateFields(this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

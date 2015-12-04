package com.cutsquash.freezeup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 * Class to represent item data and save state
 */
public class Item implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EDIT_ITEM_LOADER = 1;

    private ItemViewer mItemViewer;
    private Uri mUri;
    private String mName = "test";
    private long mDate = 1324354;
    private int mQuantity = 1;

    public Item(ItemViewer itemViewer, Uri uri) {

        // Store reference to ItemViewer which created this object
        mItemViewer = itemViewer;
        // Store the uri that refers to this item in the db
        mUri = uri;

    }

    public void loadItem() {

        // if the uri is not null, make the call to initialise the object
        if (mUri != null) {
            mItemViewer.getLoaderManager().initLoader(EDIT_ITEM_LOADER, null, this);
        }
    }

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
        return new CursorLoader(mItemViewer.getContext(),
                    mUri,
                    null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mItemViewer.updateFields(this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

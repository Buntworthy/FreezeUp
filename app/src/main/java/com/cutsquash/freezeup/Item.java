package com.cutsquash.freezeup;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.cutsquash.freezeup.data.Contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public String mImagePath = "Dummy";

    public boolean shouldSave = false;
    public boolean imageChanged = false;

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

    public void delete() {
        if (mUri != null) {
            ContentResolver resolver = mFragment.getActivity().getContentResolver();
            int nRows = resolver.delete(mUri, null, null);
            if (nRows != 1) {
                Log.e(TAG, "Unexpected number of item deleted");
            }
        }
    }

    public void close() {

        // Should the changes be saved?
        if (shouldSave) {
            if (mUri == null) {
                // We are adding and item
                ContentValues values = new ContentValues();
                values.put(Contract.COL_ITEM_NAME, mName);
                values.put(Contract.COL_DATE, mDate);
                values.put(Contract.COL_QUANTITY, mQuantity);

                // Add the image path if it exists.
                String imageString = "dummy";
                if (imageChanged) {

                    // Check if there is an image waiting to be saved
                    File src = new File(
                            mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            EditActivityFragment.TEMP_IMAGE_FILE);
                    if (src.exists()) {
                        // Save with a filename we store in the db
                        imageString = Long.toString(System.currentTimeMillis());
                        File dst = new File(
                                mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                imageString);
                        try {
                            copy(src, dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Delete the temporary image
                        src.delete();
                        values.put(Contract.COL_IMAGE, imageString);
                    }

                }
                mImagePath = imageString;
                values.put(Contract.COL_IMAGE, imageString);


                ContentResolver resolver = mFragment.getActivity().getContentResolver();
                mUri = resolver.insert(Contract.CONTENT_URI, values);
                resolver.notifyChange(mUri, null);

            } else {
                // We are updating an item
                ContentValues values = new ContentValues();
                values.put(Contract.COL_ITEM_NAME, mName);
                values.put(Contract.COL_DATE, mDate);
                values.put(Contract.COL_QUANTITY, mQuantity);

                // If the image has changed:
                String imageString = mImagePath;
                if (imageChanged) {
                    File src = new File(
                            mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            EditActivityFragment.TEMP_IMAGE_FILE);
                    if (src.exists()) {
                        // Save with a filename we store in the db
                        imageString = Long.toString(System.currentTimeMillis());
                        File dst = new File(
                                mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                imageString);
                        try {
                            copy(src, dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Delete the temporary image
                        src.delete();
                        // Delete the previous image
                        File oldImage = new File(
                                mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                mImagePath);
                        oldImage.delete();
                        values.put(Contract.COL_IMAGE, imageString);
                    }

                }
                // update the db
                mImagePath = imageString;
                values.put(Contract.COL_IMAGE, imageString);
                ContentResolver resolver = mFragment.getActivity().getContentResolver();
                int nRows = resolver.update(mUri, values, null, null);
                resolver.notifyChange(mUri, null);
            }
        }
    }


    // Getters and setters /////////////////////////////////////////////////////////////////////////

    // Getters /////
    public Uri getUri() {return mUri; }

    public String getName() {
        return mName;
    }

    public long getDate() {
        return mDate;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getQuantityString() {
        return Integer.toString(mQuantity);
    }

    public String getDateString() {
        return Long.toString(mDate);
    }

    public String getImagePath() {return mImagePath; }

    // Setters /////
    public void setName(String mName) {
        this.mName = mName;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }


    // Loader callbacks ////////////////////////////////////////////////////////////////////////////
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
            mImagePath = data.getString(data.getColumnIndex(Contract.COL_IMAGE));
        } else {
            Log.e(TAG, "No values from cursor");
        }

        // Update the item from the cursor
        mItemViewer.updateFields(this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}

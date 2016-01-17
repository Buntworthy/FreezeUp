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
import com.cutsquash.freezeup.utils.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * Class to represent item data and save state
 */
public class Item {

    public static final String TAG = Item.class.getSimpleName();
    public static final String PLACEHOLDER_IMAGE = "placeholder";

    public interface ItemDeletedListener {
        void itemDeleted();
    }


    private ItemViewer mItemViewer;
    private Fragment mFragment;
    private ItemDeletedListener mDeletedListener = null;
    private Uri mUri;
    private Long mId;
    private String mName = "test";
    private long mDate = 1324354;
    private int mQuantity = 1;
    private String mImagePath = PLACEHOLDER_IMAGE;
    private int mCategory = 0;
    private boolean mLevel = false; // units: false=number, true=level

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

        // if mUri is null we are creating a new item
        Log.d(TAG, "Creating new item");
        mName = null;
        mDate = System.currentTimeMillis();
        mQuantity = 0;
        mItemViewer.updateFields(this);
        mCategory = 0;
        mLevel = false;
    }

    public void loadItem(Cursor data) {

        if (data.moveToFirst()) {
            Log.d(TAG, "Setting item data");
            mId = data.getLong(data.getColumnIndex(Contract._ID));
            mName = data.getString(data.getColumnIndex(Contract.COL_ITEM_NAME));
            mDate = data.getLong(data.getColumnIndex(Contract.COL_DATE));
            mQuantity = data.getInt(data.getColumnIndex(Contract.COL_QUANTITY));
            if (mQuantity < 1) mLevel = true;
            mImagePath = data.getString(data.getColumnIndex(Contract.COL_IMAGE));
            mCategory = data.getInt(data.getColumnIndex(Contract.COL_CATEGORY));
        } else {
            // If we can't move to first then this item has been deleted
            // If we have a listener, inform it of the deletion
            if (mDeletedListener != null) {
                mDeletedListener.itemDeleted();
            }
        }
    }

    public void delete() {
        if (mUri != null) {
            // Delete image file if exists
            File imageFile = new File(
                    mFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    mImagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            // Delete from the database
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
                values.put(Contract.COL_CATEGORY, mCategory);

                // Add the image path if it exists.
                String imageString = PLACEHOLDER_IMAGE;
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
                            Utilities.copy(src, dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Delete the temporary image
                        src.delete();
                        values.put(Contract.COL_IMAGE, imageString);
                    }

                } else {
                    // use a default (category dependant image)
                    switch (mCategory) {
                        case 0:
                            imageString = "placeholder1";
                            break;
                        case 1:
                            imageString = "placeholder2";
                            break;
                        case 2:
                            imageString = "placeholder3";
                            break;
                        case 3:
                            imageString = "placeholder4";
                            break;
                        case 4:
                            imageString = "placeholder5";
                            break;
                        case 5:
                            imageString = "placeholder6";
                            break;
                    }
                }
                Log.d(TAG, "Selected image = " + imageString);
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
                values.put(Contract.COL_CATEGORY, mCategory);

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
                            Utilities.copy(src, dst);
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
        if (mQuantity >= 0) {
            return Integer.toString(mQuantity);
        } else {
            switch (mQuantity) {
                case -1:
                    return "Low";
                case -2:
                    return "Medium";
                case -3:
                    return "High";
                default:
                    return "Unkown";
            }
        }
    }

    public String getDateString() {
        return Utilities.formatDate(mDate);
    }

    public String getImagePath() { return mImagePath; }

    public int getCategory() { return mCategory; }

    public long getId() { return mId; }

    public boolean getUnit() { return mLevel; }

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

    public void setCategory(int mCategory) { this.mCategory = mCategory; }

    public void setDeletedListener(ItemDeletedListener listener) { this.mDeletedListener = listener; }

    public void setUnit(boolean level) { this.mLevel = level; }


}

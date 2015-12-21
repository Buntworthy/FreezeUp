package com.cutsquash.freezeup.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Justin on 26/11/2015.
 */
public class ItemProvider extends ContentProvider {

    public static final String TAG = ItemProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    private final static int ALL_ITEMS = 100;
    private final static int SINGLE_ITEM = 101;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.PATH_ITEMS, ALL_ITEMS);
        matcher.addURI(authority, Contract.PATH_ITEMS + "/#", SINGLE_ITEM);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case ALL_ITEMS:

                cursor = db.query(Contract.TABLE_NAME,
                            null, null, null, null, null, sortOrder);
                break;

            // If the incoming URI was for a single row
            case SINGLE_ITEM:
                // Get the id and use it as the selection
                cursor = db.query(Contract.TABLE_NAME, null,
                        Contract._ID + "= ?", new String[]{uri.getLastPathSegment()}, null, null, null);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long rowId = db.insert(Contract.TABLE_NAME, null, values);

        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int numRows = db.delete(Contract.TABLE_NAME,
                Contract._ID + "= ?",
                new String[]{uri.getLastPathSegment()});

        return numRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowId = db.update(Contract.TABLE_NAME,
                values,
                Contract._ID + "= ?",
                new String[]{uri.getLastPathSegment()});

        return rowId;
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}

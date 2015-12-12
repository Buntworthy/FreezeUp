package com.cutsquash.freezeup.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Justin on 26/11/2015.
 */
public class Contract implements BaseColumns {

    // URI definition for content provider
    public static final String AUTHORITY = "com.cutsquash.freezeup";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

    // Freezer items table
    public static final String TABLE_NAME = "items";

    // Table columns
    public static final String COL_ITEM_NAME = "name";
    public static final String COL_DATE = "date";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_IMAGE = "image";
    public static final String COL_CATEGORY = "category";

    // Categories
    public static final int CATEGORY_DEFAULT = 0;
    public static final int CATEGORY_MEAL = 1;
    public static final int CATEGORY_INGREDIENT = 2;
    public static final int CATEGORY_SIDE = 3;
    public static final int CATEGORY_SWEET = 4;
    public static final int CATEGORY_OTHER = 5;

}

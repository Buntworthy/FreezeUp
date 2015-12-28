package com.cutsquash.freezeup;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cutsquash.freezeup.data.Contract;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Justin on 28/11/2015.
 */
public class ItemAdapter extends CursorAdapter {

    public static final String TAG = ItemAdapter.class.getSimpleName();

    public ItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //TODO undestand why false is required here
        return inflater.inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final long itemId = cursor.getLong(cursor.getColumnIndex(Contract._ID));

        String itemName = cursor.getString(cursor.getColumnIndex(Contract.COL_ITEM_NAME));
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        nameView.setText(itemName);

        DateFormat df = DateFormat.getDateInstance();

        Long date = cursor.getLong(cursor.getColumnIndex(Contract.COL_DATE));
        String itemDate = df.format(new Date(date));
        TextView dateView = (TextView) view.findViewById(R.id.item_date);
        dateView.setText(itemDate);

        String itemImage = cursor.getString(cursor.getColumnIndex(Contract.COL_IMAGE));
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);

        File imageFile = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                itemImage);
        if (imageFile.exists()) {
            Glide.with(context).load(imageFile)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                    .into(imageView);
        } else {
            Glide.with(context).load(R.drawable.placeholder)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                    .into(imageView);
        }

        String itemQantity = Integer.toString(cursor.getInt(cursor.getColumnIndex(Contract.COL_QUANTITY)));
        Button quantityView = (Button) view.findViewById(R.id.item_quantity);
        quantityView.setText(itemQantity);
        quantityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked!" + Long.toString(itemId));
                // Get the current quantity value
                Uri thisUri = ContentUris.withAppendedId(Contract.CONTENT_URI, itemId);
                Cursor c = context.getContentResolver()
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
                    int updatedRows = context.getContentResolver().update(thisUri, values, null, null);
                    Log.d(TAG, "Updated " + Integer.toString(updatedRows));
                } else {
                    // If 1 remaining, ask to delete
                    // TODO show confirmation dialog
                    int updatedRows = context.getContentResolver().delete(thisUri, null, null);
                    Log.d(TAG, "Deleted " + Integer.toString(updatedRows));

                }
            }
        });

        int itemCategory = cursor.getInt(cursor.getColumnIndex(Contract.COL_CATEGORY));
        View categoryView = view.findViewById(R.id.item_category);
        switch (itemCategory) {
            case Contract.CATEGORY_DEFAULT:
                categoryView.setBackgroundColor(Color.BLUE);
                break;
            case Contract.CATEGORY_MEAL:
                categoryView.setBackgroundColor(Color.RED);
                break;
            case Contract.CATEGORY_INGREDIENT:
                categoryView.setBackgroundColor(Color.GREEN);
                break;
            case Contract.CATEGORY_SIDE:
                categoryView.setBackgroundColor(Color.MAGENTA);
                break;
            case Contract.CATEGORY_SWEET:
                categoryView.setBackgroundColor(Color.CYAN);
                break;
            case Contract.CATEGORY_OTHER:
                categoryView.setBackgroundColor(Color.GRAY);
                break;
            default:
                Log.e(TAG, "Unrecognised category");
                break;
        }


    }
}

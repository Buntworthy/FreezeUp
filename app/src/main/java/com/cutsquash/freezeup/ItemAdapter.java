package com.cutsquash.freezeup;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cutsquash.freezeup.utils.DecrementListener;
import com.cutsquash.freezeup.data.Contract;
import com.cutsquash.freezeup.utils.Utilities;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Justin on 28/11/2015.
 */
public class ItemAdapter extends CursorAdapter {

    public static final String TAG = ItemAdapter.class.getSimpleName();
    private Fragment mFragment;

    public ItemAdapter(Fragment fragment, Context context, Cursor c, int flags) {

        super(context, c, flags);
        this.mFragment = fragment;
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

        Long date = cursor.getLong(cursor.getColumnIndex(Contract.COL_DATE));
        String itemDate = Utilities.formatDate(date);
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

        String itemQantity = Integer.toString(cursor.getInt(
                cursor.getColumnIndex(Contract.COL_QUANTITY)));
        TextView quantityView = (TextView) view.findViewById(R.id.item_quantity_text);
        quantityView.setText(itemQantity);
        LinearLayout quantityViewButton = (LinearLayout) view.findViewById(R.id.item_quantity_button);
        quantityViewButton.setOnClickListener(
                new DecrementListener(context, mFragment.getFragmentManager(), itemId)
        );

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

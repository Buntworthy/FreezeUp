package com.cutsquash.freezeup;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cutsquash.freezeup.data.Contract;

/**
 * Created by Justin on 28/11/2015.
 */
public class ItemAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {

        String itemName = cursor.getString(cursor.getColumnIndex(Contract.COL_ITEM_NAME));
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        nameView.setText(itemName);

        String itemDate = Long.toString(cursor.getLong(cursor.getColumnIndex(Contract.COL_DATE)));
        TextView dateView = (TextView) view.findViewById(R.id.item_date);
        dateView.setText(itemDate);

        String itemImage = cursor.getString(cursor.getColumnIndex(Contract.COL_IMAGE));
        TextView imageView = (TextView) view.findViewById(R.id.item_image);
        imageView.setText(itemImage);

        String itemQantity = Integer.toString(cursor.getInt(cursor.getColumnIndex(Contract.COL_QUANTITY)));
        TextView quantityView = (TextView) view.findViewById(R.id.item_quantity);
        quantityView.setText(itemQantity);


    }
}

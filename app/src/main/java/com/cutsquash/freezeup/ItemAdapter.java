package com.cutsquash.freezeup;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cutsquash.freezeup.data.Contract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

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
            Picasso.with(context).load(imageFile).resize(200, 200)
                    .centerCrop().into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.placeholder).resize(200, 200)
                    .centerCrop().into(imageView);
        }

        String itemQantity = Integer.toString(cursor.getInt(cursor.getColumnIndex(Contract.COL_QUANTITY)));
        TextView quantityView = (TextView) view.findViewById(R.id.item_quantity);
        quantityView.setText(itemQantity);


    }
}

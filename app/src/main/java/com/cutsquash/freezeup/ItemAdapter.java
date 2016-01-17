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

    // Section headers from
    // http://cyrilmottier.com/2011/07/05/listview-tips-tricks-2-section-your-listview/

    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED = 1;
    private static final int STATE_REGULAR = 2;

    private Fragment mFragment;
    private int[] mCellStates;

    public ItemAdapter(Fragment fragment, Context context, Cursor c, int flags) {

        super(context, c, flags);
        this.mFragment = fragment;
        mCellStates = c == null ? null : new int[c.getCount()];
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
        return super.swapCursor(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //TODO undestand why false is required here
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
        return inflater.inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        boolean needSeparator = false;
        final int position = cursor.getPosition();
        final long itemId = cursor.getLong(cursor.getColumnIndex(Contract._ID));

        // Set separator
        int thisItemCategory = cursor.getInt(cursor.getColumnIndex(Contract.COL_CATEGORY));
        if (mCellStates != null) {
            switch (mCellStates[position]) {
                case STATE_SECTIONED:
                    needSeparator = true;
                    break;

                case STATE_REGULAR:
                    needSeparator = false;
                    break;

                case STATE_UNKNOWN:
                default:
                    // A separator is needed if it's the first itemview of the
                    // ListView or if the group of the current cell is different
                    // from the previous itemview.
                    if (position == 0) {
                        needSeparator = true;
                    } else {
                        cursor.moveToPosition(position - 1);

                        int prevItemCategory = cursor.getInt(cursor.getColumnIndex(Contract.COL_CATEGORY));
                        if (thisItemCategory - prevItemCategory != 0) {
                            needSeparator = true;
                        }

                        cursor.moveToPosition(position);
                    }

                    // Cache the result
                    mCellStates[position] = needSeparator ? STATE_SECTIONED : STATE_REGULAR;
                    break;
            }

            TextView separator = (TextView) view.findViewById(R.id.section);

            if (needSeparator) {
                separator.setText(
                        Utilities.getCategoryString(mFragment.getResources(), thisItemCategory)
                );
                separator.setVisibility(View.VISIBLE);
            } else {
                separator.setVisibility(View.GONE);
            }
        }

        // Set text fields
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

        String itemQantity = Utilities.quantityToShortString(cursor.getInt(
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

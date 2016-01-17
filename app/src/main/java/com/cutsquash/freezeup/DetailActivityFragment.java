package com.cutsquash.freezeup;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cutsquash.freezeup.utils.DecrementListener;
import com.cutsquash.freezeup.utils.Utilities;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment
        extends Fragment
        implements ItemViewer,
            Item.ItemDeletedListener,
            LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = DetailActivityFragment.class.getSimpleName();
    private static final int DETAIL_ITEM_LOADER = 1;
    private Item mItem;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class)
                        .setData(mItem.getUri());
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
            // We shouldn't view detail without a uri
            Log.e(TAG, "No existing item, adding new");
            mItem = new Item(this, this, null);
            mItem.loadItem();
        } else {
            Log.d(TAG, "Existing item present");
            mItem = new Item(this, this, intent.getData());
            getLoaderManager().initLoader(DETAIL_ITEM_LOADER, null, this);
            // Loader manager will call loadItem()
            Log.d(TAG, "Made item");
        }
        mItem.setDeletedListener(this);

        Log.d(TAG, "Finished onAcitivityCreated");
    }


    @Override
    public void updateFields(Item item) {

        Log.d(TAG, "updating fields");
        View rootView = getView();

        TextView nameView = (TextView) rootView.findViewById(R.id.detail_name);
        nameView.setText(item.getName());

        TextView dateView = (TextView) rootView.findViewById(R.id.detail_date);
        dateView.setText(item.getDateString());

        TextView categoryView = (TextView) rootView.findViewById(R.id.detail_category);
        categoryView.setText(
                Utilities.getCategoryString(getResources(), item.getCategory())
        );

        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);

        File imageFile = new File(
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                mItem.getImagePath());
        if (imageFile.exists()) {
            Log.d(TAG, "Loading existing image");
            Glide.with(this).load(imageFile)
                    .centerCrop().into(imageView);
        } else {
            Log.d(TAG, "No existing image");
            int id = getResources().getIdentifier(mItem.getImagePath(), "drawable", getContext().getPackageName());
            Glide.with(this).load(id)
                    .centerCrop().into(imageView);
        }

        Button decrementButton = (Button) rootView.findViewById(R.id.detail_quantity_button);
        decrementButton.setOnClickListener(
                new DecrementListener(getContext(), getFragmentManager(), item.getId())
        );
        decrementButton.setText(item.getQuantityString());
        Log.d(TAG, "Updated fields");
    }

    @Override
    public void itemDeleted() {
        getActivity().finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mItem.getUri(),
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished");
        mItem.loadItem(data);
        // Update the item from the cursor
        updateFields(mItem);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Data changed!");
    }
}

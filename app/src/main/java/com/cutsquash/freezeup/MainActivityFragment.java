package com.cutsquash.freezeup;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cutsquash.freezeup.data.Contract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

    private ItemAdapter mItemAdapter;
    private static final int ITEM_LOADER = 0;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mItemAdapter = new ItemAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(mItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start the edit activity
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                int itemId = c.getInt(c.getColumnIndex(Contract._ID));
                Intent intent = new Intent(getActivity(), EditActivity.class)
                        .setData(ContentUris.withAppendedId(Contract.BASE_CONTENT_URI, itemId));
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Contract.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemAdapter.swapCursor(null);
    }
}

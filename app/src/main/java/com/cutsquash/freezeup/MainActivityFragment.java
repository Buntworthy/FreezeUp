package com.cutsquash.freezeup;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.cutsquash.freezeup.data.Contract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivityFragment.class.getSimpleName();

    private ItemAdapter mItemAdapter;
    private static final int ITEM_LOADER = 0;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mItemAdapter = new ItemAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(mItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start the edit activity
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                int itemId = c.getInt(c.getColumnIndex(Contract._ID));
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .setData(ContentUris.withAppendedId(Contract.CONTENT_URI, itemId));
                Log.d(TAG, ContentUris.withAppendedId(Contract.CONTENT_URI, itemId).toString());
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
    public void onResume() {
        // Is this the best way?
        if(!getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().restartLoader(ITEM_LOADER, null, this);
        }
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = "";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean group = sharedPref.getBoolean(getString(R.string.pref_group), true);

        if (group) {
            sortOrder = Contract.COL_CATEGORY + " DESC, ";
        }
        sortOrder += sharedPref.getString(getString(R.string.pref_sortOrder), "name ASC");
        Log.d(TAG, sortOrder);

        return new CursorLoader(getActivity(),
                Contract.CONTENT_URI,
                null, null, null, sortOrder);
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

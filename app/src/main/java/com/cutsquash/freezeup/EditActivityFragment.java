package com.cutsquash.freezeup;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cutsquash.freezeup.data.Contract;
import com.cutsquash.freezeup.data.ItemProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = EditActivityFragment.class.getSimpleName();

    private static final int EDIT_ITEM_LOADER = 1;
    private Uri mUri;

    public EditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            // TODO temporary saving methods, could easily break
            // save to database
            View rootView = getView();
            // Get the text fields
            EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
            String name = nameView.getText().toString();

            EditText dateView = (EditText) rootView.findViewById(R.id.edit_date);
            Long date = Long.parseLong(dateView.getText().toString());

            EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
            int quantity = Integer.parseInt(quantityView.getText().toString());

            // Put into content values
            ContentValues values = new ContentValues();
            values.put(Contract.COL_ITEM_NAME, name);
            values.put(Contract.COL_DATE, date);
            values.put(Contract.COL_QUANTITY, quantity);

            // Commit to database
            if (mUri != null) {
                getActivity().getContentResolver().update(mUri, values, null, null);
                getActivity().getContentResolver().notifyChange(mUri, null);
            } else {
                // TODO Temporary image path
                values.put(Contract.COL_IMAGE, "image_path");
                Uri mUri = getActivity().getContentResolver().insert(Contract.CONTENT_URI, values);
                getActivity().getContentResolver().notifyChange(mUri, null);
            }

            // Go back to the home screen
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
            // Set up for adding new item
            Log.d(TAG, "No existing item, adding new");
            mUri = null;
        } else {
            getLoaderManager().initLoader(EDIT_ITEM_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null){ return null; } else {

            mUri = intent.getData();
            return new CursorLoader(getActivity(),
                    mUri,
                    null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        View rootView = getView();

        String itemName = data.getString(data.getColumnIndex(Contract.COL_ITEM_NAME));
        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        nameView.setText(itemName);

        String itemDate = Long.toString(data.getLong(data.getColumnIndex(Contract.COL_DATE)));
        EditText dateView = (EditText) rootView.findViewById(R.id.edit_date);
        dateView.setText(itemDate);

        String itemQantity = Integer.toString(data.getInt(data.getColumnIndex(Contract.COL_QUANTITY)));
        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        quantityView.setText(itemQantity);

        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

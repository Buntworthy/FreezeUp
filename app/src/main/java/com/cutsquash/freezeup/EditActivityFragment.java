package com.cutsquash.freezeup;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cutsquash.freezeup.data.Contract;
import com.cutsquash.freezeup.data.ItemProvider;

import java.text.DateFormat;
import java.util.Date;

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
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        // set the listener for the edit date view
        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");


            }
        });

        return rootView;
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

            EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
            int quantity = Integer.parseInt(quantityView.getText().toString());

            // Put into content values
            ContentValues values = new ContentValues();
            values.put(Contract.COL_ITEM_NAME, name);
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

        Date date = new Date(data.getLong(data.getColumnIndex(Contract.COL_DATE)));
        String itemDate = DateFormat.getDateInstance().format(date);
        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setText(itemDate);

        String itemQantity = Integer.toString(data.getInt(data.getColumnIndex(Contract.COL_QUANTITY)));
        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        quantityView.setText(itemQantity);

        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setDate(int year, int month, int day) {
        Toast.makeText(getActivity(),
                Integer.toString(year) + "/" +
                Integer.toString(month) + "/" +
                Integer.toString(day), Toast.LENGTH_SHORT).show();
    }
}

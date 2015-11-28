package com.cutsquash.freezeup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cutsquash.freezeup.data.Contract;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_ITEM_LOADER = 1;

    public EditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(EDIT_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null){ return null; } else {

            Uri intentUri = intent.getData();
            return new CursorLoader(getActivity(),
                    intentUri,
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

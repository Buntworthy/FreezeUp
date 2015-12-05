package com.cutsquash.freezeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
        implements ItemViewer {

    public static final String TAG = DetailActivityFragment.class.getSimpleName();

    private Item mItem;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
            // Set up for adding new item
            Log.d(TAG, "No existing item, adding new");
        } else {
            mItem = new Item(this, this, intent.getData());
        }
        mItem.loadItem();
        super.onActivityCreated(savedInstanceState);
    }



    public void setDate(int year, int month, int day) {
        Toast.makeText(getActivity(),
                Integer.toString(year) + "/" +
                        Integer.toString(month) + "/" +
                        Integer.toString(day), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFields(Item item) {

        View rootView = getView();

        TextView nameView = (TextView) rootView.findViewById(R.id.detail_name);
        nameView.setText(item.getName());

        TextView dateView = (TextView) rootView.findViewById(R.id.detail_date);
        dateView.setText(item.getDateString());

        TextView quantityView = (TextView) rootView.findViewById(R.id.detail_quantity);
        quantityView.setText(item.getQuantityString());


    }
}

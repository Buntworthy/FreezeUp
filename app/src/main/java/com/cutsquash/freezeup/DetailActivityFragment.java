package com.cutsquash.freezeup;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cutsquash.freezeup.data.Contract;
import com.cutsquash.freezeup.utils.DecrementListener;

import java.io.File;

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
        inflater.inflate(R.menu.menu_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), EditActivity.class)
                        .setData(mItem.getUri());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
            // We shouldn't view detail without a uri
            Log.e(TAG, "No existing item, adding new");
            mItem = new Item(this, this, null);
        } else {
            Log.d(TAG, "Existing item present");
            mItem = new Item(this, this, intent.getData());
        }
        mItem.loadItem();

        super.onActivityCreated(savedInstanceState);

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

        TextView categoryView = (TextView) rootView.findViewById(R.id.detail_category);
        // TODO Utility method
        int category = item.getCategory();
        String[] categoryNames = getResources().getStringArray(R.array.category_strings);
        categoryView.setText(categoryNames[category]);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);

        File imageFile = new File(
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                mItem.getImagePath());
        if (imageFile.exists()) {
            Log.d(TAG, "Loaading existing image");
            Glide.with(this).load(imageFile)
                    .centerCrop().into(imageView);
        } else {
            Log.d(TAG, "No existing image");
            Glide.with(this).load(R.drawable.placeholder)
                    .centerCrop().into(imageView);
        }

        Button decrementButton = (Button) rootView.findViewById(R.id.detail_quantity_button);
        decrementButton.setOnClickListener(
                new DecrementListener(getContext(), getFragmentManager(), item.getId())
        );
        decrementButton.setText(item.getQuantityString());

    }
}

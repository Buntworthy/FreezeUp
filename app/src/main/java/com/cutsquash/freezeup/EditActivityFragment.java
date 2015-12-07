package com.cutsquash.freezeup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.zip.Inflater;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditActivityFragment extends Fragment implements ItemViewer {

    public static final String TAG = EditActivityFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final String TEMP_IMAGE_FILE = "temp_image.jpg";

    private Item mItem;
    private Uri mfileUri;

    public EditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.edit_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Save the file to a temporary location in external storage, delete this when we
                // associate the file with an item
                File file = new File(
                        getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        TEMP_IMAGE_FILE);

                // If there is already a temporary file delete it so we can store a new one
                if (file.exists()) {
                    Log.d(TAG, "Deleting existing temporary image file");
                    file.delete();
                }

                // Use a Camera capture intent
                mfileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mfileUri);

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        return  rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Got a result");
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(getActivity(), "Got intent", Toast.LENGTH_LONG).show();

                ImageView imageView = (ImageView) getView().findViewById(R.id.edit_image);

                // Load image with picasso (invalidate to avoid chacheing problems)
                Picasso.with(getActivity()).invalidate(mfileUri);
                Picasso.with(getActivity()).load(mfileUri).resize(200, 200)
                        .centerCrop().into(imageView);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
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
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                getActivity().finish();
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

        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        nameView.setText(item.getName());

        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setText(item.getDateString());

        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        quantityView.setText(item.getQuantityString());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.edit_image);
        Picasso.with(getActivity()).load(R.drawable.placeholder).resize(200, 200)
                .centerCrop().into(imageView);
    }

    public void save() {
        // Get the text from the edit text fields and update the item
        View rootView = getView();

        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        mItem.setName(nameView.getText().toString());

        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        mItem.setQuantity(Integer.parseInt(quantityView.getText().toString()));

        mItem.save();
    }
}

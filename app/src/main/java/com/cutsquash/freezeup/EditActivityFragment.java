package com.cutsquash.freezeup;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cutsquash.freezeup.data.Contract;

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

        // Image click listener
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

        // Category click listener
        TextView categoryView = (TextView) rootView.findViewById(R.id.edit_category);
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new CategoryDialog();
                dialog.show(getFragmentManager(), "category");
            }
        });

        return  rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "Got a result");
            if (resultCode == Activity.RESULT_OK) {
                mItem.imageChanged = true;

                // TODO move to helper function
                ImageView imageView = (ImageView) getView().findViewById(R.id.edit_image);
                // Load image with picasso (invalidate to avoid chacheing problems)
                //Glide.with(this).invalidate(mfileUri);
                Glide.with(this).load(mfileUri).override(200, 200)
                        .centerCrop().into(imageView);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // TODO User cancelled the image capture
            } else {
                // Image capture failed
                Log.e(TAG, "Image capture failed");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                mItem.shouldSave = true;
                getTextFields();
                // Send the user back to the main activity
                // TODO should we go back to detail or main?
                Intent postSaveIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(postSaveIntent);
                return true;

            case R.id.action_delete:
                // TODO confirmation dialog
                mItem.delete();
                Intent postDeleteIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(postDeleteIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
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
    public void onPause() {
        Log.d(TAG, "Closing the item");
        mItem.close();
        super.onStop();
    }

    // Item View interface /////////////////////////////////////////////////////////////////////////

    @Override
    public void updateFields(Item item) {
        View rootView = getView();

        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        nameView.setText(item.getName());

        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setText(item.getDateString());

        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        quantityView.setText(item.getQuantityString());

        TextView categoryView = (TextView) rootView.findViewById(R.id.edit_category);
        // TODO Utility method
        int category = item.getCategory();
        String categoryText = null;
        switch (category) {
            case Contract.CATEGORY_DEFAULT:
                categoryText = getString(R.string.category_default);
                break;
            case Contract.CATEGORY_INGREDIENT:
                categoryText = getString(R.string.category_ingredient);
                break;
            case Contract.CATEGORY_MEAL:
                categoryText = getString(R.string.category_meal);
                break;
            case Contract.CATEGORY_SIDE:
                categoryText = getString(R.string.category_side);
                break;
            case Contract.CATEGORY_SWEET:
                categoryText = getString(R.string.category_sweet);
                break;
            case Contract.CATEGORY_OTHER:
                categoryText = getString(R.string.category_other);
                break;
            default:
                Log.e(TAG, "Unrecognised category");
                break;
        }
        categoryView.setText(categoryText);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.edit_image);
        File imageFile = new File(
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                mItem.getImagePath());
        if (imageFile.exists()) {
            Log.d(TAG, "Loading existing image");
            Glide.with(this).load(imageFile).override(200, 200)
                    .centerCrop().into(imageView);
        } else {
            Log.d(TAG, "No existing image");
            Glide.with(this).load(R.drawable.placeholder).override(200, 200)
                    .centerCrop().into(imageView);
        }
    }

    // Utility functions ///////////////////////////////////////////////////////////////////////////

    private void getTextFields() {
        // Get the text from the edit text fields and update the item
        View rootView = getView();

        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        mItem.setName(nameView.getText().toString());

        EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity);
        mItem.setQuantity(Integer.parseInt(quantityView.getText().toString()));

    }

    // Inner classes ///////////////////////////////////////////////////////////////////////////////
    private class CategoryDialog extends DialogFragment {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getContext(),
                    R.layout.dialog_category_item);
            arrayAdapter.add("Testing 1");
            arrayAdapter.add("Testing 2");
            arrayAdapter.add("Testing 3");
            arrayAdapter.add("Testing 4");
            arrayAdapter.add("Testing 5");

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "Clicked!" + Integer.toString(which));
                }
            });

            builder.setTitle("Select category");

            return builder.create();
        }
    }
}

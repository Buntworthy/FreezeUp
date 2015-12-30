package com.cutsquash.freezeup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.cutsquash.freezeup.dialogs.CategoryDialog;
import com.cutsquash.freezeup.dialogs.ImagePickerDialog;
import com.cutsquash.freezeup.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditActivityFragment extends Fragment
        implements ItemViewer,
        CategoryDialog.CategoryDialogListener,
        ImagePickerDialog.ImagePickerListener {

    public static final String TAG = EditActivityFragment.class.getSimpleName();

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static final String TEMP_IMAGE_FILE = "temp4_image.jpg";

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
                ImagePickerDialog dialog = new ImagePickerDialog();
                dialog.setImagePickerListener(EditActivityFragment.this);
                dialog.show(getFragmentManager(), "ImagePicker");
            }
        });

        // Category click listener
        TextView categoryView = (TextView) rootView.findViewById(R.id.edit_category);
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryDialog dialog = new CategoryDialog();
                dialog.setListener(EditActivityFragment.this);
                dialog.show(getFragmentManager(), "category");
            }
        });

        return  rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "Got camera image");
            mItem.imageChanged = true;

            Log.d(TAG, mfileUri.toString());
            // TODO move to helper function
            ImageView imageView = (ImageView) getView().findViewById(R.id.edit_image);
            Glide.with(this).load(mfileUri).override(200, 200)
                    .centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                    .into(imageView);

        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "Got gallery image");
            mItem.imageChanged = true;

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                File file = prepareTemporaryFile();
                mfileUri = Uri.fromFile(file);

                FileOutputStream fOut = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();

                ImageView imageView = (ImageView) getView().findViewById(R.id.edit_image);
                Glide.with(this).load(mfileUri).override(200, 200)
                        .centerCrop()
                        .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
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
        if (intent.getData() == null) {
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

    // Interface methods ///////////////////////////////////////////////////////////////////////////
    // Item Viewer interface ///////////////////////////////////////////////////////////////////////
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
        String[] categoryNames = getResources().getStringArray(R.array.category_strings);

        categoryView.setText(categoryNames[category]);

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

    // Category dialog listener interface //////////////////////////////////////////////////////////
    @Override
    public void categorySelected(int category) {
        mItem.setCategory(category);
        // Make sure the category field is updated
        updateFields(mItem);

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

    @Override
    public void imagePickerSelected(int choice) {
        Intent intent = new Intent();

        Log.d(TAG, "Image picker selected");
        // Save the file to a temporary location in external storage, delete this when we
        // associate the file with an item

        switch (choice) {
            case 0: // Camera
                File file = prepareTemporaryFile();
                mfileUri = Uri.fromFile(file);
                // create Intent to take a picture and return control to the calling application
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mfileUri);
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;

            case 1: // Gallery
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_ACTIVITY_REQUEST_CODE);
                break;

            default:
                Log.e(TAG, "Unregonised image picker choice");
                break;
        }

    }

    private File prepareTemporaryFile() {
        File file = new File(
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                TEMP_IMAGE_FILE);
        // If there is already a temporary file delete it so we can store a new one
        if (file.exists()) {
            Log.d(TAG, "Deleting existing temporary image file");
            boolean result = file.delete();
            Log.d(TAG, Boolean.toString(result));
        }

        return file;
    }


}

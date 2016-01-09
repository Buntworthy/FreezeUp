package com.cutsquash.freezeup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.cutsquash.freezeup.dialogs.CategoryDialog;
import com.cutsquash.freezeup.dialogs.DateDialog;
import com.cutsquash.freezeup.dialogs.ImagePickerDialog;
import com.cutsquash.freezeup.utils.BitmapWorkerTask;
import com.cutsquash.freezeup.utils.Utilities;

import java.io.File;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditActivityFragment extends Fragment
        implements ItemViewer,
        CategoryDialog.CategoryDialogListener,
        ImagePickerDialog.ImagePickerListener,
        DateDialog.DateDialogListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = EditActivityFragment.class.getSimpleName();

    private static final int EDIT_ITEM_LOADER = 2;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
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

        // Set up spinner
        Spinner spinner = (Spinner) rootView.findViewById(R.id.edit_quantity_level);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.quantity_level_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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

        // Date click listener
        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog();
                dialog.setListener(EditActivityFragment.this);
                dialog.show(getFragmentManager(), "date");
            }
        });

        // Save listener
        rootView.findViewById(R.id.done_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItem.shouldSave = true;
                getTextFields();
                getActivity().finish();

            }
        });

        // Quantity type click listener
        rootView.findViewById(R.id.Number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        rootView.findViewById(R.id.Level).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
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

            Uri srcUri = data.getData();
            File outputFile = prepareTemporaryFile();
            mfileUri = Uri.fromFile(outputFile);

            ImageView imageView = (ImageView) getView().findViewById(R.id.edit_image);

            BitmapWorkerTask task = new BitmapWorkerTask(this, srcUri, outputFile, imageView);
            task.execute();

            Glide.with(this).load(srcUri).override(200, 200)
                    .centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                    .into(imageView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.getData() == null){
            // We shouldn't view detail without a uri
            Log.e(TAG, "No existing item, adding new");
            mItem = new Item(this, this, null);
            mItem.loadItem();
        } else {
            Log.d(TAG, "Existing item present");
            mItem = new Item(this, this, intent.getData());
            getLoaderManager().initLoader(EDIT_ITEM_LOADER, null, this);
            // Loader manager will call loadItem()
            Log.d(TAG, "Made item");
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Closing the item");
        mItem.close();
        super.onPause();
    }

    // Interface methods ///////////////////////////////////////////////////////////////////////////
    // Item Viewer interface ///////////////////////////////////////////////////////////////////////
    @Override
    public void updateFields(Item item) {
        // TODO put in superclass?
        View rootView = getView();

        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        nameView.setText(item.getName());

        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setText(item.getDateString());

        if (mItem.getUnit() == false) {
            EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity_number);
            quantityView.setText(item.getQuantityString());
        }

        TextView categoryView = (TextView) rootView.findViewById(R.id.edit_category);
        categoryView.setText(
                Utilities.getCategoryString(getResources(), item.getCategory())
        );

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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        View numberView = getView().findViewById(R.id.edit_quantity_number);
        View levelView = getView().findViewById(R.id.edit_quantity_level);

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.Number:
                if (checked)
                    mItem.setUnit(false);
                    numberView.setVisibility(View.VISIBLE);
                    levelView.setVisibility(View.GONE);
                    break;
            case R.id.Level:
                if (checked)
                    mItem.setUnit(true);
                    numberView.setVisibility(View.GONE);
                    levelView.setVisibility(View.VISIBLE);
                    // Use spinner for level input
                    break;
        }
    }

    // Category dialog listener interface //////////////////////////////////////////////////////////
    @Override
    public void categorySelected(int category) {
        mItem.setCategory(category);
        // Make sure the category field is updated
        View rootView = getView();
        TextView categoryView = (TextView) rootView.findViewById(R.id.edit_category);
        categoryView.setText(
                Utilities.getCategoryString(getResources(), mItem.getCategory())
        );

    }

    // Utility functions ///////////////////////////////////////////////////////////////////////////

    private void getTextFields() {
        // Get the text from the edit text fields and update the item
        View rootView = getView();


        EditText nameView = (EditText) rootView.findViewById(R.id.edit_name);
        mItem.setName(nameView.getText().toString());

        if (mItem.getUnit() == false) {
            EditText quantityView = (EditText) rootView.findViewById(R.id.edit_quantity_number);
            mItem.setQuantity(Integer.parseInt(quantityView.getText().toString()));
        }

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

    @Override
    public void dateSelected(int year, int month, int day) {
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        mItem.setDate(date.getTimeInMillis());
        // update the UI
        View rootView = getView();
        TextView dateView = (TextView) rootView.findViewById(R.id.edit_date);
        dateView.setText(mItem.getDateString());

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,Integer.toString(-1 - position));
        mItem.setQuantity(-1 - position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mItem.getUri(),
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished");
        mItem.loadItem(data);
        // Update the item from the cursor
        updateFields(mItem);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Data changed!");
    }


}
